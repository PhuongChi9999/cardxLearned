package backend.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "extraction_results")
public class ExtractionResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String extractedData;

    @OneToOne
    @JoinColumn(name = "document_id")
    @JsonIgnore
    private Document document;

    protected ExtractionResult() {}

    public ExtractionResult(String extractedData, Document document) {
        this.extractedData = extractedData;
        this.document = document;
    }

    public Long getId() {
        return id;
    }

    public String getExtractedData() {
        return extractedData;
    }

    public void setExtractedData(String extractedData) {
        this.extractedData = extractedData;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }
}