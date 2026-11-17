package com.oficina.billing.application.usecase;

import com.oficina.billing.application.dto.OrcamentoResponse;
import com.oficina.billing.domain.event.BillingEventPublisherPort;
import com.oficina.billing.domain.event.PagamentoStatusEvent;
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
public class CancelarOrcamentoUseCase {

    private final OrcamentoRepository repository;
    private final BillingEventPublisherPort eventPublisher;

    @Transactional
    public OrcamentoResponse executar(Long orcamentoId, String motivo) {
        Orcamento orcamento = repository.buscarPorId(orcamentoId)
                .orElseThrow(() -> new EntityNotFoundException("Orçamento não encontrado com ID: " + orcamentoId));

        orcamento.cancelar(motivo);
        Orcamento salvo = repository.salvar(orcamento);

        // Notifica compensação para a mensageria
        PagamentoStatusEvent evento = PagamentoStatusEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .osId(salvo.getOsId())
                .numeroOs(salvo.getNumeroOs())
                .orcamentoId(salvo.getId())
                .statusPagamento("CANCELADO")
                .valor(salvo.getValorTotal())
                .motivo(motivo)
                .dataEvento(LocalDateTime.now())
                .build();

        eventPublisher.publicarStatusPagamento(evento);

        return OrcamentoResponse.fromDomain(salvo);
    }
}
