package br.edu.exemplo.ia.service;

import br.edu.exemplo.ia.domain.entity.Empresa;
import br.edu.exemplo.ia.domain.vo.EmpresaName;
import br.edu.exemplo.ia.dto.*;
import br.edu.exemplo.ia.repository.EmpresaRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EmpresaService implements EmpresaUseCase {
    private final EmpresaRepository repo;

    public EmpresaService(EmpresaRepository repo) {
        this.repo = repo;
    }

    public EmpresaResponse create(EmpresaRequest r) {
        return toDto(repo.save(new Empresa(new EmpresaName(r.name()), r.area())));
    }

    public List<EmpresaResponse> list() {
        return repo.findAll().stream().map(this::toDto).toList();
    }

    public boolean exists(UUID id) {
        return repo.existsById(id);
    }

    private EmpresaResponse toDto(Empresa p) {
        return new EmpresaResponse(p.getId(), p.getName().value(), p.getArea());
    }
}
