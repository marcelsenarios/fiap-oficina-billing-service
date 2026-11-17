package com.oficina.billing.application.usecase;

import com.oficina.billing.application.dto.AdicionarItemRequest;
import com.oficina.billing.application.dto.OrcamentoResponse;
import com.oficina.billing.domain.exception.EntityNotFoundException;
import com.oficina.billing.domain.model.ItemOrcamento;
import com.oficina.billing.domain.model.Orcamento;
import com.oficina.billing.domain.repository.OrcamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdicionarItemOrcamentoUseCase {

    private final OrcamentoRepository repository;

    @Transactional
    public OrcamentoResponse executar(Long orcamentoId, AdicionarItemRequest request) {
        Orcamento orcamento = repository.buscarPorId(orcamentoId)
                .orElseThrow(() -> new EntityNotFoundException("Orçamento não encontrado com ID: " + orcamentoId));

        ItemOrcamento item = ItemOrcamento.builder()
                .descricao(request.getDescricao())
                .tipo(request.getTipo())
                .quantidade(request.getQuantidade())
                .valorUnitario(request.getValorUnitario())
                .build();

        orcamento.adicionarItem(item);
        Orcamento salvo = repository.salvar(orcamento);
        return OrcamentoResponse.fromDomain(salvo);
    }
}
