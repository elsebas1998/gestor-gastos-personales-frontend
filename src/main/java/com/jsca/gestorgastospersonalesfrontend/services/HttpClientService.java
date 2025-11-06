package com.jsca.gestorgastospersonalesfrontend.services;

import com.google.gson.*;
import jakarta.enterprise.context.ApplicationScoped;
import okhttp3.*;

import java.io.IOException;
import java.time.LocalDate;
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
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .setLenient()
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
                String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                throw new IOException("Error HTTP " + response.code() + ": " + errorBody);
            }
        }
    }

    private static class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        @Override
        public JsonElement serialize(LocalDateTime src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.format(FORMATTER));
        }

        @Override
        public LocalDateTime deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                return LocalDateTime.parse(json.getAsString(), FORMATTER);
            } catch (Exception e) {
                throw new JsonParseException("Error al parsear LocalDateTime: " + json.getAsString(), e);
            }
        }
    }


    private static class LocalDateAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {

        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

        @Override
        public JsonElement serialize(LocalDate src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.format(FORMATTER));
        }

        @Override
        public LocalDate deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                return LocalDate.parse(json.getAsString(), FORMATTER);
            } catch (Exception e) {
                throw new JsonParseException("Error al parsear LocalDate: " + json.getAsString(), e);
            }
        }
    }

}
