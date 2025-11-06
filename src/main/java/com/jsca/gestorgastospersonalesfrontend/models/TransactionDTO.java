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
public class TransactionDTO implements Serializable {

    private Long transactionId;
    private BigDecimal amount;
    private String description;
    private LocalDate transactionDate;
    private String type;
    private Long categoryId;
    private String categoryName;
    private String categoryIcon;
    private LocalDateTime createdAt;
}
