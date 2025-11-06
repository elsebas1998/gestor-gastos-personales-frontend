package com.jsca.gestorgastospersonalesfrontend.converters;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.convert.FacesConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@FacesConverter(value = "localDateConverter")
public class LocalDateConverter implements Converter<LocalDate> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public LocalDate getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(value, FORMATTER);
        } catch (Exception e) {
            throw new ConverterException("Error al convertir la fecha: " + value, e);
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, LocalDate value) {
        if (value == null) {
            return "";
        }
        return value.format(FORMATTER);
    }
}
