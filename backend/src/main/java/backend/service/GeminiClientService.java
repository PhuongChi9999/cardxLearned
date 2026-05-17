package backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class GeminiClientService {

    private final RestClient restClient;
    private final String apiKey;

    public GeminiClientService(
            @Value("${gemini.api.key}") String apiKey
    ) {

        this.apiKey = apiKey;

        this.restClient = RestClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public String extract(String filePath, String mimeType) {

        try {

            byte[] fileBytes = Files.readAllBytes(Path.of(filePath));

            String base64 = Base64.getEncoder()
                    .encodeToString(fileBytes);

            Map<String, Object> body = Map.of(
                    "contents", List.of(
                            Map.of(
                                    "parts", List.of(

                                            Map.of(
                                                    "text",
                                                    """
                                                    You are an insurance document extraction AI.
                                            
                                                    Analyze the provided document and extract structured information.
                                            
                                                    Return ONLY valid JSON.
                                            
                                                    Expected JSON structure:
                                                    {
                                                      "documentType": "",
                                                      "clientName": "",
                                                      "email": "",
                                                      "phone": "",
                                                      "claimType": "",
                                                      "contractNumber": "",
                                                      "incidentDate": "",
                                                      "address": ""
                                                    }
                                            
                                                    Rules:
                                                    - Always include all keys from the expected JSON structure.
                                                    - documentType must be one of:
                                                      SINISTRE_HABITATION,
                                                      ASSURANCE_VIE,
                                                      ACTE_DE_DECES,
                                                      CONTRAT_ASSURANCE,
                                                      RIB,
                                                      IDENTITE,
                                                      JUSTIFICATIF_DOMICILE,
                                                      FACTURE,
                                                      DEVIS,
                                                      PHOTO_DOMMAGE,
                                                      UNKNOWN
                                                    - claimType examples:
                                                      Dégât des eaux, Incendie, Vol, Bris de glace, Santé, Auto
                                                    - Use ASSURANCE_VIE only for a request letter asking for life insurance payout.
                                                    - If the document is an official death certificate, use ACTE_DE_DECES even if it is attached to an assurance vie file.
                                                    - If the document is a life insurance contract, use CONTRAT_ASSURANCE even if it mentions assurance vie or contrat AV.
                                                    - If the document is a bank identity statement, use RIB.
                                                    - If the document is an identity card or passport, use IDENTITE.
                                                    - If the document is an electricity, water, gas, internet or housing proof document, use JUSTIFICATIF_DOMICILE.
                                                    - If the document is a request for life insurance payout after death, use claimType "Versement capital assurance vie".
                                                    - If information is missing, return empty string.
                                                    - Do not confuse supporting documents with the main insurance request.
                                                    - Return ONLY JSON. No markdown. No explanation.
                                                    """
                                            ),

                                            Map.of(
                                                    "inline_data",
                                                    Map.of(
                                                            "mime_type", mimeType,
                                                            "data", base64
                                                    )
                                            )
                                    )
                            )
                    )
            );

            Map response = callGeminiWithRetry(body);

            System.out.println("GEMINI RESPONSE = " + response);

            List candidates = (List) response.get("candidates");

            if (candidates == null || candidates.isEmpty()) {
                return "{\"status\":\"EMPTY_GEMINI_RESPONSE\"}";
            }

            Map candidate = (Map) candidates.get(0);

            Map content = (Map) candidate.get("content");

            if (content == null) {
                return "{\"status\":\"EMPTY_GEMINI_CONTENT\"}";
            }

            List parts = (List) content.get("parts");

            if (parts == null || parts.isEmpty()) {
                return "{\"status\":\"EMPTY_GEMINI_PARTS\"}";
            }

            Map firstPart = (Map) parts.get(0);

            String text = (String) firstPart.get("text");

            if (text == null || text.isBlank()) {
                return "{\"status\":\"EMPTY_GEMINI_TEXT\"}";
            }

            return text
                    .replace("```json", "")
                    .replace("```", "")
                    .trim();

        } catch (Exception e) {

            System.out.println("==== GEMINI ERROR ====");
            e.printStackTrace();
            System.out.println("======================");

            return "{\"status\":\"GEMINI_CALL_FAILED\"}";
        }
    }

    private Map callGeminiWithRetry(Map<String, Object> body) {
        int maxAttempts = 3;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return restClient.post()
                        .uri(uriBuilder -> uriBuilder
                                .path("/v1beta/models/gemini-2.5-flash-lite:generateContent")
                                .queryParam("key", apiKey)
                                .build())
                        .body(body)
                        .retrieve()
                        .body(Map.class);

            } catch (Exception e) {
                System.out.println("Gemini attempt " + attempt + " failed");

                if (attempt == maxAttempts) {
                    throw e;
                }

                try {
                    Thread.sleep(1000L * attempt);
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Gemini retry interrupted", interruptedException);
                }
            }
        }

        throw new RuntimeException("Gemini retry failed");
    }
}