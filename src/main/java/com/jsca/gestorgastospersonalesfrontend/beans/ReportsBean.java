package com.jsca.gestorgastospersonalesfrontend.beans;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.model.SelectItem;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Named("reportsBean")
@ViewScoped
public class ReportsBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private SessionBean sessionBean;

    @Getter @Setter
    private Integer selectedMonth;

    @Getter @Setter
    private Integer selectedYear;

    @Getter @Setter
    private List<SelectItem> monthOptions;

    @Getter @Setter
    private List<SelectItem> yearOptions;

    private static final String API_BASE_URL = "http://localhost:8080/api/reports";

    @PostConstruct
    public void init() {
        if (!sessionBean.isAuthenticated()) {
            redirectToLogin();
            return;
        }

        initializeOptions();
        this.selectedMonth = java.time.LocalDate.now().getMonthValue();
        this.selectedYear = java.time.LocalDate.now().getYear();
    }

    /**
     * Inicializar opciones de meses y años
     */
    private void initializeOptions() {
        // Meses
        monthOptions = new ArrayList<>();
        String[] months = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};

        for (int i = 1; i <= 12; i++) {
            monthOptions.add(new SelectItem(i, months[i - 1]));
        }

        yearOptions = new ArrayList<>();
        int currentYear = java.time.LocalDate.now().getYear();
        for (int i = currentYear; i >= currentYear - 4; i--) {
            yearOptions.add(new SelectItem(i, String.valueOf(i)));
        }
    }

    /**
     * Descargar reporte de resumen mensual
     * Llama al API REST del backend
     */
    public void downloadResumenMensual() {
        try {
            if (selectedMonth == null || selectedYear == null) {
                showErrorMessage("Selecciona mes y año");
                return;
            }

            Long userId = sessionBean.getCurrentUserId();

            String url = String.format("%s/resumen-mensual?mes=%d&anio=%d&userId=%d",
                    API_BASE_URL, selectedMonth, selectedYear, userId);

            System.out.println("Llamando al API: " + url);

            byte[] pdfBytes = callApiForPDF(url);

            if (pdfBytes == null || pdfBytes.length == 0) {
                showErrorMessage("Error: El API no retornó datos válidos");
                return;
            }

            downloadFile(pdfBytes, "resumen-" + selectedMonth + "-" + selectedYear + ".pdf");

            showSuccessMessage("Reporte descargado exitosamente");

        } catch (Exception e) {
            e.printStackTrace();
            showErrorMessage("Error al generar el reporte: " + e.getMessage());
        }
    }

    /**
     * Llamar al API REST y obtener el PDF
     */
    private byte[] callApiForPDF(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            if (responseCode == 200) {
                InputStream inputStream = connection.getInputStream();
                return inputStream.readAllBytes();
            } else {
                System.err.println("Error: Response Code " + responseCode);
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error calling API: " + e.getMessage());
            return null;
        }
    }

    /**
     * Descargar archivo
     */
    private void downloadFile(byte[] fileBytes, String fileName) throws IOException {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();

        externalContext.responseReset();
        externalContext.setResponseContentType("application/pdf");
        externalContext.setResponseHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        externalContext.getResponseOutputStream().write(fileBytes);
        facesContext.responseComplete();
    }

    /**
     * Mostrar mensaje de éxito
     */
    private void showSuccessMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", message));
    }

    /**
     * Mostrar mensaje de error
     */
    private void showErrorMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", message));
    }

    /**
     * Redirigir a login
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
}
