package com.example.bajoquetaapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bajoquetaapp.RecipesDetails;
import com.example.bajoquetaapp.databinding.FragmentRecipesBinding;
import com.example.bajoquetaapp.recipesAdapter;
import com.example.bajoquetaapp.recipesData;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class RecipesFragment extends Fragment {

    private FragmentRecipesBinding binding;
    public recipesAdapter adaptador;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentRecipesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        //final TextView textView = binding.textNotifications;
        RecyclerView recipesView = binding.recyclerRecipes;
        Query query = FirebaseFirestore.getInstance().collection("prueba").orderBy("nombre",Query.Direction.DESCENDING).limit(25);
        FirestoreRecyclerOptions<recipesData> opciones = new FirestoreRecyclerOptions.Builder<recipesData>().setQuery(query, recipesData.class).build();
        adaptador = new recipesAdapter(root.getContext(), opciones);
        /*
        adaptador.setOnItemClickListener(new recipesAdapter.IClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                String model = dataSource.get(position);

            }
        });
         */

        /*
        adaptador.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), RecipesDetails.class);
                startActivity(intent);
            }
        });
         */
        recipesView.setAdapter(adaptador);
        recipesView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    @Override
    public void onStart() {
        super.onStart();
        adaptador.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adaptador.stopListening();
    }
}