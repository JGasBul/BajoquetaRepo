package com.example.bajoquetaapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bajoquetaapp.R;
import com.example.bajoquetaapp.databinding.FragmentHomeBinding;
import com.example.bajoquetaapp.recipesAdapter;
import com.example.bajoquetaapp.recipesData;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Objects;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private recipesAdapter adapter;
    private ImageView switchLight;
    private boolean stateLight;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        switchStateLight();
        constructRV(root);
        return root;
    }

    private void switchStateLight() {
        switchLight = binding.switchLight;
        stateLight = false;
        switchLight.setOnClickListener(view -> {
            if (!stateLight) {
                switchLight.setImageResource(R.drawable.bulbon);
                stateLight = true;
                // Prueba conceptual, cuando se pueda iniciar sesion en la app de la bascula podremos aislar a los usuarios el led en la base de datos.
            } else {
                switchLight.setImageResource(R.drawable.bulboff);
                stateLight = false;
                // Prueba conceptual, cuando se pueda iniciar sesion en la app de la bascula podremos aislar a los usuarios el led en la base de datos.
            }
            FirebaseFirestore.getInstance().collection("LED").document("led").update("led", stateLight);
            FirebaseFirestore.getInstance().collection("users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).update("led", stateLight);
        });
    }

    private void constructRV(@NonNull View root) {
        RecyclerView recipesView = binding.rvHome;
        Query query = FirebaseFirestore.getInstance().collection("recipes").orderBy("nombre", Query.Direction.DESCENDING).limit(4);
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