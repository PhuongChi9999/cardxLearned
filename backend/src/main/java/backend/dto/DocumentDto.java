package backend.dto;

import backend.entity.Document;

import java.time.LocalDateTime;

public class DocumentDto {

    private Long id;
    private String filename;
    private String fileType;
    private LocalDateTime uploadedAt;

    private ExtractionResultDto extractionResult;

    public DocumentDto(Document doc) {
        this.id = doc.getId();
        this.filename = doc.getFilename();
        this.fileType = doc.getFileType();
        this.uploadedAt = doc.getUploadedAt();

        if (doc.getExtractionResult() != null) {
            this.extractionResult = new ExtractionResultDto(doc.getExtractionResult());
        }
    }

    public Long getId() { return id; }
    public String getFilename() { return filename; }
    public String getFileType() { return fileType; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }

    public ExtractionResultDto getExtractionResult() {
        return extractionResult;
    }
}