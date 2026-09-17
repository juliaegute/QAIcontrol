package br.edu.exemplo.ia.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "ai_usage")
public class AIUsage {
    @Id
    private UUID id;

    private UUID empresaId;
    private long tokens;
    private String model;
    private Instant occurredAt;

    protected AIUsage() {
    }

    public AIUsage(UUID empresaId, long tokens, String model) {
        if (empresaId == null) throw new IllegalArgumentException("Empresa obrigatória");
        if (tokens <= 0) throw new IllegalArgumentException("Tokens devem ser maiores que zero");
        if (model == null || model.isBlank()) throw new IllegalArgumentException("Modelo obrigatório");

        this.id = UUID.randomUUID();
        this.empresaId = empresaId;
        this.tokens = tokens;
        this.model = model;
        this.occurredAt = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getEmpresaId() { return empresaId; }
    public long getTokens() { return tokens; }
    public String getModel() { return model; }
    public Instant getOccurredAt() { return occurredAt; }
}
