package br.edu.exemplo.ia.integration.alert;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
public class NodeAlertClient implements AlertClient {

    private final RestClient restClient;

    public NodeAlertClient(@Value("${services.alert.base-url}") String alertServiceBaseUrl) {
        this.restClient = RestClient.builder().baseUrl(alertServiceBaseUrl).build();
    }

    @Override
    public AlertResponse evaluate(UUID empresaId, long tokens, String model) {
        AlertRequest request = new AlertRequest(empresaId, tokens, model);

        AlertResponse response = restClient.post()
                .uri("/api/alerts/evaluate")
                .body(request)
                .retrieve()
                .body(AlertResponse.class);

        return response != null
                ? response
                : new AlertResponse("UNKNOWN", "Serviço de alertas não retornou conteúdo");
    }

    private record AlertRequest(UUID empresaId, long tokens, String model) {
    }
}
