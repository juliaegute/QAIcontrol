package br.edu.exemplo.ia.domain.entity;

import br.edu.exemplo.ia.domain.vo.EmpresaName;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "empresa")
public class Empresa {
    @Id
    private UUID id;

    @Embedded
    private EmpresaName name;

    private String area;

    protected Empresa() {
    }

    public Empresa(EmpresaName name, String area) {
        if (area == null || area.isBlank()) {
            throw new IllegalArgumentException("Área obrigatória");
        }
        this.id = UUID.randomUUID();
        this.name = name;
        this.area = area;
    }

    public UUID getId() {
        return id;
    }

    public EmpresaName getName() {
        return name;
    }

    public String getArea() {
        return area;
    }
}
