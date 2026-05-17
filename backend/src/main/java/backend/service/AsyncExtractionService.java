package backend.service;

import backend.entity.Document;
import backend.entity.ExtractionResult;
import backend.entity.ExtractionStatus;
import backend.repository.DocumentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AsyncExtractionService {

    private final DocumentRepository documentRepository;
    private final AiExtractionService aiExtractionService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AsyncExtractionService(DocumentRepository documentRepository,
                                  AiExtractionService aiExtractionService) {
        this.documentRepository = documentRepository;
        this.aiExtractionService = aiExtractionService;
    }

    @Async
    public void processDocument(Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        ExtractionResult result = document.getExtractionResult();

        try {
            result.setStatus(ExtractionStatus.PROCESSING);
            result.setExtractedData("{}");
            documentRepository.save(document);

            String aiResponse = aiExtractionService.extract(
                    document.getStoragePath(),
                    document.getFileType()
            );

            System.out.println("ASYNC AI RESPONSE = " + aiResponse);

            if (isValidExtraction(aiResponse)) {

                result.setExtractedData(aiResponse);
                result.setStatus(ExtractionStatus.COMPLETED);

            } else {

                result.setExtractedData(
                        aiResponse != null
                                ? aiResponse
                                : "{\"status\":\"INVALID_AI_RESPONSE\"}"
                );

                result.setStatus(ExtractionStatus.FAILED);
            }

            documentRepository.save(document);

        } catch (Exception e) {
            result.setExtractedData("{\"status\":\"AI_EXTRACTION_FAILED\"}");
            result.setStatus(ExtractionStatus.FAILED);
            documentRepository.save(document);

            System.out.println("==== ASYNC EXTRACTION ERROR ====");
            e.printStackTrace();
            System.out.println("================================");
        }
    }

    private boolean isValidExtraction(String aiResponse) {
        if (aiResponse == null || aiResponse.isBlank()) {
            return false;
        }

        if (!aiResponse.startsWith("{")) {
            return false;
        }

        if (aiResponse.contains("GEMINI_CALL_FAILED")
                || aiResponse.contains("INVALID_AI_RESPONSE")
                || aiResponse.contains("EMPTY_GEMINI_RESPONSE")
                || aiResponse.contains("AI_EXTRACTION_FAILED")) {
            return false;
        }

        try {
            Map<String, Object> data = objectMapper.readValue(aiResponse, Map.class);

            Object documentType = data.get("documentType");

            return documentType instanceof String
                    && !((String) documentType).isBlank()
                    && !"UNKNOWN".equals(documentType);

        } catch (Exception e) {
            return false;
        }
    }
}