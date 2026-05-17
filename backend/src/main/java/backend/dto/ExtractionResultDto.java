package backend.dto;

import backend.entity.ExtractionResult;
import backend.entity.ExtractionStatus;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class ExtractionResultDto {

    private Long id;
    private ExtractionStatus status;
    private Object extractedData;

    public ExtractionResultDto(ExtractionResult entity) {
        this.id = entity.getId();
        this.status = entity.getStatus();

        try {
            ObjectMapper mapper = new ObjectMapper();

            String data = entity.getExtractedData();

            if (data == null || data.isBlank()) {
                this.extractedData = Map.of();
            } else {
                this.extractedData = mapper.readValue(data, Object.class);
            }

        } catch (Exception e) {
            e.printStackTrace();
            this.extractedData = Map.of(
                    "status", "INVALID_STORED_EXTRACTION_DATA"
            );
        }
    }

    public Long getId() {
        return id;
    }

    public ExtractionStatus getStatus() {
        return status;
    }

    public Object getExtractedData() {
        return extractedData;
    }
}