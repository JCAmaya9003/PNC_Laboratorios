package com.server.app.dto.finanzas;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class PrestamoCreateDto {

    @NotNull(message = "El capital solicitado es obligatorio")
    @Positive(message = "El capital solicitado debe ser mayor que 0")
    @Digits(integer = 13, fraction = 2, message = "El capital tiene un formato inválido")
    private BigDecimal capitalSolicitado;

    @NotNull(message = "La tasa de interés anual es obligatoria")
    @PositiveOrZero(message = "La tasa de interés anual no puede ser negativa")
    @DecimalMin(value = "0.0", message = "La tasa de interés anual no puede ser negativa")
    @Digits(integer = 4, fraction = 4, message = "La tasa de interés tiene un formato inválido")
    private BigDecimal tasaInteresAnual;

    @NotNull(message = "El plazo en meses es obligatorio")
    @Min(value = 1, message = "El plazo debe ser de al menos 1 mes")
    @Max(value = 600, message = "El plazo no puede superar los 600 meses")
    private Integer plazoMeses;
}
