package com.jsca.gestorgastospersonalesfrontend.services;

import com.jsca.gestorgastospersonalesfrontend.models.LoginRequest;
import com.jsca.gestorgastospersonalesfrontend.models.RegisterRequest;
import com.jsca.gestorgastospersonalesfrontend.models.UserDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.IOException;

/**
 * Servicio para autenticación (Login y Registro)
 */
@ApplicationScoped
public class AuthService {

    @Inject
    private HttpClientService httpClient;

    /**
     * Login de usuario
     */
    public UserDTO login(String username, String password) throws IOException {
        LoginRequest request = new LoginRequest(username, password);
        return httpClient.post("/auth/login", request, UserDTO.class);
    }

    /**
     * Registro de nuevo usuario
     */
    public UserDTO register(RegisterRequest request) throws IOException {
        return httpClient.post("/auth/register", request, UserDTO.class);
    }
}
