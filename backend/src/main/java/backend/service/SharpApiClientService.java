package backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;

@Service
public class SharpApiClientService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${sharpapi.api.key}")
    private String apiKey;

    public String extractClientData(String filename) {
        try {
            String submitResponse = submitFile(filename);

            JsonNode submitJson = objectMapper.readTree(submitResponse);
            String statusUrl = submitJson.path("status_url").asText();
            String jobId = submitJson.path("job_id").asText();

            if (statusUrl == null || statusUrl.isBlank() || jobId == null || jobId.isBlank()) {
                return "{\"status\":\"SHARPAPI_INVALID_SUBMIT_RESPONSE\",\"raw\":" + objectMapper.writeValueAsString(submitResponse) + "}";
            }

            String apiStatusUrl = "https://api.apyhub.com/sharpapi/api/v1/hr/parse_resume/job/status/" + jobId;
            return waitForCompletedResult(apiStatusUrl);

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"status\":\"SHARPAPI_CALL_FAILED\"}";
        }
    }

    private String submitFile(String filename) {
        File file = new File(filename);

        HttpHeaders headers = new HttpHeaders();
        headers.set("apy-token", apiKey);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(file));

        HttpEntity<MultiValueMap<String, Object>> requestEntity =
                new HttpEntity<>(body, headers);

        String url = "https://api.apyhub.com/sharpapi/api/v1/hr/parse_resume";

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        return response.getBody();
    }

    private String waitForCompletedResult(String statusUrl) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("apy-token", apiKey);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        for (int i = 0; i < 4; i++) {
            try {
                ResponseEntity<String> response = restTemplate.exchange(
                        statusUrl,
                        HttpMethod.GET,
                        entity,
                        String.class
                );

                String body = response.getBody();
                System.out.println("SharpAPI polling attempt " + (i + 1) + ": " + body);

                JsonNode json = objectMapper.readTree(body);
                JsonNode attributes = json.path("data").path("attributes");

                String status = attributes.path("status").asText();

                if ("completed".equalsIgnoreCase(status)) {
                    JsonNode result = attributes.path("result");

                    if (result.isMissingNode() || result.isNull()) {
                        return "{\"status\":\"SHARPAPI_COMPLETED_WITHOUT_RESULT\"}";
                    }

                    return objectMapper.writeValueAsString(result);
                }

                if ("failed".equalsIgnoreCase(status) || "error".equalsIgnoreCase(status)) {
                    return "{\"status\":\"SHARPAPI_JOB_FAILED\",\"raw\":" + objectMapper.writeValueAsString(body) + "}";
                }

                Thread.sleep(30000);

            } catch (Exception e) {
                e.printStackTrace();
                return "{\"status\":\"SHARPAPI_CALL_FAILED\"}";
            }
        }

        return "{\"status\":\"SHARPAPI_TIMEOUT\"}";
    }
}