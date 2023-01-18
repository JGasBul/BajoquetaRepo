package com.example.bajoquetaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class newUserActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText email, pass,checkPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        createAccount();
    }

    private void createAccount() {
        email = findViewById(R.id.newCorreo);
        pass = findViewById(R.id.newContraseña);
        checkPass = findViewById(R.id.checkContraseña);
        auth = FirebaseAuth.getInstance();
        Button register = findViewById(R.id.newUser);
        register.setOnClickListener(view -> {
            if (verificaCampos()){
                auth.createUserWithEmailAndPassword(String.valueOf(email.getText()), String.valueOf(pass.getText()))
                        .addOnCompleteListener(newUserActivity.this, task -> {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("loginEmail", "signInWithEmailAndPassword:success");
                                FirebaseUser user = auth.getCurrentUser();
                                newUserActivity.this.updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("loginEmail", "signInWithEmailAndPassword:failure", task.getException());
                                Toast.makeText(newUserActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                //authActivity.this.updateUI(null);
                            }
                        });
            }
        });
    }

    private boolean verificaCampos() {
        String emailString = email.getText().toString();
        String passString = pass.getText().toString();
        String checkPassString = checkPass.getText().toString();
        TextInputLayout tilEmail = findViewById(R.id.newTil_correo);
        tilEmail.setError("");
        TextInputLayout tilPass = findViewById(R.id.newTil_contraseña);
        tilPass.setError("");
        if (emailString.isEmpty()) {
            tilEmail.setError("Introduce un correo");
        } else if (!emailString.matches(".+@.+[.].+")) {
            tilEmail.setError("Correo no válido");
        } else if (passString.isEmpty()) {
            tilPass.setError("Introduce una contraseña");
        } else if (passString.length() < 6) {
            tilPass.setError("Ha de contener al menos 6 caracteres");
        } else if (!passString.matches(".*[0-9].*")) {
            tilPass.setError("Ha de contener un número");
        } else if (!passString.matches(checkPassString)) {
            tilPass.setError("Las dos contraseñas no coinciden");
        } else {
            return true;
        }
        return false;
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        }
    }
}