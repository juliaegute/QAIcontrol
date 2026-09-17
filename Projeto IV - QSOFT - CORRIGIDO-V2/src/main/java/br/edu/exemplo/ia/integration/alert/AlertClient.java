package br.edu.exemplo.ia.integration.alert;

import java.util.UUID;

public interface AlertClient {
    AlertResponse evaluate(UUID empresaId, long tokens, String model);
}
