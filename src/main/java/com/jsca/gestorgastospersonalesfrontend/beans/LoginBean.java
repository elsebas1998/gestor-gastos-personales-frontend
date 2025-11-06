package com.jsca.gestorgastospersonalesfrontend.beans;

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
 * Managed Bean para la página de Login
 * RequestScoped: Se destruye después de procesar el request
 */
@Data
@Named("loginBean")
@RequestScoped
public class LoginBean implements Serializable {

    private String username;
    private String password;

    @Inject
    private AuthService authService;

    @Inject
    private SessionBean sessionBean;

    /**
     * Método que se ejecuta al hacer submit del formulario de login
     */
    public String login() {
        try {
            if (username == null || username.trim().isEmpty()) {
                showErrorMessage("El username es requerido");
                return null;
            }

            if (password == null || password.trim().isEmpty()) {
                showErrorMessage("La contraseña es requerida");
                return null;
            }

            UserDTO user = authService.login(username, password);

            sessionBean.setCurrentUser(user);

            showSuccessMessage("¡Bienvenido " + user.getUsername() + "!");

            return "/pages/dashboard.xhtml?faces-redirect=true";

        } catch (Exception e) {
            showErrorMessage("Error al iniciar sesión: " + e.getMessage());
            return null;
        }
    }

    /**
     * Navegar a la página de registro
     */
    public String goToRegister() {
        return "/pages/register.xhtml?faces-redirect=true";
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
