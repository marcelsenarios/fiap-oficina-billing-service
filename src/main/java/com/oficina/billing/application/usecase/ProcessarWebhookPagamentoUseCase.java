package com.oficina.billing.application.usecase;

import com.oficina.billing.domain.event.BillingEventPublisherPort;
import com.oficina.billing.domain.event.PagamentoStatusEvent;
import com.oficina.billing.domain.gateway.MercadoPagoGatewayPort;
import com.oficina.billing.domain.model.Orcamento;
import com.oficina.billing.domain.repository.OrcamentoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessarWebhookPagamentoUseCase {

    private final OrcamentoRepository orcamentoRepository;
    private final MercadoPagoGatewayPort mercadoPagoGateway;
    private final BillingEventPublisherPort eventPublisher;

    @Transactional
    public void executar(String idTransacao) {
        log.info("Processando webhook do Mercado Pago para transação: {}", idTransacao);

        MercadoPagoGatewayPort.MercadoPagoStatusResult statusResult = mercadoPagoGateway.consultarStatus(idTransacao);
        log.info("Status retornado pelo Mercado Pago para transação {}: {}", idTransacao, statusResult.status());

        // Busca o orçamento que possui este pagamento
        Orcamento orcamento = orcamentoRepository.listarTodos().stream()
                .filter(o -> o.getPagamento() != null && idTransacao.equals(o.getPagamento().getIdTransacaoMercadoPago()))
                .findFirst()
                .orElse(null);

        if (orcamento == null) {
            log.warn("Nenhum orçamento encontrado com o ID de transação Mercado Pago: {}", idTransacao);
            return;
        }

        if ("approved".equalsIgnoreCase(statusResult.status())) {
            // SAGA PIVOT TRANSACTION: Pagamento aprovado!
            log.info("Saga Pivot: Confirmando pagamento para Orçamento #{} (OS: {})", orcamento.getId(), orcamento.getNumeroOs());
            orcamento.confirmarPagamento();
            orcamentoRepository.salvar(orcamento);

            PagamentoStatusEvent event = PagamentoStatusEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .osId(orcamento.getOsId())
                    .numeroOs(orcamento.getNumeroOs())
                    .orcamentoId(orcamento.getId())
                    .idTransacao(idTransacao)
                    .statusPagamento("APROVADO")
                    .valor(orcamento.getValorTotal())
                    .dataEvento(LocalDateTime.now())
                    .build();

            eventPublisher.publicarStatusPagamento(event);

        } else if ("rejected".equalsIgnoreCase(statusResult.status()) || "cancelled".equalsIgnoreCase(statusResult.status())) {
            // SAGA COMPENSATING TRANSACTION: Pagamento recusado -> Rollback!
            log.warn("Saga Compensação: Pagamento recusado para Orçamento #{} (OS: {})", orcamento.getId(), orcamento.getNumeroOs());
            orcamento.recusar("Recusado pelo Mercado Pago: " + statusResult.statusDetail());
            orcamentoRepository.salvar(orcamento);

            PagamentoStatusEvent event = PagamentoStatusEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .osId(orcamento.getOsId())
                    .numeroOs(orcamento.getNumeroOs())
                    .orcamentoId(orcamento.getId())
                    .idTransacao(idTransacao)
                    .statusPagamento("RECUSADO")
                    .valor(orcamento.getValorTotal())
                    .motivo(statusResult.statusDetail())
                    .dataEvento(LocalDateTime.now())
                    .build();

            eventPublisher.publicarStatusPagamento(event);
        }
    }
}
