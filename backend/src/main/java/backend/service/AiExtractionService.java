package backend.service;

import org.springframework.stereotype.Service;

@Service
public class AiExtractionService {

    private final LocalAiClientService localAiClientService;

    public AiExtractionService(LocalAiClientService localAiClientService) {
        this.localAiClientService = localAiClientService;
    }

    public String extract(String filename) {
        return localAiClientService.extractClientData(filename);
    }
}