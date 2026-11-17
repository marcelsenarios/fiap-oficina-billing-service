package com.oficina.billing.application.usecase;

import com.oficina.billing.application.dto.OrcamentoResponse;
import com.oficina.billing.domain.event.BillingEventPublisherPort;
import com.oficina.billing.domain.event.OrcamentoGeradoEvent;
import com.oficina.billing.domain.exception.EntityNotFoundException;
import com.oficina.billing.domain.model.Orcamento;
import com.oficina.billing.domain.repository.OrcamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FinalizarOrcamentoUseCase {

    private final OrcamentoRepository repository;
    private final BillingEventPublisherPort eventPublisher;

    @Transactional
    public OrcamentoResponse executar(Long orcamentoId) {
        Orcamento orcamento = repository.buscarPorId(orcamentoId)
                .orElseThrow(() -> new EntityNotFoundException("Orçamento não encontrado com ID: " + orcamentoId));

        orcamento.finalizarElaboracao();
        Orcamento salvo = repository.salvar(orcamento);

        // Dispara evento para o RabbitMQ (Saga Pattern -> OS Service atualiza status da OS para AGUARDANDO_APROVACAO)
        OrcamentoGeradoEvent evento = OrcamentoGeradoEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .osId(salvo.getOsId())
                .numeroOs(salvo.getNumeroOs())
                .orcamentoId(salvo.getId())
                .valorTotal(salvo.getValorTotal())
                .dataCriacao(LocalDateTime.now())
                .build();

        eventPublisher.publicarOrcamentoGerado(evento);

        return OrcamentoResponse.fromDomain(salvo);
    }
}
