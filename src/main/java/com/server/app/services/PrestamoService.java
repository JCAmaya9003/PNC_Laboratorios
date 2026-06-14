package com.server.app.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.server.app.dto.finanzas.PrestamoConPlanDto;
import com.server.app.dto.finanzas.PrestamoCreateDto;
import com.server.app.dto.finanzas.ResumenCreditoDto;
import com.server.app.entities.EstadoCuota;
import com.server.app.entities.EstadoPrestamo;
import com.server.app.entities.PlanPago;
import com.server.app.entities.Prestamo;
import com.server.app.entities.User;
import com.server.app.exceptions.ForbiddenException;
import com.server.app.exceptions.NotFoundException;
import com.server.app.repositories.AbonoRepository;
import com.server.app.repositories.PlanPagoRepository;
import com.server.app.repositories.PrestamoRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PrestamoService {

    private static final int ESCALA_INTERNA = 10;

    private static final BigDecimal TASA_MORA_MENSUAL = new BigDecimal("0.05");

    private final PrestamoRepository prestamoRepository;
    private final PlanPagoRepository planPagoRepository;
    private final AbonoRepository abonoRepository;

    @Transactional
    public PrestamoConPlanDto solicitar(User usuario, PrestamoCreateDto dto) {

        Prestamo prestamo = Prestamo.builder()
                .capitalSolicitado(dto.getCapitalSolicitado())
                .tasaInteresAnual(dto.getTasaInteresAnual())
                .plazoMeses(dto.getPlazoMeses())
                .estado(EstadoPrestamo.APROBADO)
                .fechaSolicitud(LocalDate.now())
                .usuario(usuario)
                .build();

        prestamo = prestamoRepository.save(prestamo);

        List<PlanPago> planes = generarPlanAmortizacion(prestamo);
        planes = planPagoRepository.saveAll(planes);

        return new PrestamoConPlanDto(prestamo, planes);
    }

    private List<PlanPago> generarPlanAmortizacion(Prestamo prestamo) {

        BigDecimal capital = prestamo.getCapitalSolicitado().setScale(2, RoundingMode.HALF_UP);
        int n = prestamo.getPlazoMeses();

        BigDecimal tasaMensual = prestamo.getTasaInteresAnual()
                .divide(BigDecimal.valueOf(100), ESCALA_INTERNA, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(12), ESCALA_INTERNA, RoundingMode.HALF_UP);

        BigDecimal cuotaFija;
        if (tasaMensual.compareTo(BigDecimal.ZERO) == 0) {
            cuotaFija = capital.divide(BigDecimal.valueOf(n), 2, RoundingMode.HALF_UP);
        } else {
            BigDecimal factor = BigDecimal.ONE.add(tasaMensual).pow(n);
            cuotaFija = capital.multiply(tasaMensual).multiply(factor)
                    .divide(factor.subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP);
        }

        List<PlanPago> planes = new ArrayList<>();
        BigDecimal saldo = capital;
        LocalDate base = prestamo.getFechaSolicitud();

        for (int k = 1; k <= n; k++) {
            BigDecimal interes = saldo.multiply(tasaMensual).setScale(2, RoundingMode.HALF_UP);
            BigDecimal abonoCapital = cuotaFija.subtract(interes).setScale(2, RoundingMode.HALF_UP);

            if (k == n) {
                abonoCapital = saldo;
            }

            saldo = saldo.subtract(abonoCapital).setScale(2, RoundingMode.HALF_UP);

            PlanPago cuota = PlanPago.builder()
                    .numeroCuota(k)
                    .montoCapital(abonoCapital)
                    .montoInteres(interes)
                    .fechaVencimiento(base.plusMonths(k))
                    .estado(EstadoCuota.PENDIENTE)
                    .prestamo(prestamo)
                    .build();

            planes.add(cuota);
        }

        return planes;
    }

    @Transactional(readOnly = true)
    public Page<Prestamo> listarPorUsuario(int usuarioId, int page, int size) {
        return prestamoRepository.findByUsuarioId(usuarioId, PageRequest.of(page, size));
    }

    @Transactional(readOnly = true)
    public Page<PlanPago> cuotasPendientes(Long prestamoId, int usuarioId, int page, int size) {
        Prestamo prestamo = prestamoRepository.findById(prestamoId)
                .orElseThrow(() -> new NotFoundException("Préstamo no encontrado"));

        if (prestamo.getUsuario().getId() != usuarioId) {
            throw new ForbiddenException("No tienes acceso a este préstamo");
        }

        return planPagoRepository.findByPrestamoIdAndEstado(
                prestamoId, EstadoCuota.PENDIENTE, PageRequest.of(page, size));
    }

    @Transactional(readOnly = true)
    public ResumenCreditoDto resumenCredito(int usuarioId) {

        List<Prestamo> prestamos = prestamoRepository.findByUsuarioId(usuarioId);
        List<PlanPago> planes = planPagoRepository.findByPrestamo_Usuario_Id(usuarioId);

        long cantidadPrestamos = prestamos.size();
        long prestamosActivos = prestamos.stream()
                .filter(p -> p.getEstado() == EstadoPrestamo.APROBADO).count();
        long prestamosPagados = prestamos.stream()
                .filter(p -> p.getEstado() == EstadoPrestamo.PAGADO).count();

        LocalDate hoy = LocalDate.now();

        BigDecimal capitalPendiente = BigDecimal.ZERO;
        BigDecimal interesPendiente = BigDecimal.ZERO;
        BigDecimal moraPendiente = BigDecimal.ZERO;
        long cuotasPendientes = 0;
        long cuotasVencidas = 0;

        for (PlanPago cuota : planes) {
            if (cuota.getEstado() == EstadoCuota.PENDIENTE) {
                capitalPendiente = capitalPendiente.add(cuota.getMontoCapital());
                interesPendiente = interesPendiente.add(cuota.getMontoInteres());
                cuotasPendientes++;

                long dias = ChronoUnit.DAYS.between(cuota.getFechaVencimiento(), hoy);
                if (dias > 0) {
                    cuotasVencidas++;
                    BigDecimal montoCuota = cuota.getMontoCapital().add(cuota.getMontoInteres());
                    BigDecimal mora = montoCuota
                            .multiply(TASA_MORA_MENSUAL)
                            .multiply(BigDecimal.valueOf(dias))
                            .divide(BigDecimal.valueOf(30), 2, RoundingMode.HALF_UP);
                    moraPendiente = moraPendiente.add(mora);
                }
            }
        }

        BigDecimal totalPagado = abonoRepository.findByPlanPago_Prestamo_Usuario_Id(usuarioId)
                .stream()
                .map(a -> a.getMonto())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal deudaTotalPendiente = capitalPendiente.add(interesPendiente);

        return new ResumenCreditoDto(
                cantidadPrestamos,
                prestamosActivos,
                prestamosPagados,
                capitalPendiente.setScale(2, RoundingMode.HALF_UP),
                interesPendiente.setScale(2, RoundingMode.HALF_UP),
                deudaTotalPendiente.setScale(2, RoundingMode.HALF_UP),
                moraPendiente.setScale(2, RoundingMode.HALF_UP),
                totalPagado.setScale(2, RoundingMode.HALF_UP),
                cuotasPendientes,
                cuotasVencidas
        );
    }
}
