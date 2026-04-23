package org.example.labo01;

import org.example.labo01.service.MaterialService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Labo01Application {

    public static void main(String[] args) {
        SpringApplication.run(Labo01Application.class, args);
    }
    @Bean
    public CommandLineRunner run(MaterialService materialService) {

        return args -> {
            System.out.println("=== INICIANDO APP ===");
            materialService.getAllMaterialsSorted().forEach(material ->
                    System.out.println("[HYRULE-DB] Nombre: " + material.getNombre() + " | Categoría: " + material.getCategoria() + " | Precio: " + material.getPrecio() + " Rupias")
            );

        };
    }
}
