package backend.dto;

import backend.entity.ExtractionResult;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ExtractionResultDto {

    private Long id;
    private Object extractedData;

    public ExtractionResultDto(ExtractionResult entity) {
        this.id = entity.getId();

        try {
            ObjectMapper mapper = new ObjectMapper();
            this.extractedData = mapper.readValue(
                    entity.getExtractedData(),
                    Object.class
            );
        } catch (Exception e) {
            e.printStackTrace();
            this.extractedData = null;
        }
    }

    public Long getId() {
        return id;
    }

    public Object getExtractedData() {
        return extractedData;
    }
}