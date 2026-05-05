package backend.service;

import org.springframework.stereotype.Service;

@Service
public class AiExtractionService {

    private final SharpAiClientService sharpAiClientService;


    public AiExtractionService(SharpAiClientService sharpAiClientService) {
        this.sharpAiClientService = sharpAiClientService;
    }

    public String extract(String filename) {
        return sharpAiClientService.extractClientData(filename);
    }
}