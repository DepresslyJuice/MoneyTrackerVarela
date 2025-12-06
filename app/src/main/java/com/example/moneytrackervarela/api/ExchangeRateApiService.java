package com.example.moneytrackervarela.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ExchangeRateApiService {

    // The API key is hardcoded here for simplicity, but in a real app, it should be stored securely.
    @GET("v6/faf40786d5622758b06ade92/latest/{currency}")
    Call<ExchangeRateResponse> getLatestRates(@Path("currency") String baseCurrency);
}
