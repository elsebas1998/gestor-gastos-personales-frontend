package com.jsca.gestorgastospersonalesfrontend.services;

import com.jsca.gestorgastospersonalesfrontend.models.MonthlySummaryDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.IOException;

@ApplicationScoped
public class TransactionService {
    @Inject
    private HttpClientService httpClient;

    /**
     * Obtener resumen mensual
     */
    public MonthlySummaryDTO getMonthlySummary(Long userId, int year, int month) throws IOException {
        String endpoint = String.format("/transactions/summary?userId=%d&year=%d&month=%d",
                userId, year, month);
        return httpClient.get(endpoint, MonthlySummaryDTO.class);
    }
}
