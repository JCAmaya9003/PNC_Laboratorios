package com.server.app.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.server.app.dto.finanzas.AbonoCreateDto;
import com.server.app.entities.Abono;
import com.server.app.entities.EstadoCuota;
import com.server.app.entities.EstadoPrestamo;
import com.server.app.entities.PlanPago;
import com.server.app.entities.Prestamo;
import com.server.app.entities.User;
import com.server.app.exceptions.ConfictException;
import com.server.app.exceptions.ForbiddenException;
import com.server.app.exceptions.NotFoundException;
import com.server.app.repositories.AbonoRepository;
import com.server.app.repositories.PlanPagoRepository;
import com.server.app.repositories.PrestamoRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AbonoService {

    // Mora: cuota * (tasa_mora_mensual / 30) * dias_de_atraso
    private static final BigDecimal TASA_MORA_MENSUAL = new BigDecimal("0.05");
    private static final BigDecimal DIAS_MES = BigDecimal.valueOf(30);

    private final AbonoRepository abonoRepository;
    private final PlanPagoRepository planPagoRepository;
    private final PrestamoRepository prestamoRepository;

    /**
     * Registra el pago de una cuota: calcula la mora si está vencida, marca la
     * cuota como PAGADO y, si era la última pendiente, marca el préstamo como PAGADO.
     */
    @Transactional
    public Abono registrar(User usuario, AbonoCreateDto dto) {

        PlanPago cuota = planPagoRepository.findById(dto.getPlanPagoId())
                .orElseThrow(() -> new NotFoundException("Cuota no encontrada"));

        Prestamo prestamo = cuota.getPrestamo();

        // La cuota debe pertenecer a un préstamo del usuario autenticado.
        if (prestamo.getUsuario().getId() != usuario.getId()) {
            throw new ForbiddenException("No tienes acceso a esta cuota");
        }

        if (cuota.getEstado() == EstadoCuota.PAGADO) {
            throw new ConfictException("La cuota ya fue pagada");
        }

        BigDecimal montoCuota = cuota.getMontoCapital().add(cuota.getMontoInteres());
        BigDecimal mora = calcularMora(cuota.getFechaVencimiento(), montoCuota);
        BigDecimal montoTotal = montoCuota.add(mora).setScale(2, RoundingMode.HALF_UP);

        Abono abono = Abono.builder()
                .monto(montoTotal)
                .recargoMora(mora)
                .fechaPago(LocalDate.now())
                .planPago(cuota)
                .build();

        abono = abonoRepository.save(abono);

        // Marcar la cuota como pagada.
        cuota.setEstado(EstadoCuota.PAGADO);
        planPagoRepository.save(cuota);

        // Si ya no quedan cuotas pendientes, el préstamo queda PAGADO.
        long pendientes = planPagoRepository.countByPrestamoIdAndEstado(
                prestamo.getId(), EstadoCuota.PENDIENTE);
        if (pendientes == 0) {
            prestamo.setEstado(EstadoPrestamo.PAGADO);
            prestamoRepository.save(prestamo);
        }

        return abono;
    }

    /**
     * Mora = montoCuota * (TASA_MORA_MENSUAL / 30) * diasAtraso.
     * Si la cuota no está vencida (diasAtraso <= 0), la mora es 0.
     */
    private BigDecimal calcularMora(LocalDate fechaVencimiento, BigDecimal montoCuota) {
        long diasAtraso = ChronoUnit.DAYS.between(fechaVencimiento, LocalDate.now());
        if (diasAtraso <= 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return montoCuota
                .multiply(TASA_MORA_MENSUAL)
                .multiply(BigDecimal.valueOf(diasAtraso))
                .divide(DIAS_MES, 2, RoundingMode.HALF_UP);
    }
}
