package backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;


@Service
public class SharpAiClientService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SharpAiClientService(@Value("${sharpapi.api.key}") String apiKey) {
        this.restClient = RestClient.builder()
                .baseUrl("https://api.apyhub.com")
                .defaultHeader("apy-token", apiKey)
                .build();
    }

    public String extractClientData(String filename) {
        try {
            FileSystemResource file = new FileSystemResource(filename);

            LinkedMultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", file);
            body.add("language", "English");

            Map response = restClient.post()
                    .uri("/sharpapi/api/v1/hr/parse_resume")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(body)
                    .retrieve()
                    .body(Map.class);

            return objectMapper.writeValueAsString(response);

        } catch (Exception e) {
            System.out.println("==== SHARPAPI ERROR ====");
            e.printStackTrace();
            System.out.println("========================");

            return "{\"status\":\"SHARPAPI_CALL_FAILED\"}";
        }
    }
}