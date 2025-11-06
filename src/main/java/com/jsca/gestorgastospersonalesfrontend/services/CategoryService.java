package com.jsca.gestorgastospersonalesfrontend.services;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jsca.gestorgastospersonalesfrontend.models.CategoryDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

@ApplicationScoped
public class CategoryService {
    @Inject
    private HttpClientService httpClient;

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
}
