package com.example.bajoquetaapp;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class authActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        login();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode,Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
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

    private void login() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AuthMethodPickerLayout customLayout = new AuthMethodPickerLayout
                .Builder(R.layout.activity_auth)
                .setGoogleButtonId(R.id.googleButton)
                .setEmailButtonId(R.id.registerButton)
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
