package br.edu.exemplo.ia.service;

import br.edu.exemplo.ia.domain.entity.AIUsage;
import br.edu.exemplo.ia.dto.UsageRequest;
import br.edu.exemplo.ia.dto.UsageResponse;
import br.edu.exemplo.ia.dto.UsageSummaryResponse;
import br.edu.exemplo.ia.integration.alert.AlertClient;
import br.edu.exemplo.ia.integration.alert.AlertResponse;
import br.edu.exemplo.ia.repository.UsageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsageService implements UsageUseCase {

    private final UsageRepository usageRepo;
    private final EmpresaUseCase empresas;
    private final AlertClient alerts;

    public UsageService(UsageRepository usageRepo, EmpresaUseCase empresas, AlertClient alerts) {
        this.usageRepo = usageRepo;
        this.empresas = empresas;
        this.alerts = alerts;
    }

    @Override
    public UsageResponse register(UsageRequest request) {
        if (!empresas.exists(request.empresaId())) {
            throw new IllegalArgumentException("Empresa inexistente: " + request.empresaId());
        }

        AIUsage usage = usageRepo.save(
                new AIUsage(request.empresaId(), request.tokens(), request.model())
        );

        AlertResponse alert = alerts.evaluate(
                usage.getEmpresaId(), usage.getTokens(), usage.getModel()
        );

        return new UsageResponse(
                usage.getId(), usage.getEmpresaId(), usage.getTokens(), usage.getModel(),
                usage.getOccurredAt(), alert.level(), alert.message()
        );
    }

    @Override
    public List<UsageSummaryResponse> list() {
        return usageRepo.findAll().stream()
                .map(u -> new UsageSummaryResponse(
                        u.getId(), u.getEmpresaId(), u.getTokens(), u.getModel(), u.getOccurredAt()))
                .toList();
    }
}
