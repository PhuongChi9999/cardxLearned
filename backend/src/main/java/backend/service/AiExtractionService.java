package backend.service;

import org.springframework.stereotype.Service;

@Service
public class AiExtractionService {

    //private final OpenAiClientService openAiClientService;
    private final LocalAiClientService openAiClientService;

   /* public AiExtractionService(OpenAiClientService openAiClientService) {
        this.openAiClientService = openAiClientService;
    }*/

   public AiExtractionService(LocalAiClientService openAiClientService) {
        this.openAiClientService = openAiClientService;
    }

    public String extract(String filename) {
        return openAiClientService.extractClientData(filename);
    }
}