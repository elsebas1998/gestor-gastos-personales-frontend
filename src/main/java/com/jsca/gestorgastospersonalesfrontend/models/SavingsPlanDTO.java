package com.jsca.gestorgastospersonalesfrontend.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SavingsPlanDTO implements Serializable {

    private Long planId;
    private String name;
    private String description;
    private BigDecimal goalAmount;
    private BigDecimal currentAmount;
    private LocalDate targetDate;
    private String planType;
    private String status;
    private BigDecimal progressPercentage;
    private LocalDateTime createdAt;
}
