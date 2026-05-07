package backend.service;

import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;

@Service
public class LocalAiClientService {

    private final String model;
    private final OpenAIClient restClient;

    public LocalAiClientService(
            @Value("${localai.base.url}") String baseUrl,
            @Value("${localai.api.key}") String apiKey,
            @Value("${localai.model}") String model
    ) {
        this.model = model;
        this.restClient = OpenAIOkHttpClient.builder()
        .apiKey(apiKey)
        .putHeader("Content-Type", "application/json")
        .baseUrl(baseUrl)
        .build();
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
                clientName, status.
                Ne mets pas ```json.

                DOCUMENT:
                """ + fileText;

            ChatCompletionCreateParams params =
                                            ChatCompletionCreateParams.builder()
                                                .model(this.model)
                                                .addUserMessage(prompt)
                                                .build();

            ChatCompletion completion = restClient.chat().completions().create(params);

            String text = completion.choices()
                .get(0)
                .message()
                .content()
                .orElse("");

            System.out.println(text);
            return text;

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"status\":\"LOCALAI_CALL_FAILED\"}";
        }
    }

}