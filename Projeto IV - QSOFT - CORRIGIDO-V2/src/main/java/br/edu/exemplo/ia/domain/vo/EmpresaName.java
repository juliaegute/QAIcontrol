package br.edu.exemplo.ia.domain.vo;

import jakarta.persistence.Embeddable;

@Embeddable
public record EmpresaName(String value) {
    public EmpresaName {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("Nome obrigatório");
    }
}
