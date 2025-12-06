package com.example.moneytrackervarela.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneytrackervarela.R;
import com.example.moneytrackervarela.models.CategoryStat;

import java.util.List;
import java.util.Locale;

public class StatsCategoryAdapter extends RecyclerView.Adapter<StatsCategoryAdapter.StatViewHolder> {

    private List<CategoryStat> statsList;

    public StatsCategoryAdapter(List<CategoryStat> statsList) {
        this.statsList = statsList;
    }

    @NonNull
    @Override
    public StatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stat_category_item, parent, false);
        return new StatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatViewHolder holder, int position) {
        CategoryStat stat = statsList.get(position);
        holder.categoryNameTextView.setText(stat.getCategoryName());
        holder.categoryTotalTextView.setText(String.format(Locale.US, "$%.2f", stat.getTotalAmount()));
        holder.categoryPercentageTextView.setText(String.format(Locale.US, "%.1f%%", stat.getPercentage()));
    }

    @Override
    public int getItemCount() {
        return statsList.size();
    }

    public static class StatViewHolder extends RecyclerView.ViewHolder {
        TextView categoryNameTextView;
        TextView categoryPercentageTextView;
        TextView categoryTotalTextView;

        public StatViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryNameTextView = itemView.findViewById(R.id.category_name_stat_textview);
            categoryPercentageTextView = itemView.findViewById(R.id.category_percentage_textview);
            categoryTotalTextView = itemView.findViewById(R.id.category_total_textview);
        }
    }
}
