package com.example.bajoquetaapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bajoquetaapp.databinding.FragmentRecipesBinding;
import com.example.bajoquetaapp.recipesAdapter;
import com.example.bajoquetaapp.recipesData;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class RecipesFragment extends Fragment {

    private FragmentRecipesBinding binding;
    public recipesAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentRecipesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        //final TextView textView = binding.textNotifications;

        constructRV(root);
        return root;
    }

    private void constructRV(@NonNull View root) {
        RecyclerView recipesView = binding.recyclerRecipes;
        Query query = FirebaseFirestore.getInstance().collection("recipes").orderBy("nombre", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<recipesData> options = new FirestoreRecyclerOptions.Builder<recipesData>().setQuery(query, recipesData.class).build();
        adapter = new recipesAdapter(root.getContext(), options);
        recipesView.setAdapter(adapter);
        recipesView.setLayoutManager(new LinearLayoutManager(root.getContext()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}