package com.oficina.billing.infrastructure.persistence.repository;

import com.oficina.billing.domain.model.StatusOrcamento;
import com.oficina.billing.infrastructure.persistence.entity.OrcamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataOrcamentoRepository extends JpaRepository<OrcamentoEntity, Long> {

    Optional<OrcamentoEntity> findByOsId(Long osId);

    Optional<OrcamentoEntity> findByNumeroOs(String numeroOs);

    List<OrcamentoEntity> findByStatus(StatusOrcamento status);
}
