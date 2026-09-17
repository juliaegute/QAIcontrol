package br.edu.exemplo.ia.controller;

import br.edu.exemplo.ia.dto.*;
import br.edu.exemplo.ia.service.EmpresaUseCase;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
@RequestMapping("/api/empresa")
public class EmpresaController {
    private final EmpresaUseCase useCase;

    public EmpresaController(EmpresaUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    public EmpresaResponse create(@Valid @RequestBody EmpresaRequest r) {
        return useCase.create(r);
    }

    @GetMapping
    public List<EmpresaResponse> list() {
        return useCase.list();
    }
}
