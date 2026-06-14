package com.server.app.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.server.app.entities.EstadoCuota;
import com.server.app.entities.PlanPago;

public interface PlanPagoRepository extends JpaRepository<PlanPago, Long> {

    Page<PlanPago> findByPrestamoIdAndEstado(Long prestamoId, EstadoCuota estado, Pageable pageable);

    Page<PlanPago> findByPrestamoId(Long prestamoId, Pageable pageable);

    long countByPrestamoIdAndEstado(Long prestamoId, EstadoCuota estado);

    List<PlanPago> findByPrestamo_Usuario_Id(Integer usuarioId);
}
