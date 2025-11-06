package com.jsca.gestorgastospersonalesfrontend.beans;

import com.jsca.gestorgastospersonalesfrontend.models.MonthlySummaryDTO;
import com.jsca.gestorgastospersonalesfrontend.services.TransactionService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * Managed Bean para el Dashboard
 * Verifica autenticación en @PostConstruct
 */
@Named("dashboardBean")
@RequestScoped
public class DashboardBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private SessionBean sessionBean;

    @Inject
    private TransactionService transactionService;

    private MonthlySummaryDTO monthlySummary;
    private int currentYear;
    private int currentMonth;

    /**
     * Verifica si el usuario está autenticado
     */
    @PostConstruct
    public void init() {
        if (!sessionBean.isAuthenticated()) {
            redirectToLogin();
            return;
        }
        LocalDate now = LocalDate.now();
        this.currentYear = now.getYear();
        this.currentMonth = now.getMonthValue();

        loadMonthlySummary();
    }

    /**
     * Cargar resumen mensual
     */
    public void loadMonthlySummary() {
        try {
            Long userId = sessionBean.getCurrentUserId();
            monthlySummary = transactionService.getMonthlySummary(userId, currentYear, currentMonth);
        } catch (IOException e) {
            showErrorMessage("Error al cargar el resumen mensual: " + e.getMessage());
            monthlySummary = new MonthlySummaryDTO();
        }
    }

    /**
     * Mostrar mensaje de error
     */
    private void showErrorMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", message));
    }

    /**
     * Cambiar al mes anterior
     */
    public void previousMonth() {
        currentMonth--;
        if (currentMonth < 1) {
            currentMonth = 12;
            currentYear--;
        }
        loadMonthlySummary();
    }

    /**
     * Cambiar al mes siguiente
     */
    public void nextMonth() {
        currentMonth++;
        if (currentMonth > 12) {
            currentMonth = 1;
            currentYear++;
        }
        loadMonthlySummary();
    }



    /**
     * Redirigir al login si no está autenticado
     */
    private void redirectToLogin() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();

        try {
            externalContext.redirect(externalContext.getRequestContextPath() + "/pages/login.xhtml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getCurrentMonthName() {
        String[] months = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        return months[currentMonth - 1];
    }
    public MonthlySummaryDTO getMonthlySummary() {
        return monthlySummary;
    }

    public void setMonthlySummary(MonthlySummaryDTO monthlySummary) {
        this.monthlySummary = monthlySummary;
    }

    public int getCurrentYear() {
        return currentYear;
    }

    public void setCurrentYear(int currentYear) {
        this.currentYear = currentYear;
    }

    public int getCurrentMonth() {
        return currentMonth;
    }

    public void setCurrentMonth(int currentMonth) {
        this.currentMonth = currentMonth;
    }

    public SessionBean getSessionBean() {
        return sessionBean;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public TransactionService getTransactionService() {
        return transactionService;
    }

    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }
}
