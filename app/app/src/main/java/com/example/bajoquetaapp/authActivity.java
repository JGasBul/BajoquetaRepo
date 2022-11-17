package com.example.bajoquetaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class authActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_SIGN_IN = 123;
    private FirebaseAuth mAuth;
    private String correo = "";
    private String contraseña = "";
    private ViewGroup contenedor;
    private EditText etCorreo, etContraseña;
    private TextInputLayout tilCorreo, tilContraseña;
    private ProgressDialog dialogo;
    private Button loginButton, registButton;
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        mAuth = FirebaseAuth.getInstance();
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
                        .setFilterByAuthorizedAccounts(true)
                        .build())
                // Automatically sign in when exactly one credential is retrieved.
                .setAutoSelectEnabled(true)
                .build();
        loginButton = findViewById(R.id.inicio_sesión);
        loginButton.setOnClickListener(this);
        registButton = findViewById(R.id.registro);
        registButton.setOnClickListener(this);
        etCorreo = (EditText) findViewById(R.id.correo);
        etContraseña = (EditText) findViewById(R.id.contraseña);
        tilCorreo = (TextInputLayout) findViewById(R.id.til_correo);
        tilContraseña = (TextInputLayout) findViewById(R.id.til_contraseña);
        contenedor = (ViewGroup) findViewById(R.id.authLayout);
        dialogo = new ProgressDialog(this);
        dialogo.setTitle("Verificando usuario");
        dialogo.setMessage("Por favor espere...");
        //verificaSiUsuarioValidado();
        //login();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            //reload();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.inicio_sesión:
                signIn(etCorreo.getText().toString(), etContraseña.getText().toString());
                break;
            case R.id.registro:
                createAccount(etCorreo.getText().toString(), etContraseña.getText().toString());
                break;
        }
    }

    private void signIn(String email, String password) {
        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
        // [END sign_in_with_email]
    }

    private void createAccount(String email, String password) {
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
        // [END create_user_with_email]
    }

    private void updateUI(FirebaseUser user) {
        if (user != null){
            Intent i = new Intent(this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        }
    }


    /*
            private void verificaSiUsuarioValidado() {
                if (auth.getCurrentUser() != null) {
                    Intent i = new Intent(this, homeActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish();
                }
            }
        */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                if (null != currentUser) {
                    if ("password".equals(currentUser.getProviderData().get(1).getProviderId())) {
                        if (!currentUser.isEmailVerified()) {
                            /* Send Verification Email */
                            currentUser.sendEmailVerification()
                                    .addOnCompleteListener(this, new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {
                                            /* Check Success */
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getApplicationContext(),
                                                        "Verification Email Sent To: " + currentUser.getEmail(),
                                                        Toast.LENGTH_SHORT).show();
                                            } else {
                                                Log.e("TAG", "sendEmailVerification", task.getException());
                                                Toast.makeText(getApplicationContext(),
                                                        "Failed To Send Verification Email!",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                            /* Handle Case When Email Not Verified */
                            Toast.makeText(getApplicationContext(),
                                    "Verifique su cuenta accediendo al correo. Busque en spam si no encuentra el correo",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                    /* Login Success */
                    login();
                } else {
                    String s = "";
                    IdpResponse response = IdpResponse.fromResultIntent(data);
                    if (response == null) s = "Cancelado";
                    else switch (Objects.requireNonNull(response.getError()).getErrorCode()) {
                        case ErrorCodes.NO_NETWORK:
                            s = "Sin conexión a Internet";
                            break;
                        case ErrorCodes.PROVIDER_ERROR:
                            s = "Error en proveedor";
                            break;
                        case ErrorCodes.DEVELOPER_ERROR:
                            s = "Error desarrollador";
                            break;
                        default:
                            s = "Otros errores de autentificación";
                        case ErrorCodes.ANONYMOUS_UPGRADE_MERGE_CONFLICT:
                        case ErrorCodes.EMAIL_LINK_CROSS_DEVICE_LINKING_ERROR:
                        case ErrorCodes.EMAIL_LINK_DIFFERENT_ANONYMOUS_USER_ERROR:
                        case ErrorCodes.EMAIL_LINK_PROMPT_FOR_EMAIL_ERROR:
                        case ErrorCodes.EMAIL_LINK_WRONG_DEVICE_ERROR:
                        case ErrorCodes.EMAIL_MISMATCH_ERROR:
                        case ErrorCodes.ERROR_GENERIC_IDP_RECOVERABLE_ERROR:
                        case ErrorCodes.ERROR_USER_DISABLED:
                        case ErrorCodes.INVALID_EMAIL_LINK_ERROR:
                        case ErrorCodes.PLAY_SERVICES_UPDATE_CANCELLED:
                        case ErrorCodes.UNKNOWN_ERROR:
                            break;
                    }
                    Toast.makeText(this, s, Toast.LENGTH_LONG).show();
                }
            }
        }
    }
/*
    public void inicioSesiónCorreo() {
        if (verificaCampos()) {
            dialogo.show();
            auth.signInWithEmailAndPassword(correo, contraseña).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        verificaSiUsuarioValidado();
                    } else {
                        dialogo.dismiss();
                        mensaje(task.getException().getLocalizedMessage());
                    }
                }
            });
        }
    }
*/
    /*
    public void registroCorreo(View v) {
        if (verificaCampos()) {
            dialogo.show();
            mAauth.createUserWithEmailAndPassword(correo, contraseña).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        //verificaSiUsuarioValidado();
                    } else {
                        dialogo.dismiss();
                        mensaje(task.getException().getLocalizedMessage());
                    }
                }
            });
        }
    }
    */

    private void mensaje(String mensaje) {
        Snackbar.make(contenedor, mensaje, Snackbar.LENGTH_LONG).show();
    }

    private boolean verificaCampos() {
        correo = etCorreo.getText().toString();
        contraseña = etContraseña.getText().toString();
        tilCorreo.setError("");
        tilContraseña.setError("");
        if (correo.isEmpty()) {
            tilCorreo.setError("Introduce un correo");
        } else if (!correo.matches(".+@.+[.].+")) {
            tilCorreo.setError("Correo no válido");
        } else if (contraseña.isEmpty()) {
            tilContraseña.setError("Introduce una contraseña");
        } else if (contraseña.length() < 6) {
            tilContraseña.setError("Ha de contener al menos 6 caracteres");
        } else if (!contraseña.matches(".*[0-9].*")) {
            tilContraseña.setError("Ha de contener un número");
        } else {
            return true;
        }
        return false;
    }

    private void login() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AuthMethodPickerLayout customLayout = new AuthMethodPickerLayout
                .Builder(R.layout.activity_auth)
                .setGoogleButtonId(R.id.googleButton)
                //.setEmailButtonId(R.id.registerButton)
                .setPhoneButtonId(R.id.phoneButton)
                //.setTosAndPrivacyPolicyId(R.id.baz)
                .build();
        if (user != null) {
            Toast.makeText(this, "Iniciada sesion: " + user.getDisplayName() +
                    " - " + user.getEmail(), Toast.LENGTH_LONG).show();
            Intent i = new Intent(this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        } else {
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    //new AuthUI.IdpConfig.EmailBuilder().build(),
                    new AuthUI.IdpConfig.GoogleBuilder().build(),
                    new AuthUI.IdpConfig.PhoneBuilder().build());
            startActivityForResult(
                    AuthUI.getInstance().createSignInIntentBuilder()
                            .setAuthMethodPickerLayout(customLayout)
                            .setAvailableProviders(providers)
                            .setIsSmartLockEnabled(false)
                            .setTheme(R.style.Theme_BajoquetaApp)
                            .build(),
                    RC_SIGN_IN);
        }
    }

}
