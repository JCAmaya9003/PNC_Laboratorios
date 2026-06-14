package com.server.app.dto.finanzas;

import java.math.BigDecimal;

public record ResumenCreditoDto(
        long cantidadPrestamos,
        long prestamosActivos,
        long prestamosPagados,
        BigDecimal capitalPendiente,
        BigDecimal interesPendiente,
        BigDecimal deudaTotalPendiente,
        BigDecimal moraPendiente,
        BigDecimal totalPagado,
        long cuotasPendientes,
        long cuotasVencidas
) {
}
