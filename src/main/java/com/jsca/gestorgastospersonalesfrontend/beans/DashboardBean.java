package com.jsca.gestorgastospersonalesfrontend.beans;

import com.jsca.gestorgastospersonalesfrontend.models.MonthlySummaryDTO;
import com.jsca.gestorgastospersonalesfrontend.services.TransactionService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.model.SelectItem;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Managed Bean para el Dashboard
 * Verifica autenticación en @PostConstruct
 */

@Named("dashboardBean")
@ViewScoped
public class DashboardBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private SessionBean sessionBean;

    @Inject
    private TransactionService transactionService;

    private MonthlySummaryDTO monthlySummary;
    private Integer currentYear;
    private Integer currentMonth;
    private Integer todayYear;
    private Integer todayMonth;

    /**
     * Verifica si el usuario está autenticado
     */
    @PostConstruct
    public void init() {
        System.out.println("=== DashboardBean.init() ===");

        if (!sessionBean.isAuthenticated()) {
            redirectToLogin();
            return;
        }
        LocalDate now = LocalDate.now();
        this.currentYear = now.getYear();
        this.currentMonth = now.getMonthValue();

        System.out.println("Mes actual: " + getCurrentMonthName() + " " + currentYear);

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
            monthlySummary.setYear(currentYear);
            monthlySummary.setMonth(currentMonth);
        }
    }

    /**
     * Obtener los meses
     */
    public String getCurrentMonthName() {
        String[] months = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        if (currentMonth >= 1 && currentMonth <= 12) {
            return months[currentMonth - 1];
        }
        return "Mes Desconocido";
    }


    /**
     * Cambio el mes o año
     */
    public void onPeriodChange() {
        LocalDate selected = LocalDate.of(currentYear, currentMonth, 1);
        LocalDate today = LocalDate.now();

        if (selected.isAfter(today.withDayOfMonth(1))) {
            showErrorMessage("No puedes seleccionar un mes futuro");
            this.currentYear = todayYear;
            this.currentMonth = todayMonth;
        }
        loadMonthlySummary();
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
     * Volver al mes actual
     */
    public void goToCurrentMonth() {
        LocalDate now = LocalDate.now();
        this.currentYear = now.getYear();
        this.currentMonth = now.getMonthValue();
        loadMonthlySummary();
    }

    /**
     * Verificar si estamos en el mes actual
     */
    public boolean isCurrentMonth() {
        return currentYear == todayYear && currentMonth == todayMonth;
    }

    /**
     * Obtener lista de meses para el dropdown
     */
    public List<SelectItem> getMonthOptions() {
        List<SelectItem> months = new ArrayList<>();
        String[] monthNames = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        for (int i = 0; i < 12; i++) {
            int monthValue = i + 1;
            if (currentYear == todayYear && monthValue > todayMonth) {
                continue;
            }
            months.add(new SelectItem(monthValue, monthNames[i]));
        }
        return months;
    }

    /**
     * Obtener lista de años para el dropdown
     * Últimos 5 años + año actual
     */
    public List<SelectItem> getYearOptions() {
        List<SelectItem> years = new ArrayList<>();
        int currentYearNow = LocalDate.now().getYear();
        for (int i = currentYearNow; i >= currentYearNow - 5; i--) {
            years.add(new SelectItem(i, String.valueOf(i)));
        }
        return years;
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

    // ========== GETTERS Y SETTERS (TODOS) ==========

    public MonthlySummaryDTO getMonthlySummary() {
        return monthlySummary;
    }

    public void setMonthlySummary(MonthlySummaryDTO monthlySummary) {
        this.monthlySummary = monthlySummary;
    }

    public Integer getCurrentYear() {
        return currentYear;
    }

    public void setCurrentYear(int currentYear) {
        System.out.println("setCurrentYear llamado con: " + currentYear);
        this.currentYear = currentYear;
    }

    public Integer getCurrentMonth() {
        return currentMonth;
    }

    public void setCurrentMonth(int currentMonth) {
        System.out.println("setCurrentMonth llamado con: " + currentMonth);
        this.currentMonth = currentMonth;
    }

    public Integer getTodayYear() {
        return todayYear;
    }

    public void setTodayYear(int todayYear) {
        this.todayYear = todayYear;
    }

    public Integer getTodayMonth() {
        return todayMonth;
    }

    public void setTodayMonth(int todayMonth) {
        this.todayMonth = todayMonth;
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
