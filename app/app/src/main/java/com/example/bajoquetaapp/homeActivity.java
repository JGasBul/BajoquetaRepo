package com.example.bajoquetaapp;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class homeActivity extends AppCompatActivity {

    TextView welcome, datos;
    FirebaseUser currentUser;
    Button logOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        welcome = findViewById(R.id.welcomeString);
        logOut = findViewById(R.id.logOutButton);
        welcome.setText(String.format("%s %s", getString(R.string.bienvenido), currentUser.getDisplayName()));
        datos = findViewById(R.id.infoUser);
        datos.setText(String.format("Sus datos son: \nNombre: %s\nCorreo: %s\nProveedor: %s\nUID: %s"
                , currentUser.getDisplayName(), currentUser.getEmail(), currentUser.getProviderData(), currentUser.getUid()));
    logOut.setOnClickListener(view -> AuthUI.getInstance()
            .signOut(homeActivity.this)
            .addOnCompleteListener(task -> {
                // user is now signed out
                startActivity(new Intent(homeActivity.this, authActivity.class));
                finish();
            }));
    }

}