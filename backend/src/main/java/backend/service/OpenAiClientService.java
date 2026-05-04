package backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class OpenAiClientService {

    private final RestClient restClient;
    private final String model;

    public OpenAiClientService(
            @Value("${openai.api.key}") String apiKey,
            @Value("${openai.model}") String model
    ) {
        this.model = model;
        this.restClient = RestClient.builder()
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    //public String extractClientData(String filename) {
        //return "{\"clientName\":\"Mock User\",\"email\":null,\"phone\":null,\"amount\":1000,\"status\":\"MOCK_AI_FALLBACK\"}";
    //}

    public String extractClientData(String filename) {
        Map<String, Object> request = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "user",
                                "content", "Extract client info from file: " + filename)
                )
        );

        try {
            Map response = restClient.post()
                    .uri("/chat/completions")
                    .body(request)
                    .retrieve()
                    .body(Map.class);

            List choices = (List) response.get("choices");
            Map choice = (Map) choices.get(0);
            Map message = (Map) choice.get("message");

            return (String) message.get("content");

        } catch (Exception e) {
            System.out.println("==== OPENAI ERROR ====");
            e.printStackTrace();   // 👈 DÒNG QUAN TRỌNG
            System.out.println("======================");

            return "{\"status\":\"AI_CALL_FAILED\"}";
        }
    }
}