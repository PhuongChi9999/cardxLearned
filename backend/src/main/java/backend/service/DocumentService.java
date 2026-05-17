package backend.service;

import backend.entity.ClientDossier;
import backend.entity.Document;
import backend.entity.ExtractionResult;
import backend.repository.ClientDossierRepository;
import backend.repository.DocumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Set;
import java.nio.file.Paths;

@Service
public class DocumentService {

    private static final String UPLOAD_DIR = "uploads";

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "application/pdf",
            "image/jpeg",
            "image/png"
    );

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    private final ClientDossierRepository dossierRepository;
    private final DocumentRepository documentRepository;
    private final AsyncExtractionService asyncExtractionService;

    public DocumentService(ClientDossierRepository dossierRepository,
                           DocumentRepository documentRepository,
                           AsyncExtractionService asyncExtractionService) {
        this.dossierRepository = dossierRepository;
        this.documentRepository = documentRepository;
        this.asyncExtractionService = asyncExtractionService;
    }

    public Document uploadDocument(MultipartFile file, Long dossierId) {

        ClientDossier dossier = dossierRepository.findById(dossierId)
                .orElseThrow(() -> new RuntimeException("Dossier not found"));

        validateUploadedFile(file);

        try {
            Files.createDirectories(Path.of(UPLOAD_DIR));

            String originalFilename = file.getOriginalFilename();
            String cleanFilename = sanitizeFilename(originalFilename);
            String safeFilename = System.currentTimeMillis() + "_" + cleanFilename;
            Path storagePath = Path.of(UPLOAD_DIR, safeFilename);

            Files.copy(
                    file.getInputStream(),
                    storagePath,
                    StandardCopyOption.REPLACE_EXISTING
            );

            Document document = new Document(
                    originalFilename,
                    file.getContentType(),
                    storagePath.toString(),
                    file.getSize(),
                    dossier
            );

            ExtractionResult extractionResult = new ExtractionResult("{}", document);
            document.setExtractionResult(extractionResult);

            dossier.addDocument(document);
            Document savedDocument = documentRepository.save(document);

            asyncExtractionService.processDocument(savedDocument.getId());

            return savedDocument;

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

    public void deleteDocument(Long id) {

        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        try {

            if (document.getStoragePath() != null) {
                Files.deleteIfExists(Paths.get(document.getStoragePath()));
            }

        } catch (IOException e) {
            System.out.println("Could not delete physical file: " + e.getMessage());
        }

        documentRepository.delete(document);
    }

    public Document extractData(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        asyncExtractionService.processDocument(document.getId());

        return document;
    }

    public ExtractionResult getExtractionResult(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        return document.getExtractionResult();
    }

    private void validateUploadedFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Uploaded file is empty");
        }

        String contentType = file.getContentType();

        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new RuntimeException("Only PDF, JPG and PNG files are accepted");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("File size must not exceed 10 MB");
        }
    }

    private String sanitizeFilename(String filename) {
        if (filename == null || filename.isBlank()) {
            return "document.pdf";
        }

        return filename
                .replace("\\", "_")
                .replace("/", "_")
                .replace("..", "_")
                .replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}