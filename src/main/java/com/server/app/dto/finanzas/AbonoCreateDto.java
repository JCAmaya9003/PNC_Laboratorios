package com.server.app.dto.finanzas;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;


@Data
public class AbonoCreateDto {

    @NotNull(message = "El id de la cuota es obligatorio")
    @Positive(message = "El id de la cuota debe ser un número positivo")
    private Long planPagoId;
}
