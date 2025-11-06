package com.jsca.gestorgastospersonalesfrontend.beans;

import com.jsca.gestorgastospersonalesfrontend.models.UserDTO;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

import java.io.Serializable;

@Named("sessionBean")
@SessionScoped
public class SessionBean implements Serializable {
    private UserDTO currentUser;
    private boolean authenticated = false;

    /**
     * Establecer usuario después del login
     */
    public void setCurrentUser(UserDTO user) {
        this.currentUser = user;
        this.authenticated = true;
    }

    /**
     * Cerrar sesión
     */
    public String logout() {
        FacesContext.getCurrentInstance()
                .getExternalContext()
                .invalidateSession();

        this.currentUser = null;
        this.authenticated = false;

        return "/index.xhtml?faces-redirect=true";
    }

    /**
     * Verificar si el usuario está logueado
     */
    public boolean isAuthenticated() {
        return authenticated && currentUser != null;
    }



    public String getCurrentUserName() {
        return currentUser != null ? currentUser.getUsername() : "Usuario";
    }

    public Long getCurrentUserId() {
        return currentUser != null ? currentUser.getUserId() : null;
    }
}
