package backend.service;

import org.springframework.stereotype.Service;

@Service
public class AiExtractionService {

    private final GeminiClientService geminiClientService;

    public AiExtractionService(GeminiClientService geminiClientService) {
        this.geminiClientService = geminiClientService;
    }

    public String extract(String filePath, String mimeType) {
        return geminiClientService.extract(filePath, mimeType);
    }
}