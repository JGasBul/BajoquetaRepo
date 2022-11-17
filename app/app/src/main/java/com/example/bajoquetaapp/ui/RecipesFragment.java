package com.example.bajoquetaapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import com.example.bajoquetaapp.databinding.FragmentRecipesBinding;

public class RecipesFragment extends Fragment {

    private FragmentRecipesBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentRecipesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        //final TextView textView = binding.textNotifications;
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}