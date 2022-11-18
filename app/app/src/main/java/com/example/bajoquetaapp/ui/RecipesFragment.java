package com.example.bajoquetaapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import com.example.bajoquetaapp.MainActivity;
import com.example.bajoquetaapp.authActivity;
import com.example.bajoquetaapp.databinding.FragmentRecipesBinding;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class RecipesFragment extends Fragment  {

    private FragmentRecipesBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentRecipesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        //final TextView textView = binding.textNotifications;
        Button logOut = binding.logOut;
        logOut.setOnClickListener(view -> AuthUI.getInstance()
                .signOut(binding.getRoot().getContext())
                .addOnCompleteListener(task -> {
                    // user is now signed out
                    startActivity(new Intent(binding.getRoot().getContext(), authActivity.class));
                    getActivity().finish();
                }));
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}