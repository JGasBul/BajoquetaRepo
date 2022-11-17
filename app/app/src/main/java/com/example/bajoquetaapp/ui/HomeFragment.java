package com.example.bajoquetaapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.bajoquetaapp.R;
import com.example.bajoquetaapp.authActivity;
import com.example.bajoquetaapp.databinding.FragmentHomeBinding;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    TextView welcome, datos;
    FirebaseUser currentUser;
    Button logOut;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        /*
        welcome = binding.textView;
        //logOut = binding.logOutButton;
        welcome.setText(String.format("%s %s", getString(R.string.bienvenido), currentUser.getDisplayName()));

        datos = binding.infoUser;
        datos.setText(String.format("Sus datos son: \nNombre: %s\nCorreo: %s\nProveedor: %s\nUID: %s"
                , currentUser.getDisplayName(), currentUser.getEmail(), currentUser.getProviderData(), currentUser.getUid()));

        logOut.setOnClickListener(view -> AuthUI.getInstance()
                .signOut(root.getContext())
                .addOnCompleteListener(task -> {
                    // user is now signed out
                    startActivity(new Intent(root.getContext(), authActivity.class));
                    getActivity().finish();
                }));
        */
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}