package com.example.bajoquetaapp.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bajoquetaapp.authActivity;
import com.example.bajoquetaapp.databinding.FragmentUserBinding;
import com.example.bajoquetaapp.recipesAdapter;
import com.example.bajoquetaapp.recipesData;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;


public class UserFragment extends Fragment {

    private FragmentUserBinding binding;
    ArrayList<BarEntry> barArrayList;
    private recipesAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentUserBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        constructBars();
        constructRV(root);
        logOut();

        return root;
    }

    private void constructRV(@NonNull View root) {
        RecyclerView recipesView = binding.rvUser;
        Query query = FirebaseFirestore.getInstance().collection("recipes").orderBy("nombre", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<recipesData> options = new FirestoreRecyclerOptions.Builder<recipesData>().setQuery(query, recipesData.class).build();
        adapter = new recipesAdapter(root.getContext(), options);
        recipesView.setAdapter(adapter);
        recipesView.setLayoutManager(new LinearLayoutManager(root.getContext()));
    }


    private void logOut() {
        ImageView logout = binding.logout;
        logout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), authActivity.class);
            startActivity(intent);
        });
    }

    private void constructBars() {
        //Start Graphic Bar
        BarChart barChart = binding.barchart;
        getData();
        BarDataSet barDataSet = new BarDataSet(barArrayList, "");
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        //color bar data set
        //One color for each index. If there're not enought colors, it will start since the first color setted
        barDataSet.setColors(Color.CYAN, Color.GREEN, Color.RED, Color.GREEN, Color.GREEN, Color.RED, Color.CYAN);
        //text color
        barDataSet.setValueTextColor(Color.BLACK);
        //Setting text size
        barDataSet.setValueTextSize(16f);
        barChart.getDescription().setEnabled(true);
    }

    //Methods
    private void getData() {
        barArrayList = new ArrayList<>();
        barArrayList.add(new BarEntry(1f, 1500));
        barArrayList.add(new BarEntry(2f, 1800));
        barArrayList.add(new BarEntry(3f, 2100));
        barArrayList.add(new BarEntry(4f, 1900));
        barArrayList.add(new BarEntry(5f, 2000));
        barArrayList.add(new BarEntry(6f, 2200));
        barArrayList.add(new BarEntry(7f, 1500));
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