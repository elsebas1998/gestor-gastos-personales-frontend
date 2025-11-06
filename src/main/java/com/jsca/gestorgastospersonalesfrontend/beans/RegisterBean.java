package com.jsca.gestorgastospersonalesfrontend.beans;

import com.jsca.gestorgastospersonalesfrontend.models.RegisterRequest;
import com.jsca.gestorgastospersonalesfrontend.models.UserDTO;
import com.jsca.gestorgastospersonalesfrontend.services.AuthService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Data;

import java.io.Serializable;

/**
 * Managed Bean para la página de Registro
 */
@Named("registerBean")
@RequestScoped
@Data
public class RegisterBean implements Serializable {

    private RegisterRequest registerRequest = new RegisterRequest();

    @Inject
    private AuthService authService;

    @Inject
    private SessionBean sessionBean;

    /**
     * Método que se ejecuta al hacer submit del formulario de registro
     */
    public String register() {
        try {
            if (!validateForm()) {
                return null;
            }

            UserDTO user = authService.register(registerRequest);

            sessionBean.setCurrentUser(user);

            showSuccessMessage("¡Cuenta creada exitosamente! Bienvenido " + user.getUsername());

            return "/pages/dashboard.xhtml?faces-redirect=true";

        } catch (Exception e) {
            String errorMessage = e.getMessage();
            if (errorMessage.contains("username")) {
                showErrorMessage("El nombre de usuario ya está en uso");
            } else if (errorMessage.contains("email")) {
                showErrorMessage("El email ya está registrado");
            } else {
                showErrorMessage("Error al crear la cuenta: " + errorMessage);
            }
            return null;
        }
    }

    /**
     * Validaciones personalizadas del formulario
     */
    private boolean validateForm() {
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            showErrorMessage("Las contraseñas no coinciden");
            return false;
        }

        if (registerRequest.getPassword().length() < 6) {
            showErrorMessage("La contraseña debe tener al menos 6 caracteres");
            return false;
        }

        if (registerRequest.getUsername().length() < 3) {
            showErrorMessage("El username debe tener al menos 3 caracteres");
            return false;
        }

        return true;
    }

    /**
     * Navegar a la página de login
     */
    public String goToLogin() {
        return "/pages/login.xhtml?faces-redirect=true";
    }

    private void showErrorMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", message));
    }

    private void showSuccessMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", message));
    }
    
}
