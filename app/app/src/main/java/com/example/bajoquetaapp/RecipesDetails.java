package com.example.bajoquetaapp;


import android.content.Intent;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirestoreRegistrar;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RecipesDetails extends AppCompatActivity {

    private ImageView favButton;
    private boolean stateFav;
    private Map<String, Object> response;
    private List<String> userFavs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes_details);
        Intent i = getIntent();
        constructView(i);
        favFunction();
    }

    private void favFunction() {
        userFavs = new ArrayList<>();
        favButton = findViewById(R.id.favBtn);
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        response = task.getResult().getData();
                        userFavs = (List<String>) response.get("favRecipes");
                        stateFav = false;
                        if (userFavs != null) {
                            for (String userFav : userFavs) {
                                if (userFav.matches(getIntent().getStringExtra("uuid"))) {
                                    stateFav = true;
                                    favButton.setImageResource(R.drawable.favon);
                                }
                            }
                        }
                        Log.d("userFav", String.valueOf(userFavs));
                    }
                });
        favButton.setOnClickListener(view -> {
            if (!stateFav) {
                favButton.setImageResource(R.drawable.favon);
                FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                        .update("favRecipes", FieldValue.arrayUnion(getIntent().getStringExtra("uuid")));
            } else {
                favButton.setImageResource(R.drawable.favoff);
                stateFav = false;
                FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                        .update("favRecipes", FieldValue.arrayRemove(getIntent().getStringExtra("uuid")));
            }
        });
    }

    private void constructView(Intent i) {
        String nombre = i.getStringExtra("name");
        String descripcion = i.getStringExtra("description");
        String ingredientes = i.getStringExtra("description");
        String preparacion = i.getStringExtra("description");
        String foto = i.getStringExtra("foto");
        String hidratos = i.getStringExtra("hidratos");
        String proteinas = i.getStringExtra("proteinas");
        String grasas = i.getStringExtra("grasas");

        TextView nombreReceta = findViewById(R.id.nombreReceta);
        ImageView img = findViewById(R.id.imageView3);
        TextView resumenTextView = (TextView) findViewById(R.id.resumenTextView);
        TextView ingredientesTextView = (TextView) findViewById(R.id.ingredientesTextView);
        TextView preparacionTextView = (TextView) findViewById(R.id.preparacionTextView);

        //---------------------------------------------------------
        ProgressBar barraHidratos = findViewById(R.id.barraHidratos);
        TextView hidratosPorcentaje = findViewById(R.id.hidratosPorcentaje);
        ProgressBar barraProteinas = findViewById(R.id.barraProteinas);
        TextView proteinasPorcentaje = findViewById(R.id.proteinasPorcentaje);
        ProgressBar barraGrasas = findViewById(R.id.barraGrasas);
        TextView grasasPorcentaje = findViewById(R.id.grasasPorcentaje);
        //---------------------------------------------------------

        nombreReceta.setText(nombre);
        nombreReceta.setPadding(6, 6, 20, 6);

        barraHidratos.setProgress(Integer.parseInt(hidratos));
        barraProteinas.setProgress(Integer.parseInt(proteinas));
        barraGrasas.setProgress(Integer.parseInt(grasas));

        hidratosPorcentaje.setText(hidratos + "g");
        proteinasPorcentaje.setText(proteinas + "g");
        grasasPorcentaje.setText(grasas + "g");

        Glide.with(this.getApplicationContext()).load(foto).into(img);
        resumenTextView.setText(descripcion);
        ingredientesTextView.setText(ingredientes);
        preparacionTextView.setText(preparacion);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
    }
}