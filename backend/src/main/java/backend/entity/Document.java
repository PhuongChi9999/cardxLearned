package backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;

    private String fileType;

    private String storagePath;

    private Long fileSize;

    private LocalDateTime uploadedAt;

    @ManyToOne
    @JoinColumn(name = "dossier_id")
    @JsonBackReference
    private ClientDossier dossier;

    @OneToOne(mappedBy = "document", cascade = CascadeType.ALL)
    private ExtractionResult extractionResult;

    protected Document() {}

    public Document(String filename, String fileType, String storagePath, Long fileSize, ClientDossier dossier) {
        this.filename = filename;
        this.fileType = fileType;
        this.storagePath = storagePath;
        this.fileSize = fileSize;
        this.dossier = dossier;
        this.uploadedAt = LocalDateTime.now();
    }

    public void setExtractionResult(ExtractionResult extractionResult) {
        this.extractionResult = extractionResult;
        extractionResult.setDocument(this);
    }

    public Long getId() { return id; }
    public String getFilename() { return filename; }
    public String getFileType() { return fileType; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public ClientDossier getDossier() { return dossier; }
    public ExtractionResult getExtractionResult() { return extractionResult; }
    public void setDossier(ClientDossier dossier) {
        this.dossier = dossier;
    }

    public String getStoragePath() { return storagePath; }
    public Long getFileSize() { return fileSize; }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
}