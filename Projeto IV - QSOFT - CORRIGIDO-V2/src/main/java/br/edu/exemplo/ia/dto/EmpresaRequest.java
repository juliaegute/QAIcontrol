package br.edu.exemplo.ia.dto;

import jakarta.validation.constraints.NotBlank;

public record EmpresaRequest(@NotBlank String name, @NotBlank String area) {
}