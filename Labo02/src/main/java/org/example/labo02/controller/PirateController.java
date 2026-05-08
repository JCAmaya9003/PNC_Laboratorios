package org.example.labo02.controller;

import lombok.AllArgsConstructor;
import org.example.labo02.domain.entity.Pirate;
import org.example.labo02.service.impl.PirateServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pirates")
@AllArgsConstructor
public class PirateController {
    private final PirateServiceImpl pirateService;

    //get by id
    @GetMapping("/{id}")
    public ResponseEntity<Pirate> getPirate(@PathVariable UUID id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pirateService.getPirateById(id));
    }

    //get all
    @GetMapping
    public ResponseEntity<List<Pirate>> getPirates() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pirateService.getPirates());
    }

    //create
    @PostMapping
    public ResponseEntity<Pirate> createPirate(@RequestBody Pirate pirate) {
        pirateService.createPirate(pirate);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(pirate);
    }

    //edit
    @PutMapping("/{id}")
    public ResponseEntity<Pirate> updatePirate(
            @PathVariable UUID id,
            @RequestBody Pirate pirate
    ) {
        pirateService.editPirate(id, pirate);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pirate);
    }

    //delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Pirate> deletePirate(
            @PathVariable UUID id
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pirateService.deletePirate(id));
    }
}
