package com.jsca.gestorgastospersonalesfrontend.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO implements Serializable {

    private Long categoryId;
    private String name;
    private String type;
    private Boolean isFixed;
    private String icon;
    private String color;
    private String description;
    private LocalDateTime createdAt;
}
