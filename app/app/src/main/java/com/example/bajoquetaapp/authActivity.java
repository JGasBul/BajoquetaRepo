package com.example.bajoquetaapp;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class authActivity extends AppCompatActivity {

    private static final int REQ_ONE_TAP = 2;

    private FirebaseAuth mAuth;
    private EditText email, pass;

    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        createOneTap();
        createEmailFunction();
        createGoogleFunction();
    }

    private void createOneTap() {
        oneTapClient = Identity.getSignInClient(this);
        signInRequest = BeginSignInRequest.builder()
                .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                        .setSupported(true)
                        .build())
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(getString(R.string.default_web_client_id))
                        // Only show accounts previously used to sign in.
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                // Automatically sign in when exactly one credential is retrieved.
                .setAutoSelectEnabled(true)
                .build();
    }

    private void createGoogleFunction() {
        Button google = findViewById(R.id.googleButton);
        Button newAcc = findViewById(R.id.registro);
        google.setOnClickListener(view -> oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(authActivity.this, result -> {
                    try {
                        startIntentSenderForResult(
                                result.getPendingIntent().getIntentSender(), REQ_ONE_TAP,
                                null, 0, 0, 0);
                    } catch (IntentSender.SendIntentException e) {
                        Log.e("OneTap", "Couldn't start One Tap UI: " + e.getLocalizedMessage());
                    }
                })
                .addOnFailureListener(authActivity.this, e -> {
                    // No Google Accounts found. Just continue presenting the signed-out UI.
                    Log.d("OneTap", e.getLocalizedMessage());
                }));
        newAcc.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), newUserActivity.class);
            startActivity(intent);
        });
    }

    private void createEmailFunction() {
        mAuth = FirebaseAuth.getInstance();
        Button login = findViewById(R.id.inicio_sesión);

        email = findViewById(R.id.correo);
        pass = findViewById(R.id.contraseña);
        login.setOnClickListener(view -> {
            if (verificaCampos()) {
                mAuth.signInWithEmailAndPassword(String.valueOf(email.getText()), String.valueOf(pass.getText()))
                        .addOnCompleteListener(authActivity.this, task -> {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("loginEmail", "signInWithEmailAndPassword:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                authActivity.this.updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("loginEmail", "signInWithEmailAndPassword:failure", task.getException());
                                Toast.makeText(authActivity.this, "Authentication failed.",
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
        TextInputLayout tilEmail = findViewById(R.id.til_correo);
        tilEmail.setError("");
        TextInputLayout tilPass = findViewById(R.id.til_contraseña);
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
        } else {
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_ONE_TAP) {
            try {
                SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                String idToken = credential.getGoogleIdToken();
                if (idToken != null) {
                    // Got an ID token from Google. Use it to authenticate
                    // with Firebase.
                    AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
                    mAuth.signInWithCredential(firebaseCredential)
                            .addOnCompleteListener(this, task -> {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("signInWithCredential", "signInWithCredential:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("signInWithCredential", "signInWithCredential:failure", task.getException());
                                    updateUI(null);
                                }
                            });
                }
            } catch (ApiException e) {
                switch (e.getStatusCode()) {
                    case CommonStatusCodes.CANCELED:
                        Log.d("oneTapError", "One-tap dialog was closed.");
                        // Don't re-prompt the user.
                        boolean showOneTapUI = false;
                        break;
                    case CommonStatusCodes.NETWORK_ERROR:
                        Log.d("oneTapError", "One-tap encountered a network error.");
                        // Try again or just ignore.
                        break;
                    default:
                        Log.d("oneTapError", "Couldn't get credential from result."
                                + e.getLocalizedMessage());
                        break;
                }
            }
        }
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        }
    }
}
