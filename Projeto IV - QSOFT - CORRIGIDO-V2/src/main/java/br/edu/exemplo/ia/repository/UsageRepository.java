package br.edu.exemplo.ia.repository;

import br.edu.exemplo.ia.domain.entity.AIUsage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UsageRepository extends JpaRepository<AIUsage, UUID> {
}
