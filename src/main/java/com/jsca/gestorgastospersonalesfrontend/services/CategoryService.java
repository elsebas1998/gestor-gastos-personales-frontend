package com.jsca.gestorgastospersonalesfrontend.services;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.jsca.gestorgastospersonalesfrontend.models.CategoryDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class CategoryService {
    @Inject
    private HttpClientService httpClient;

    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson;
    private static final String BASE_URL = "http://localhost:8080/api";

    public CategoryService() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .setLenient()
                .create();
    }

    /**
     * Obtener todas las categorías del usuario
     */
    public List<CategoryDTO> getCategoriesByUser(Long userId) throws IOException {
        String endpoint = "/categories?userId=" + userId;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://localhost:8080/api" + endpoint)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Error HTTP: " + response.code());
            }

            String responseBody = response.body().string();
            Type listType = new TypeToken<List<CategoryDTO>>(){}.getType();
            return new Gson().fromJson(responseBody, listType);
        }
    }

    private static class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        @Override
        public JsonElement serialize(LocalDateTime src, java.lang.reflect.Type typeOfSrc, com.google.gson.JsonSerializationContext context) {
            return new JsonPrimitive(src.format(FORMATTER));
        }

        @Override
        public LocalDateTime deserialize(JsonElement json, java.lang.reflect.Type typeOfT, com.google.gson.JsonDeserializationContext context) throws JsonParseException {
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
        public JsonElement serialize(LocalDate src, java.lang.reflect.Type typeOfSrc, com.google.gson.JsonSerializationContext context) {
            return new JsonPrimitive(src.format(FORMATTER));
        }

        @Override
        public LocalDate deserialize(JsonElement json, java.lang.reflect.Type typeOfT, com.google.gson.JsonDeserializationContext context) throws JsonParseException {
            try {
                return LocalDate.parse(json.getAsString(), FORMATTER);
            } catch (Exception e) {
                throw new JsonParseException("Error al parsear LocalDate: " + json.getAsString(), e);
            }
        }
    }

    /**
     * Obtener categorías por tipo (INGRESO o EGRESO)
     */
    public List<CategoryDTO> getCategoriesByType(Long userId, String type) throws IOException {
        String endpoint = String.format("/categories/type?userId=%d&type=%s", userId, type);

        Request request = new Request.Builder()
                .url(BASE_URL + endpoint)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Error HTTP: " + response.code());
            }

            String responseBody = response.body().string();
            Type listType = new TypeToken<List<CategoryDTO>>(){}.getType();
            return gson.fromJson(responseBody, listType);
        }
    }

    /**
     * Crear nueva categoría
     */
    public CategoryDTO createCategory(Long userId, CategoryDTO category) throws IOException {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", category.getName());
        requestBody.put("type", category.getType());
        requestBody.put("isFixed", category.getIsFixed());
        requestBody.put("icon", category.getIcon());
        requestBody.put("color", category.getColor());
        requestBody.put("description", category.getDescription());

        String endpoint = "/categories?userId=" + userId;
        return httpClient.post(endpoint, requestBody, CategoryDTO.class);
    }

    /**
     * Actualizar categoría existente
     */
    public CategoryDTO updateCategory(Long userId, Long categoryId, CategoryDTO category) throws IOException {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", category.getName());
        requestBody.put("icon", category.getIcon());
        requestBody.put("color", category.getColor());
        requestBody.put("description", category.getDescription());

        String endpoint = String.format("/categories/%d?userId=%d", categoryId, userId);
        return httpClient.put(endpoint, requestBody, CategoryDTO.class);
    }

    /**
     * Eliminar categoría
     */
    public void deleteCategory(Long userId, Long categoryId) throws IOException {
        String endpoint = String.format("/categories/%d?userId=%d", categoryId, userId);
        httpClient.delete(endpoint);
    }


}
