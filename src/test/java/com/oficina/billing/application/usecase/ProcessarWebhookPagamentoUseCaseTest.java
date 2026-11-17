package com.oficina.billing.application.usecase;

import com.oficina.billing.domain.event.BillingEventPublisherPort;
import com.oficina.billing.domain.event.PagamentoStatusEvent;
import com.oficina.billing.domain.gateway.MercadoPagoGatewayPort;
import com.oficina.billing.domain.model.*;
import com.oficina.billing.domain.repository.OrcamentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessarWebhookPagamentoUseCaseTest {

    @Mock
    private OrcamentoRepository orcamentoRepository;

    @Mock
    private MercadoPagoGatewayPort mercadoPagoGateway;

    @Mock
    private BillingEventPublisherPort eventPublisher;

    @InjectMocks
    private ProcessarWebhookPagamentoUseCase useCase;

    private Orcamento orcamento;

    @BeforeEach
    void setUp() {
        orcamento = Orcamento.iniciar(1L, "OS-123", "52998224725");
        orcamento.setId(10L);
        orcamento.adicionarItem(ItemOrcamento.builder()
                .descricao("Serviço")
                .tipo(TipoItem.SERVICO)
                .quantidade(1)
                .valorUnitario(new BigDecimal("100.00"))
                .build());
        orcamento.finalizarElaboracao();

        Pagamento pagamento = Pagamento.criar(
                orcamento.getValorTotal(),
                FormaPagamento.PIX_MERCADO_PAGO,
                "MP-TRANSACTION-123",
                "pix",
                "link"
        );
        orcamento.vincularPagamento(pagamento);
    }

    @Test
    @DisplayName("Saga Pivot: Deve processar webhook com pagamento aprovado")
    void deveProcessarPagamentoAprovado() {
        when(orcamentoRepository.listarTodos()).thenReturn(List.of(orcamento));
        when(mercadoPagoGateway.consultarStatus("MP-TRANSACTION-123"))
                .thenReturn(new MercadoPagoGatewayPort.MercadoPagoStatusResult("MP-TRANSACTION-123", "approved", "accredited"));

        useCase.executar("MP-TRANSACTION-123");

        verify(orcamentoRepository, times(1)).salvar(any(Orcamento.class));
        verify(eventPublisher, times(1)).publicarStatusPagamento(argThat(event ->
                "APROVADO".equals(event.getStatusPagamento()) && 1L.equals(event.getOsId())));
    }

    @Test
    @DisplayName("Saga Rollback: Deve processar webhook com pagamento rejeitado")
    void deveProcessarPagamentoRejeitado() {
        when(orcamentoRepository.listarTodos()).thenReturn(List.of(orcamento));
        when(mercadoPagoGateway.consultarStatus("MP-TRANSACTION-123"))
                .thenReturn(new MercadoPagoGatewayPort.MercadoPagoStatusResult("MP-TRANSACTION-123", "rejected", "insufficient_amount"));

        useCase.executar("MP-TRANSACTION-123");

        verify(orcamentoRepository, times(1)).salvar(any(Orcamento.class));
        verify(eventPublisher, times(1)).publicarStatusPagamento(argThat(event ->
                "RECUSADO".equals(event.getStatusPagamento()) && 1L.equals(event.getOsId())));
    }
}
