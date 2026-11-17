package com.oficina.billing.domain.repository;

import com.oficina.billing.domain.model.Orcamento;
import com.oficina.billing.domain.model.StatusOrcamento;

import java.util.List;
import java.util.Optional;

public interface OrcamentoRepository {

    Orcamento salvar(Orcamento orcamento);

    Optional<Orcamento> buscarPorId(Long id);

    Optional<Orcamento> buscarPorOsId(Long osId);

    Optional<Orcamento> buscarPorNumeroOs(String numeroOs);

    List<Orcamento> listarTodos();

    List<Orcamento> buscarPorStatus(StatusOrcamento status);
}
