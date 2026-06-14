package com.server.app.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.server.app.entities.Prestamo;

public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {

    Page<Prestamo> findByUsuarioId(Integer usuarioId, Pageable pageable);

    List<Prestamo> findByUsuarioId(Integer usuarioId);
}
