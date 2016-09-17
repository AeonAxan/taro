package io.azaan.taro;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import io.azaan.taro.io.azaan.taro.viz.models.StackedBarData;
import io.azaan.taro.io.azaan.taro.viz.views.StackedBarChart;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);
        }

        List<StackedBarData> data = new ArrayList<>();
        data.add(new StackedBarData("Sun", 100));
        data.add(new StackedBarData("Mon", 150));
        data.add(new StackedBarData("Tue", 180));
        data.add(new StackedBarData("Wed", 242));
        data.add(new StackedBarData("Thu", 329));
        data.add(new StackedBarData("Fri", 80));
        data.add(new StackedBarData("Sat", 50));

        StackedBarChart chart = (StackedBarChart) findViewById(R.id.chart);
        chart.setData(data);
    }
}
