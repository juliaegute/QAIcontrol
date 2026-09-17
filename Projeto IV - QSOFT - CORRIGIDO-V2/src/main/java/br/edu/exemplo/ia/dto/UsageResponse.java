package br.edu.exemplo.ia.dto;

import java.time.Instant;
import java.util.UUID;

public record UsageResponse(
        UUID id,
        UUID empresaId,
        long tokens,
        String model,
        Instant occurredAt,
        String alertLevel,
        String alertMessage
) {
}
