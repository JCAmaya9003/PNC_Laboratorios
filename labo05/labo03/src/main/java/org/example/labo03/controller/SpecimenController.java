package org.example.labo03.controller;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.labo03.domain.dto.request.CreateSpecimenRequest;
import org.example.labo03.domain.dto.request.UpdateSpecimenRequest;
import org.example.labo03.domain.dto.response.SpecimenResponse;
import org.example.labo03.service.SpecimenService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/specimens")
@RequiredArgsConstructor
public class SpecimenController {

    private final SpecimenService specimenService;

    @PostMapping
    public ResponseEntity<SpecimenResponse> create(
            @Valid @RequestBody CreateSpecimenRequest request) {
        return new ResponseEntity<>(
                specimenService.createSpecimen(request),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<Page<SpecimenResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(specimenService.getAllSpecimens(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SpecimenResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(specimenService.getSpecimenById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SpecimenResponse> update(
            @PathVariable UUID id,
            @RequestBody UpdateSpecimenRequest request) {
        return ResponseEntity.ok(specimenService.updateSpecimen(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SpecimenResponse> delete(@PathVariable UUID id) {
        return ResponseEntity.ok(specimenService.deleteSpecimen(id));
    }
}
