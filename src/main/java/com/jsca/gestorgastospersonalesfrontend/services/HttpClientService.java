package com.jsca.gestorgastospersonalesfrontend.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import jakarta.enterprise.context.ApplicationScoped;
import okhttp3.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;


@ApplicationScoped
public class HttpClientService {

    private static final String API_BASE_URL = "http://localhost:8080/api";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client;
    private final Gson gson;

    public HttpClientService() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class,
                        (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) ->
                                context.serialize(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .registerTypeAdapter(LocalDateTime.class,
                        (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) ->
                                LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .create();

    }

    /**
     * GET request
     */
    public <T> T get(String endpoint, Class<T> responseType) throws IOException {
        Request request = new Request.Builder()
                .url(API_BASE_URL + endpoint)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Error HTTP: " + response.code());
            }

            String responseBody = response.body().string();
            return gson.fromJson(responseBody, responseType);
        }
    }

    /**
     * POST request
     */
    public <T> T post(String endpoint, Object requestBody, Class<T> responseType) throws IOException {
        String json = gson.toJson(requestBody);
        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(API_BASE_URL + endpoint)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                throw new IOException("Error HTTP " + response.code() + ": " + errorBody);
            }

            String responseBody = response.body().string();
            return gson.fromJson(responseBody, responseType);
        }
    }

    /**
     * PUT request
     */
    public <T> T put(String endpoint, Object requestBody, Class<T> responseType) throws IOException {
        String json = gson.toJson(requestBody);
        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(API_BASE_URL + endpoint)
                .put(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Error HTTP: " + response.code());
            }

            String responseBody = response.body().string();
            return gson.fromJson(responseBody, responseType);
        }
    }

    /**
     * DELETE request
     */
    public void delete(String endpoint) throws IOException {
        Request request = new Request.Builder()
                .url(API_BASE_URL + endpoint)
                .delete()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Error HTTP: " + response.code());
            }
        }
    }
}
