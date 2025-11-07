package com.jsca.gestorgastospersonalesfrontend.beans;

import com.jsca.gestorgastospersonalesfrontend.models.CategoryDTO;
import com.jsca.gestorgastospersonalesfrontend.models.TransactionDTO;
import com.jsca.gestorgastospersonalesfrontend.services.CategoryService;
import com.jsca.gestorgastospersonalesfrontend.services.TransactionService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.model.SelectItem;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Data;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Named("transactionsBean")
@ViewScoped
public class TransactionsBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private SessionBean sessionBean;

    @Inject
    private TransactionService transactionService;

    @Inject
    private CategoryService categoryService;

    private List<TransactionDTO> transactions;
    private List<CategoryDTO> categories;
    private int currentYear;
    private int currentMonth;

    private TransactionDTO selectedTransaction;
    private boolean showDialog = false;

    @PostConstruct
    public void init() {
        System.out.println("=== TransactionsBean.init() ejecutándose ===");
        System.out.println("Usuario autenticado: " + sessionBean.isAuthenticated());

        if (!sessionBean.isAuthenticated()) {
            redirectToLogin();
            return;
        }

        LocalDate now = LocalDate.now();
        this.currentYear = now.getYear();
        this.currentMonth = now.getMonthValue();

        System.out.println("currentYear: " + currentYear);
        System.out.println("currentMonth: " + currentMonth);
        System.out.println("currentMonthName: " + getCurrentMonthName());

        this.selectedTransaction = new TransactionDTO();
        this.selectedTransaction.setTransactionDate(LocalDate.now());
        this.selectedTransaction.setType("EGRESO");

        loadTransactions();
        loadCategories();

        System.out.println("Transacciones cargadas: " + (transactions != null ? transactions.size() : 0));
    }

    /**
     * Cargar transacciones del mes
     */
    public void loadTransactions() {
        try {
            Long userId = sessionBean.getCurrentUserId();
            transactions = transactionService.getTransactionsOfMonth(userId, currentYear, currentMonth);
        } catch (IOException e) {
            showErrorMessage("Error al cargar transacciones: " + e.getMessage());
            transactions = new ArrayList<>();
        }
    }

    /**
     * Cargar categorías para el dropdown
     */
    public void loadCategories() {
        try {
            Long userId = sessionBean.getCurrentUserId();
            categories = categoryService.getCategoriesByUser(userId);
        } catch (IOException e) {
            showErrorMessage("Error al cargar categorías: " + e.getMessage());
            categories = new ArrayList<>();
        }
    }

    /**
     * Abrir diálogo para nueva transacción
     */
    public void openNewDialog() {
        selectedTransaction = new TransactionDTO();
        selectedTransaction.setTransactionDate(LocalDate.now());
        selectedTransaction.setType("EGRESO"); // Por defecto
        showDialog = true;
    }

    /**
     * Abrir diálogo para editar transacción
     */
    public void openEditDialog(TransactionDTO transaction) {
        // Clonar la transacción para editar
        selectedTransaction = new TransactionDTO();
        selectedTransaction.setTransactionId(transaction.getTransactionId());
        selectedTransaction.setAmount(transaction.getAmount());
        selectedTransaction.setDescription(transaction.getDescription());
        selectedTransaction.setTransactionDate(transaction.getTransactionDate());
        selectedTransaction.setType(transaction.getType());
        selectedTransaction.setCategoryId(transaction.getCategoryId());
        showDialog = true;
    }

    /**
     * Guardar transacción (crear o actualizar)
     */
    public void saveTransaction() {
        try {
            Long userId = sessionBean.getCurrentUserId();

            if (selectedTransaction.getTransactionId() == null) {
                transactionService.createTransaction(userId, selectedTransaction);
                showSuccessMessage("Transacción creada exitosamente");
            } else {
                transactionService.updateTransaction(userId,
                        selectedTransaction.getTransactionId(),
                        selectedTransaction);
                showSuccessMessage("Transacción actualizada exitosamente");
            }

            loadTransactions();
            showDialog = false;

        } catch (IOException e) {
            showErrorMessage("Error al guardar transacción: " + e.getMessage());
        }
    }

    /**
     * Eliminar transacción
     */
    public void deleteTransaction(TransactionDTO transaction) {
        try {
            Long userId = sessionBean.getCurrentUserId();
            transactionService.deleteTransaction(userId, transaction.getTransactionId());

            showSuccessMessage("Transacción eliminada exitosamente");
            loadTransactions();

        } catch (IOException e) {
            showErrorMessage("Error al eliminar transacción: " + e.getMessage());
        }
    }

    /**
     * Cancelar edición
     */
    public void cancelDialog() {
        showDialog = false;
        selectedTransaction = null;
    }

    /**
     * Obtener nombre del mes actual
     */
    public String getCurrentMonthName() {
        String[] months = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        if (currentMonth >= 1 && currentMonth <= 12) {
            return months[currentMonth - 1];
        }
        return "Mes Desconocido";
    }

    private void redirectToLogin() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        try {
            externalContext.redirect(externalContext.getRequestContextPath() + "/pages/login.xhtml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showErrorMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", message));
    }

    private void showSuccessMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", message));
    }

    /**
     * Cuando se cambia el mes o año desde los dropdowns
     */
    public void onPeriodChange() {
        // Validar que no sea fecha futura
        LocalDate selected = LocalDate.of(currentYear, currentMonth, 1);
        LocalDate today = LocalDate.now();

        if (selected.isAfter(today.withDayOfMonth(1))) {
            // Si seleccionó fecha futura, volver al mes actual
            showErrorMessage("No puedes seleccionar un mes futuro");
            LocalDate now = LocalDate.now();
            this.currentYear = now.getYear();
            this.currentMonth = now.getMonthValue();
        }

        loadTransactions();
    }

    /**
     * Obtener lista de meses para el dropdown
     */
    public List<SelectItem> getMonthOptions() {
        List<SelectItem> months = new ArrayList<>();
        String[] monthNames = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};

        LocalDate now = LocalDate.now();

        for (int i = 0; i < 12; i++) {
            int monthValue = i + 1;

            // Si es el año actual, solo mostrar meses hasta el actual
            if (currentYear == now.getYear() && monthValue > now.getMonthValue()) {
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

        // Últimos 5 años hacia atrás + año actual
        for (int i = currentYearNow; i >= currentYearNow - 5; i--) {
            years.add(new SelectItem(i, String.valueOf(i)));
        }

        return years;
    }

}
