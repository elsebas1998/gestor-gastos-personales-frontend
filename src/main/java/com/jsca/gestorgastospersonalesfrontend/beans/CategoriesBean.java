package com.jsca.gestorgastospersonalesfrontend.beans;

import com.jsca.gestorgastospersonalesfrontend.models.CategoryDTO;
import com.jsca.gestorgastospersonalesfrontend.services.CategoryService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("categoriesBean")
@ViewScoped
public class CategoriesBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private SessionBean sessionBean;

    @Inject
    private CategoryService categoryService;

    private List<CategoryDTO> categories;
    private CategoryDTO selectedCategory;
    private String filterType = "ALL"; // ALL, INGRESO, EGRESO

    @PostConstruct
    public void init() {
        if (!sessionBean.isAuthenticated()) {
            redirectToLogin();
            return;
        }

        // Inicializar selectedCategory para evitar NullPointerException
        this.selectedCategory = new CategoryDTO();
        this.selectedCategory.setType("EGRESO");
        this.selectedCategory.setIsFixed(false);

        loadCategories();
    }

    /**
     * Cargar categorías según el filtro
     */
    public void loadCategories() {
        try {
            Long userId = sessionBean.getCurrentUserId();

            if ("ALL".equals(filterType)) {
                categories = categoryService.getCategoriesByUser(userId);
            } else {
                categories = categoryService.getCategoriesByType(userId, filterType);
            }

        } catch (IOException e) {
            showErrorMessage("Error al cargar categorías: " + e.getMessage());
            categories = new ArrayList<>();
        }
    }

    /**
     * Abrir diálogo para nueva categoría
     */
    public void openNewDialog() {
        selectedCategory = new CategoryDTO();
        selectedCategory.setType("EGRESO");
        selectedCategory.setIsFixed(false);
        selectedCategory.setIcon("pi pi-tag");
        selectedCategory.setColor("#3498db");
    }

    /**
     * Abrir diálogo para editar categoría
     */
    public void openEditDialog(CategoryDTO category) {
        selectedCategory = new CategoryDTO();
        selectedCategory.setCategoryId(category.getCategoryId());
        selectedCategory.setName(category.getName());
        selectedCategory.setType(category.getType());
        selectedCategory.setIsFixed(category.getIsFixed());
        selectedCategory.setIcon(category.getIcon());
        selectedCategory.setColor(category.getColor());
        selectedCategory.setDescription(category.getDescription());
    }

    /**
     * Guardar categoría (crear o actualizar)
     */
    public void saveCategory() {
        try {
            Long userId = sessionBean.getCurrentUserId();

            if (selectedCategory.getCategoryId() == null) {
                // Crear nueva
                categoryService.createCategory(userId, selectedCategory);
                showSuccessMessage("Categoría creada exitosamente");
            } else {
                // Actualizar existente
                categoryService.updateCategory(userId,
                        selectedCategory.getCategoryId(),
                        selectedCategory);
                showSuccessMessage("Categoría actualizada exitosamente");
            }

            loadCategories();

        } catch (IOException e) {
            showErrorMessage("Error al guardar categoría: " + e.getMessage());
        }
    }

    /**
     * Eliminar categoría
     */
    public void deleteCategory(CategoryDTO category) {
        try {
            Long userId = sessionBean.getCurrentUserId();
            categoryService.deleteCategory(userId, category.getCategoryId());

            showSuccessMessage("Categoría eliminada exitosamente");
            loadCategories();

        } catch (IOException e) {
            showErrorMessage("Error al eliminar categoría: " + e.getMessage());
        }
    }

    /**
     * Filtrar por tipo
     */
    public void filterByType() {
        loadCategories();
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

    // ========== GETTERS Y SETTERS ==========

    public List<CategoryDTO> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryDTO> categories) {
        this.categories = categories;
    }

    public CategoryDTO getSelectedCategory() {
        return selectedCategory;
    }

    public void setSelectedCategory(CategoryDTO selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    public String getFilterType() {
        return filterType;
    }

    public void setFilterType(String filterType) {
        this.filterType = filterType;
    }
}
