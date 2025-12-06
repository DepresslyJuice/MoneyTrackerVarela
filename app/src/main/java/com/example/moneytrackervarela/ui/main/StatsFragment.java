package com.example.moneytrackervarela.ui.main;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneytrackervarela.R;
import com.example.moneytrackervarela.adapters.StatsCategoryAdapter;
import com.example.moneytrackervarela.database.DatabaseHelper;
import com.example.moneytrackervarela.models.CategoryStat;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StatsFragment extends Fragment {

    private PieChart pieChart;
    private TextView dailyAverageTextView;
    private RecyclerView statsRecyclerView;

    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dbHelper = new DatabaseHelper(getContext());
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pieChart = view.findViewById(R.id.pie_chart);
        dailyAverageTextView = view.findViewById(R.id.daily_average_textview);
        statsRecyclerView = view.findViewById(R.id.stats_recyclerview);
        statsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onResume() {
        super.onResume();
        loadStatsData();
    }

    private void loadStatsData() {
        // Get date range for the current month
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        String startDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        String endDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());

        // Get data from DB
        Map<String, Double> categoryExpenses = dbHelper.getCategoryStatistics("EXPENSE", startDate, endDate);
        double dailyAverage = dbHelper.getDailyAverage("EXPENSE", startDate, endDate);

        // Update UI
        dailyAverageTextView.setText(String.format(Locale.US, "$%.2f", dailyAverage));
        setupPieChart(categoryExpenses);
        setupRecyclerView(categoryExpenses);
    }

    private void setupPieChart(Map<String, Double> categoryExpenses) {
        if (categoryExpenses == null || categoryExpenses.isEmpty()) {
            pieChart.clear();
            pieChart.setNoDataText("Aún no hay gastos este mes.");
            pieChart.invalidate();
            return;
        }

        List<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Double> entry : categoryExpenses.entrySet()) {
            entries.add(new PieEntry(entry.getValue().floatValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Gastos por Categoría");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);
        dataSet.setValueFormatter(new PercentFormatter(pieChart));

        PieData pieData = new PieData(dataSet);
        
        // Chart configuration
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setEntryLabelColor(Color.BLACK);

        pieChart.setData(pieData);
        pieChart.animateY(1000);
        pieChart.invalidate(); // Refresh chart
    }

    private void setupRecyclerView(Map<String, Double> categoryExpenses) {
        if (categoryExpenses == null || categoryExpenses.isEmpty()) {
            statsRecyclerView.setAdapter(null); // Clear the list if no data
            return;
        }

        double totalExpenses = categoryExpenses.values().stream().mapToDouble(Double::doubleValue).sum();
        
        List<CategoryStat> statsList = new ArrayList<>();
        for (Map.Entry<String, Double> entry : categoryExpenses.entrySet()) {
            CategoryStat stat = new CategoryStat(entry.getKey(), entry.getValue());
            float percentage = (float) ((entry.getValue() / totalExpenses) * 100);
            stat.setPercentage(percentage);
            statsList.add(stat);
        }

        StatsCategoryAdapter adapter = new StatsCategoryAdapter(statsList);
        statsRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        if (dbHelper != null) {
            dbHelper.close();
        }
        super.onDestroy();
    }
}
