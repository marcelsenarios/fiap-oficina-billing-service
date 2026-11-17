package com.oficina.billing.infrastructure.messaging.rabbitmq;

import com.oficina.billing.domain.event.BillingEventPublisherPort;
import com.oficina.billing.domain.event.OrcamentoGeradoEvent;
import com.oficina.billing.domain.event.PagamentoStatusEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BillingRabbitMqPublisher implements BillingEventPublisherPort {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.billing:billing.events.exchange}")
    private String billingExchangeName;

    @Override
    public void publicarOrcamentoGerado(OrcamentoGeradoEvent event) {
        String routingKey = "billing.orcamento.gerado";
        log.info("Billing Service: Publicando evento de orçamento gerado para OS {}: valor R$ {} na routingKey: {}",
                event.getNumeroOs(), event.getValorTotal(), routingKey);
        rabbitTemplate.convertAndSend(billingExchangeName, routingKey, event);
    }

    @Override
    public void publicarStatusPagamento(PagamentoStatusEvent event) {
        String routingKey = "billing.pagamento.status";
        log.info("Billing Service [Saga]: Publicando status de pagamento {} para OS {} na routingKey: {}",
                event.getStatusPagamento(), event.getNumeroOs(), routingKey);
        rabbitTemplate.convertAndSend(billingExchangeName, routingKey, event);
    }
}
