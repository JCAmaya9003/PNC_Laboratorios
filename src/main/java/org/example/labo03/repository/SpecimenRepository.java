package org.example.labo03.repository;

import org.example.labo03.domain.entity.Specimen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface SpecimenRepository extends JpaRepository<Specimen, UUID> {
}