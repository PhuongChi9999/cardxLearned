package backend.controller;

import backend.dto.ClientDossierDto;
import backend.service.ClientDossierService;
import backend.dto.ClientDossierDetailDto;
import backend.dto.CreateDossierRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dossiers")
@CrossOrigin(origins = "http://localhost:4200, http://localhost")
public class ClientDossierController {

    private final ClientDossierService service;

    public ClientDossierController(ClientDossierService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<ClientDossierDto>> getAllDossiers() {
        return ResponseEntity.ok(service.getAllDossiers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientDossierDetailDto> getDossierById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getDossierById(id));
    }

    @PostMapping
    public ResponseEntity<ClientDossierDto> createDossier(@RequestBody CreateDossierRequest request) {
        return ResponseEntity.ok(service.createDossier(request));
    }
}