package com.server.app.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "prestamos")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "capital_solicitado", nullable = false, precision = 15, scale = 2)
    private BigDecimal capitalSolicitado;

    @Column(name = "tasa_interes_anual", nullable = false, precision = 8, scale = 4)
    private BigDecimal tasaInteresAnual;

    @Column(name = "plazo_meses", nullable = false)
    private Integer plazoMeses;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPrestamo estado;

    @Column(name = "fecha_solicitud", nullable = false)
    private LocalDate fechaSolicitud;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private User usuario;
}
