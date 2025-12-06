package com.example.moneytrackervarela.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneytrackervarela.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Locale;

public class ExchangeRateAdapter extends RecyclerView.Adapter<ExchangeRateAdapter.RateViewHolder> {

    private final List<Map.Entry<String, Double>> ratesList;

    public ExchangeRateAdapter(Map<String, Double> ratesMap) {
        this.ratesList = new ArrayList<>(ratesMap.entrySet());
    }

    @NonNull
    @Override
    public RateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.exchange_rate_item, parent, false);
        return new RateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RateViewHolder holder, int position) {
        Map.Entry<String, Double> rateEntry = ratesList.get(position);
        holder.currencyCodeTextView.setText(rateEntry.getKey());
        holder.currencyRateTextView.setText(String.format(Locale.US, "%.4f", rateEntry.getValue()));
    }

    @Override
    public int getItemCount() {
        return ratesList.size();
    }

    public static class RateViewHolder extends RecyclerView.ViewHolder {
        TextView currencyCodeTextView;
        TextView currencyRateTextView;

        public RateViewHolder(@NonNull View itemView) {
            super(itemView);
            currencyCodeTextView = itemView.findViewById(R.id.currency_code_textview);
            currencyRateTextView = itemView.findViewById(R.id.currency_rate_textview);
        }
    }
}
