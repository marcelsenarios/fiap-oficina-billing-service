package com.oficina.billing.infrastructure.persistence.repository;

import com.oficina.billing.infrastructure.persistence.entity.PagamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpringDataPagamentoRepository extends JpaRepository<PagamentoEntity, Long> {

    Optional<PagamentoEntity> findByIdTransacaoMercadoPago(String idTransacaoMercadoPago);
}
