package com.jsca.gestorgastospersonalesfrontend.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthlySummaryDTO implements Serializable {

    private Integer year;
    private Integer month;
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal savings;
    private BigDecimal savingsRate;

    public String getFormattedSavingsRate() {
        if (savingsRate == null) {
            return "0.0%";
        }
        return String.format("%.1f%%", savingsRate);
    }

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) {
            return "$0.00";
        }
        return String.format("$%,.2f", amount);
    }

    public String getFormattedIncome() {
        return formatCurrency(totalIncome);
    }

    public String getFormattedExpense() {
        return formatCurrency(totalExpense);
    }

    public String getFormattedSavings() {
        return formatCurrency(savings);
    }

}
