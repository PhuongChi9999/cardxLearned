package backend.controller;

import backend.dto.DocumentDto;
import backend.dto.ExtractionResultDto;
import backend.entity.Document;
import backend.service.DocumentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping("/upload")
    public ResponseEntity<DocumentDto> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("dossierId") Long dossierId
    ) {
        Document document = documentService.uploadDocument(file, dossierId);
        return ResponseEntity.ok(new DocumentDto(document));
    }

    @GetMapping
    public ResponseEntity<List<DocumentDto>> getAllDocuments() {
        List<DocumentDto> documents = documentService.getAllDocuments()
                .stream()
                .map(DocumentDto::new)
                .toList();

        return ResponseEntity.ok(documents);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentDto> getDocumentById(@PathVariable Long id) {
        Document document = documentService.getDocumentById(id);
        return ResponseEntity.ok(new DocumentDto(document));
    }

    @PostMapping("/{id}/extract")
    public ResponseEntity<DocumentDto> extractData(@PathVariable Long id) {
        Document document = documentService.extractData(id);
        return ResponseEntity.ok(new DocumentDto(document));
    }

    @GetMapping("/{id}/result")
    public ResponseEntity<ExtractionResultDto> getExtractionResult(@PathVariable Long id) {
        return ResponseEntity.ok(new ExtractionResultDto(documentService.getExtractionResult(id)));
    }
}