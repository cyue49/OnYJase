package com.example.onyjase.utils;

import com.example.onyjase.models.stickers.Stickers;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface StickersService {
    @GET("stickers/search")
    Call<Stickers> getStickers(@Query("api_key") String apiKey, @Query("q") String query);
}
