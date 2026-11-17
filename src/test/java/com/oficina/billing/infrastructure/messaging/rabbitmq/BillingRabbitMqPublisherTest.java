package com.oficina.billing.infrastructure.messaging.rabbitmq;

import com.oficina.billing.domain.event.OrcamentoGeradoEvent;
import com.oficina.billing.domain.event.PagamentoStatusEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Messaging: BillingRabbitMqPublisher")
class BillingRabbitMqPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private BillingRabbitMqPublisher publisher;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(publisher, "billingExchangeName", "billing.events.exchange");
    }

    @Test
    @DisplayName("Deve publicar evento de orçamento gerado")
    void devePublicarOrcamentoGerado() {
        OrcamentoGeradoEvent event = OrcamentoGeradoEvent.builder()
                .osId(1L)
                .numeroOs("OS-1")
                .valorTotal(BigDecimal.TEN)
                .build();

        publisher.publicarOrcamentoGerado(event);
        verify(rabbitTemplate).convertAndSend(eq("billing.events.exchange"), eq("billing.orcamento.gerado"), eq(event));
    }

    @Test
    @DisplayName("Deve publicar evento de status de pagamento")
    void devePublicarStatusPagamento() {
        PagamentoStatusEvent event = PagamentoStatusEvent.builder()
                .osId(1L)
                .numeroOs("OS-1")
                .statusPagamento("APROVADO")
                .build();

        publisher.publicarStatusPagamento(event);
        verify(rabbitTemplate).convertAndSend(eq("billing.events.exchange"), eq("billing.pagamento.status"), eq(event));
    }
}
