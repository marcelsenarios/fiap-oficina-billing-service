package com.oficina.billing.infrastructure.messaging.rabbitmq;

import com.oficina.billing.application.dto.CriarOrcamentoRequest;
import com.oficina.billing.application.usecase.CancelarOrcamentoUseCase;
import com.oficina.billing.application.usecase.CriarOrcamentoUseCase;
import com.oficina.billing.domain.model.Orcamento;
import com.oficina.billing.domain.repository.OrcamentoRepository;
import com.oficina.billing.infrastructure.messaging.dto.OsCanceladaIntegrationMsg;
import com.oficina.billing.infrastructure.messaging.dto.OsCriadaIntegrationMsg;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class BillingOsEventsListener {

    private final CriarOrcamentoUseCase criarOrcamentoUseCase;
    private final CancelarOrcamentoUseCase cancelarOrcamentoUseCase;
    private final OrcamentoRepository orcamentoRepository;

    @RabbitListener(queues = "${rabbitmq.queue.billing-os-events:billing.os.events.queue}")
    public void processarEventoOs(Object payload) {
        log.info("Billing Service: Evento recebido da fila de OS: {}", payload);

        if (payload instanceof OsCriadaIntegrationMsg osCriada) {
            log.info("Billing Service [Saga]: Inicializando orçamento para nova OS #{} ({})",
                    osCriada.getOsId(), osCriada.getNumeroOs());

            // Garante idempotência verificando se já existe orçamento para esta OS
            if (orcamentoRepository.buscarPorOsId(osCriada.getOsId()).isEmpty()) {
                criarOrcamentoUseCase.executar(CriarOrcamentoRequest.builder()
                        .osId(osCriada.getOsId())
                        .numeroOs(osCriada.getNumeroOs())
                        .clienteCpfCnpj(osCriada.getClienteCpfCnpj())
                        .build());
            }
        } else if (payload instanceof OsCanceladaIntegrationMsg osCancelada) {
            log.warn("Billing Service [Saga Compensação]: OS #{} cancelada. Cancelando orçamento.",
                    osCancelada.getOsId());

            Optional<Orcamento> orcamentoOpt = orcamentoRepository.buscarPorOsId(osCancelada.getOsId());
            orcamentoOpt.ifPresent(orcamento ->
                    cancelarOrcamentoUseCase.executar(orcamento.getId(), osCancelada.getMotivo()));
        }
    }
}
