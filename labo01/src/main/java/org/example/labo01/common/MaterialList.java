package org.example.labo01.common;

import org.example.labo01.domain.entity.Material;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MaterialList {
    private final List<Material> materials;
    public MaterialList() {
        this.materials = new ArrayList<>();

        this.materials.add(Material.builder()
                .id(1L)
                .nombre("Pimienta Ardiente")
                .categoria("Planta")
                .efecto("Estamina")
                .precio(10)
                .ubicacion("Volcán de Eldin")
                .rareza("Legendario")
                .build());

        this.materials.add(Material.builder()
                .id(2L)
                .nombre("Ámbar rojo")
                .categoria("Mineral")
                .efecto("Ataque")
                .precio(30)
                .ubicacion("Desierto Gerudo")
                .rareza("Legendario")
                .build());

        this.materials.add(Material.builder()
                .id(3L)
                .nombre("Ala de Keese")
                .categoria("Parte de Monstruo")
                .efecto("Corazones")
                .precio(15)
                .ubicacion("Volcán de Eldin")
                .rareza("Legendario")
                .build());

        this.materials.add(Material.builder()
                .id(4L)
                .nombre("FLuorazul")
                .categoria("Planta")
                .efecto("Sigilo")
                .precio(25)
                .ubicacion("Cordillera de Hebra")
                .rareza("Común")
                .build());

        this.materials.add(Material.builder()
                .id(5L)
                .nombre("Diente de vampiro")
                .categoria("Parte de Monstruo")
                .efecto("Corazones")
                .precio(100)
                .ubicacion("Desierto Gerudo")
                .rareza("Raro")
                .build());
    }

    public List<Material> getProducts() {
        return materials;
    }
}
