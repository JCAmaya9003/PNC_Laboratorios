package com.server.app.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "abonos")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Abono {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal monto;

    @Column(name = "fecha_pago", nullable = false)
    private LocalDate fechaPago;

    @Column(name = "recargo_mora", nullable = false, precision = 15, scale = 2)
    private BigDecimal recargoMora;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "plan_pago_id", nullable = false)
    private PlanPago planPago;
}
