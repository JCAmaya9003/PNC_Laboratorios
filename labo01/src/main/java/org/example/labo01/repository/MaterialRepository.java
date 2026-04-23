package org.example.labo01.repository;

import lombok.RequiredArgsConstructor;
import org.example.labo01.common.MaterialList;
import org.example.labo01.domain.entity.Material;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MaterialRepository {
    private final MaterialList materialList;

    public List<Material> findAll() {
        return materialList.getProducts();
    }
}
