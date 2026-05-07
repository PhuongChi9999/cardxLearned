package backend.service;

import backend.entity.ClientDossier;
import backend.entity.Document;
import backend.entity.ExtractionResult;
import backend.repository.ClientDossierRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import java.util.List;
import backend.repository.DocumentRepository;
import jakarta.annotation.PostConstruct;

@Service
public class DocumentService {

    private static final String UPLOAD_DIR = "uploads";

    private final ClientDossierRepository dossierRepository;

    private final DocumentRepository documentRepository;

    private final AiExtractionService aiExtractionService;

    @Value("${app.upload.dir}")
    private String uploadDirProperty;

    public DocumentService(ClientDossierRepository dossierRepository,
                           DocumentRepository documentRepository,
                           AiExtractionService aiExtractionService) {
        this.dossierRepository = dossierRepository;
        this.documentRepository = documentRepository;
        this.aiExtractionService = aiExtractionService;
    }

    @PostConstruct
    public void init() {
        try {
            Path uploadDir = Paths.get(uploadDirProperty);
            Files.createDirectories(uploadDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Document uploadDocument(MultipartFile file, Long dossierId) {
        ClientDossier dossier = dossierRepository.findById(dossierId)
                .orElseThrow(() -> new RuntimeException("Dossier not found"));

        try {
            String originalFilename = file.getOriginalFilename();
            String safeFilename = System.currentTimeMillis() + "_" + originalFilename;
            Path storagePath = Path.of(uploadDirProperty, safeFilename);

            Files.copy(file.getInputStream(), storagePath, StandardCopyOption.REPLACE_EXISTING);

            Document document = new Document(
                    originalFilename,
                    file.getContentType(),
                    storagePath.toString(),
                    file.getSize(),
                    dossier
            );

            ExtractionResult extractionResult = new ExtractionResult(
                    "{\"status\":\"PENDING_AI_EXTRACTION\"}",
                    document
            );

            document.setExtractionResult(extractionResult);
            dossier.addDocument(document);

            ClientDossier savedDossier = dossierRepository.save(dossier);

            return savedDossier.getDocuments()
                    .get(savedDossier.getDocuments().size() - 1);

        } catch (IOException e) {
            throw new RuntimeException("Could not store uploaded file", e);
        }
    }


    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }

    public Document getDocumentById(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));
    }

    public Document extractData(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        String aiResponse = aiExtractionService.extract(document.getStoragePath());
        System.out.println("aiResponse=" + aiResponse);

        ExtractionResult result = document.getExtractionResult();

        String cleaned = aiResponse.replace("```json", "").replace("```", "").trim();

        System.out.println("cleaned=" + cleaned);

        if (cleaned.startsWith("{")) {
            result.setExtractedData(cleaned);
        } else {
            result.setExtractedData("{\"status\":\"INVALID_AI_RESPONSE\"}");
        }

        documentRepository.save(document);

        return document;
    }

    public ExtractionResult getExtractionResult(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        return document.getExtractionResult();
    }
}