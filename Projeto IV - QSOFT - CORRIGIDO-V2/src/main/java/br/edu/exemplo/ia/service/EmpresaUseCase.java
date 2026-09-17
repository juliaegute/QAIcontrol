package br.edu.exemplo.ia.service;

import br.edu.exemplo.ia.dto.*;

import java.util.*;

public interface EmpresaUseCase {
    EmpresaResponse create(EmpresaRequest r);

    List<EmpresaResponse> list();

    boolean exists(UUID id);
}