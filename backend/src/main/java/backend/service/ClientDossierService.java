package backend.service;

import backend.dto.ClientDossierDto;
import backend.repository.ClientDossierRepository;
import backend.repository.DocumentRepository;

import backend.dto.DocumentDto;
import backend.dto.ClientDossierDetailDto;

import backend.dto.CreateDossierRequest;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientDossierService {

    private final ClientDossierRepository repository;
    private final DocumentRepository documentRepository;

    public ClientDossierService(ClientDossierRepository repository,
                                DocumentRepository documentRepository) {
        this.repository = repository;
        this.documentRepository = documentRepository;
    }

    public List<ClientDossierDto> getAllDossiers() {
        return repository.findAll()
                .stream()
                .map(dossier -> {
                    ClientDossierDto dto = new ClientDossierDto(dossier);
                    dto.setDocumentCount(
                            (int) documentRepository.countByDossierId(dossier.getId())
                    );
                    return dto;
                })
                .toList();
    }

    public ClientDossierDetailDto getDossierById(Long id) {
        var dossier = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dossier not found"));

        var documents = documentRepository.findByDossierId(id)
                .stream()
                .map(DocumentDto::new)
                .toList();

        return new ClientDossierDetailDto(dossier, documents);
    }

    public ClientDossierDto createDossier(CreateDossierRequest request) {

        var dossier = new backend.entity.ClientDossier(
                request.getClientName(),
                request.getEmail(),
                request.getPhone()
        );

        var saved = repository.save(dossier);

        return new ClientDossierDto(saved);
    }
}