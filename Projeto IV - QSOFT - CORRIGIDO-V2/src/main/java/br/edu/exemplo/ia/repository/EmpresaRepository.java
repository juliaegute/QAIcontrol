package br.edu.exemplo.ia.repository;

import br.edu.exemplo.ia.domain.entity.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EmpresaRepository extends JpaRepository<Empresa, UUID> {
}
