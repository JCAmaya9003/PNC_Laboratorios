package com.server.app.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "planes_pago")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class PlanPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_cuota", nullable = false)
    private Integer numeroCuota;

    @Column(name = "monto_capital", nullable = false, precision = 15, scale = 2)
    private BigDecimal montoCapital;

    @Column(name = "monto_interes", nullable = false, precision = 15, scale = 2)
    private BigDecimal montoInteres;

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoCuota estado;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "prestamo_id", nullable = false)
    private Prestamo prestamo;
}
