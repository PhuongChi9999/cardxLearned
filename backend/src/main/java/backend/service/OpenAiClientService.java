package backend.service;

import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;

@Service
public class OpenAiClientService {

    private final String model;
    private final OpenAIClient restClient;

    public OpenAiClientService(
            @Value("${openai.api.key}") String apiKey,
            @Value("${openai.model}") String model
    ) {
        this.model = model;
        this.restClient = OpenAIOkHttpClient.builder().apiKey(apiKey).putHeader("Content-Type", "application/json").build();
    }

    //public String extractClientData(String filename) {
        //return "{\"clientName\":\"Mock User\",\"email\":null,\"phone\":null,\"amount\":1000,\"status\":\"MOCK_AI_FALLBACK\"}";
    //}


    public String extractClientData(String filename) {

        try {
            Tika tika = new Tika();
            String fileText = tika.parseToString(new File(filename));

            String prompt = """
                Analyse ce document et retourne uniquement un JSON valide avec:
                clientName, email, phone, status.

                DOCUMENT:
                """ + fileText;

            Response response = restClient.responses().create(
                    ResponseCreateParams.builder()
                            .model(this.model)
                            .input(prompt)
                            .build()
            );
            System.out.println(response.output().getFirst().toString());
            return response.output().getFirst().toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"status\":\"OPENAI_CALL_FAILED\"}";
        }
    }

}