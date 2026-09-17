package br.edu.exemplo.ia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record UsageRequest(
        @NotNull UUID empresaId,
        @Positive long tokens,
        @NotBlank String model
) {
}
