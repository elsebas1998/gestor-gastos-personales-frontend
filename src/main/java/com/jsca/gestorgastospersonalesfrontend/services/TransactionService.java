package com.jsca.gestorgastospersonalesfrontend.services;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.jsca.gestorgastospersonalesfrontend.models.MonthlySummaryDTO;
import com.jsca.gestorgastospersonalesfrontend.models.TransactionDTO;
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
public class TransactionService {
    @Inject
    private HttpClientService httpClient;

    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson;
    private static final String BASE_URL = "http://localhost:8080/api";

    public TransactionService() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .setLenient()
                .create();
    }

    /**
     * Obtener resumen mensual
     */
    public MonthlySummaryDTO getMonthlySummary(Long userId, int year, int month) throws IOException {
        String endpoint = String.format("/transactions/summary?userId=%d&year=%d&month=%d",
                userId, year, month);
        return httpClient.get(endpoint, MonthlySummaryDTO.class);
    }

    /**
     * Obtener transacciones del mes
     */
    public List<TransactionDTO> getTransactionsOfMonth(Long userId, int year, int month) throws IOException {
        String endpoint = String.format("/transactions/month?userId=%d&year=%d&month=%d",
                userId, year, month);

        Request request = new Request.Builder()
                .url(BASE_URL + endpoint)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Error HTTP: " + response.code());
            }

            String responseBody = response.body().string();
            Type listType = new TypeToken<List<TransactionDTO>>(){}.getType();
            return gson.fromJson(responseBody, listType);
        }
    }

    /**
     * Crear nueva transacción
     */
    public TransactionDTO createTransaction(Long userId, TransactionDTO transaction) throws IOException {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("amount", transaction.getAmount());
        requestBody.put("description", transaction.getDescription());
        requestBody.put("transactionDate", transaction.getTransactionDate().toString());
        requestBody.put("type", transaction.getType());
        requestBody.put("categoryId", transaction.getCategoryId());

        String endpoint = "/transactions?userId=" + userId;
        return httpClient.post(endpoint, requestBody, TransactionDTO.class);
    }

    /**
     * Actualizar transacción existente
     */
    public TransactionDTO updateTransaction(Long userId, Long transactionId, TransactionDTO transaction) throws IOException {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("amount", transaction.getAmount());
        requestBody.put("description", transaction.getDescription());

        String endpoint = String.format("/transactions/%d?userId=%d", transactionId, userId);
        return httpClient.put(endpoint, requestBody, TransactionDTO.class);
    }

    /**
     * Eliminar transacción
     */
    public void deleteTransaction(Long userId, Long transactionId) throws IOException {
        String endpoint = String.format("/transactions/%d?userId=%d", transactionId, userId);
        httpClient.delete(endpoint);
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
}
