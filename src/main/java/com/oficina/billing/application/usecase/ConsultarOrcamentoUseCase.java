package com.oficina.billing.application.usecase;

import com.oficina.billing.application.dto.OrcamentoResponse;
import com.oficina.billing.domain.exception.EntityNotFoundException;
import com.oficina.billing.domain.model.Orcamento;
import com.oficina.billing.domain.model.StatusOrcamento;
import com.oficina.billing.domain.repository.OrcamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsultarOrcamentoUseCase {

    private final OrcamentoRepository repository;

    @Transactional(readOnly = true)
    public OrcamentoResponse buscarPorId(Long id) {
        Orcamento orcamento = repository.buscarPorId(id)
                .orElseThrow(() -> new EntityNotFoundException("Orçamento não encontrado com ID: " + id));
        return OrcamentoResponse.fromDomain(orcamento);
    }

    @Transactional(readOnly = true)
    public OrcamentoResponse buscarPorOsId(Long osId) {
        Orcamento orcamento = repository.buscarPorOsId(osId)
                .orElseThrow(() -> new EntityNotFoundException("Orçamento não encontrado para OS ID: " + osId));
        return OrcamentoResponse.fromDomain(orcamento);
    }

    @Transactional(readOnly = true)
    public List<OrcamentoResponse> listar(StatusOrcamento status) {
        if (status != null) {
            return repository.buscarPorStatus(status).stream()
                    .map(OrcamentoResponse::fromDomain)
                    .toList();
        }
        return repository.listarTodos().stream()
                .map(OrcamentoResponse::fromDomain)
                .toList();
    }
}
