package br.edu.exemplo.ia.service;

import br.edu.exemplo.ia.dto.UsageRequest;
import br.edu.exemplo.ia.dto.UsageResponse;
import br.edu.exemplo.ia.dto.UsageSummaryResponse;

import java.util.List;

public interface UsageUseCase {
    UsageResponse register(UsageRequest request);
    List<UsageSummaryResponse> list();
}
