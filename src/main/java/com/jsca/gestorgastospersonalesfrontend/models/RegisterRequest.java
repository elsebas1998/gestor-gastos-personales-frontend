package com.jsca.gestorgastospersonalesfrontend.models;

import lombok.Data;

import java.io.Serializable;

@Data
public class RegisterRequest implements Serializable {
    private String username;
    private String email;
    private String password;
    private String confirmPassword; // Solo para validación en frontend
    private String firstName;
    private String lastName;
}
