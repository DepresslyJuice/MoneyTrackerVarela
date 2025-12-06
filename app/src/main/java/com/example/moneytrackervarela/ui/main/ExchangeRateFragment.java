package com.example.moneytrackervarela.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneytrackervarela.R;
import com.example.moneytrackervarela.adapters.ExchangeRateAdapter;
import com.example.moneytrackervarela.api.ExchangeRateApiService;
import com.example.moneytrackervarela.api.ExchangeRateResponse;
import com.example.moneytrackervarela.api.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExchangeRateFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView errorTextView;
    private ExchangeRateAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_exchange_rate, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.exchange_recyclerview);
        progressBar = view.findViewById(R.id.exchange_progress_bar);
        errorTextView = view.findViewById(R.id.exchange_error_textview);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fetchExchangeRates();
    }

    private void fetchExchangeRates() {
        progressBar.setVisibility(View.VISIBLE);
        errorTextView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        ExchangeRateApiService apiService = RetrofitClient.getClient().create(ExchangeRateApiService.class);
        Call<ExchangeRateResponse> call = apiService.getLatestRates("USD");

        call.enqueue(new Callback<ExchangeRateResponse>() {
            @Override
            public void onResponse(Call<ExchangeRateResponse> call, Response<ExchangeRateResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && "success".equals(response.body().getResult())) {
                    adapter = new ExchangeRateAdapter(response.body().getConversionRates());
                    recyclerView.setAdapter(adapter);
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    errorTextView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ExchangeRateResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                errorTextView.setVisibility(View.VISIBLE);
            }
        });
    }
}
