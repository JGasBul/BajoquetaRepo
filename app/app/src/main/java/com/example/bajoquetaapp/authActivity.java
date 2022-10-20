package com.example.bajoquetaapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;
import java.util.List;

public class authActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_auth);
        login();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                login();
                //finish(); //Quitar
            } else {
                String s = "";
                IdpResponse response = IdpResponse.fromResultIntent(data);
                if (response == null) s = "Cancelado";
                else switch (response.getError().getErrorCode()) {
                    case ErrorCodes.NO_NETWORK: s="Sin conexión a Internet"; break;
                    case ErrorCodes.PROVIDER_ERROR: s="Error en proveedor"; break;
                    case ErrorCodes.DEVELOPER_ERROR: s="Error desarrollador"; break;
                    default: s="Otros errores de autentificación";
                }
                Toast.makeText(this, s, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void login() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AuthMethodPickerLayout customLayout = new AuthMethodPickerLayout
                .Builder(R.layout.activity_auth)
                .setGoogleButtonId(R.id.bar)
                .setEmailButtonId(R.id.foo)
                //.setTosAndPrivacyPolicyId(R.id.baz)
                .build();
        if (user != null) {
            Toast.makeText(this, "Iniciada sesion: "+user.getDisplayName()+
                    " - "+ user.getEmail(),Toast.LENGTH_LONG).show();
            Intent i = new Intent(this, homeActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        } else {
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.EmailBuilder().build(),
                    new AuthUI.IdpConfig.GoogleBuilder().build());
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
