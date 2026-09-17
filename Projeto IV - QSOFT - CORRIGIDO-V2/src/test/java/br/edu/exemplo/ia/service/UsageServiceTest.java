package br.edu.exemplo.ia.service;

import br.edu.exemplo.ia.domain.entity.AIUsage;
import br.edu.exemplo.ia.dto.UsageRequest;
import br.edu.exemplo.ia.dto.UsageResponse;
import br.edu.exemplo.ia.integration.alert.AlertClient;
import br.edu.exemplo.ia.integration.alert.AlertResponse;
import br.edu.exemplo.ia.repository.UsageRepository;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UsageServiceTest {

    @Test
    void deveRegistrarConsumoDaEmpresaEConsultarAlerta_AAA() {
        UsageRepository repo = mock(UsageRepository.class);
        EmpresaUseCase empresas = mock(EmpresaUseCase.class);
        AlertClient alerts = mock(AlertClient.class);

        UUID empresaId = UUID.randomUUID();
        when(empresas.exists(empresaId)).thenReturn(true);
        when(repo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(alerts.evaluate(empresaId, 500, "gpt-demo"))
                .thenReturn(new AlertResponse("INFO", "Consumo dentro do esperado"));

        UsageService service = new UsageService(repo, empresas, alerts);
        UsageResponse result = service.register(new UsageRequest(empresaId, 500, "gpt-demo"));

        assertEquals(empresaId, result.empresaId());
        assertEquals(500, result.tokens());
        assertEquals("INFO", result.alertLevel());
        verify(repo).save(any(AIUsage.class));
        verify(alerts).evaluate(empresaId, 500, "gpt-demo");
    }
}
