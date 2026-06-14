package com.server.app.controllers;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.server.app.dto.finanzas.AbonoCreateDto;
import com.server.app.dto.finanzas.PrestamoConPlanDto;
import com.server.app.dto.finanzas.PrestamoCreateDto;
import com.server.app.dto.finanzas.ResumenCreditoDto;
import com.server.app.dto.response.Pagination;
import com.server.app.dto.response.PaginationMeta;
import com.server.app.entities.Abono;
import com.server.app.entities.PlanPago;
import com.server.app.entities.Prestamo;
import com.server.app.entities.User;
import com.server.app.services.AbonoService;
import com.server.app.services.PrestamoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/finanzas")
public class FinanzasController {

    private final PrestamoService prestamoService;
    private final AbonoService abonoService;

    public FinanzasController(PrestamoService prestamoService, AbonoService abonoService) {
        this.prestamoService = prestamoService;
        this.abonoService = abonoService;
    }

    @GetMapping("/prestamos")
    public ResponseEntity<Pagination<Prestamo>> listarPrestamos(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Prestamo> p = prestamoService.listarPorUsuario(user.getId(), page, size);
        return ResponseEntity.ok(new Pagination<>(
                p.getContent(),
                new PaginationMeta(p.getNumber(), p.getSize(), p.getTotalPages(), p.getTotalElements())
        ));
    }

    @PostMapping("/prestamos")
    public ResponseEntity<PrestamoConPlanDto> solicitarPrestamo(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody PrestamoCreateDto dto) {

        return ResponseEntity.ok(prestamoService.solicitar(user, dto));
    }

    @GetMapping("/prestamos/{id}/planes-pago")
    public ResponseEntity<Pagination<PlanPago>> planesPago(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<PlanPago> p = prestamoService.cuotasPendientes(id, user.getId(), page, size);
        return ResponseEntity.ok(new Pagination<>(
                p.getContent(),
                new PaginationMeta(p.getNumber(), p.getSize(), p.getTotalPages(), p.getTotalElements())
        ));
    }

    @PostMapping("/abonos")
    public ResponseEntity<Abono> registrarAbono(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody AbonoCreateDto dto) {

        return ResponseEntity.ok(abonoService.registrar(user, dto));
    }

    @GetMapping("/resumen-credito")
    public ResponseEntity<ResumenCreditoDto> resumenCredito(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(prestamoService.resumenCredito(user.getId()));
    }
}
