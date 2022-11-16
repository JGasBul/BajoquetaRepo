package com.example.smartcook_perfil.presentacion;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import com.example.smartcook_perfil.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList barArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil_user);
        //------------------------------------------------------------------------------------------
        //Start Graphic Bar
        BarChart barChart = findViewById(R.id.barchart);
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
//    private ArrayList getCalorias(ArrayList barArrayList){
//        ArrayList colors;
//        for(int i=0; i<barArrayList.size(); i++){
//            if(barArrayList[i].y <= 1500){colors.add(Color.CYAN);}
//            else if(barArrayList[i].y <= 2000){colors.add(Color.GREEN);}
//            else if(barArrayList[i].y > 2000){colors.add(Color.RED);}
//            else {colors.add(Color.BLACK);}
//        }
//        return colors;
//    }
}