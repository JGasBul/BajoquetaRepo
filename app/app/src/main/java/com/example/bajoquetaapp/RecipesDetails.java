package com.example.bajoquetaapp;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class RecipesDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes_details);
        Intent i = getIntent();

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
        nombreReceta.setPadding(6,6,20,6);

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
        startActivity(new Intent(this,MainActivity.class));
    }
}