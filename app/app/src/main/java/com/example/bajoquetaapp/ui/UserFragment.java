package com.example.bajoquetaapp.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.bajoquetaapp.databinding.FragmentUserBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;


public class UserFragment extends Fragment {

    private FragmentUserBinding binding;
    ArrayList barArrayList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentUserBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        //------------------------------------------------------------------------------------------
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
        //------------------------------------------------------------------------------------------



        //final TextView textView = binding.textDashboard;

        return root;
    }

    //Methods
    private void getData(){
        barArrayList = new ArrayList();
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
}