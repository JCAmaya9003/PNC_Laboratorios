package org.example.labo01.service;

import lombok.AllArgsConstructor;
import org.example.labo01.domain.entity.Material;
import org.example.labo01.repository.MaterialRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class MaterialService {
    private final MaterialRepository materialRepository;

    public List<Material> getAllMaterialsSorted(){
        return materialRepository.findAll().stream()
                .sorted((m1, m2) -> Double.compare(m2.getPrecio(), m1.getPrecio()))
                .toList();
    }

    public Material getExpensiveMaterial(){
        return getAllMaterialsSorted().getFirst();
    }

    public List<Material> getLegendaryMaterials(){
        return materialRepository.findAll().stream()
                .filter(obj -> obj.getRareza() == "Legendario")
                .toList();
    }

    public List<String> getAllLocations(){
        return materialRepository.findAll().stream()
                .map(Material::getUbicacion)
                .distinct()
                .toList();
    }
}
