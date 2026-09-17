package br.edu.exemplo.ia.controller;

import br.edu.exemplo.ia.dto.UsageRequest;
import br.edu.exemplo.ia.dto.UsageResponse;
import br.edu.exemplo.ia.dto.UsageSummaryResponse;
import br.edu.exemplo.ia.service.UsageUseCase;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/usages")
public class UsageController {
    private final UsageUseCase useCase;

    public UsageController(UsageUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    public UsageResponse register(@Valid @RequestBody UsageRequest request) {
        return useCase.register(request);
    }

    @GetMapping
    public List<UsageSummaryResponse> list() {
        return useCase.list();
    }
}
