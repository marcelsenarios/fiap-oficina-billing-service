package com.oficina.billing.application.usecase;

import com.oficina.billing.application.dto.OrcamentoResponse;
import com.oficina.billing.domain.event.BillingEventPublisherPort;
import com.oficina.billing.domain.event.PagamentoStatusEvent;
import com.oficina.billing.domain.model.Orcamento;
import com.oficina.billing.domain.model.StatusOrcamento;
import com.oficina.billing.domain.repository.OrcamentoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CancelarOrcamentoUseCaseTest {

    @Mock
    private OrcamentoRepository repository;

    @Mock
    private BillingEventPublisherPort eventPublisher;

    @InjectMocks
    private CancelarOrcamentoUseCase useCase;

    @Test
    @DisplayName("Deve cancelar orçamento e publicar evento de compensação no RabbitMQ")
    void deveCancelarOrcamento() {
        Orcamento orcamento = Orcamento.iniciar(1L, "OS-123", "52998224725");
        orcamento.setId(10L);

        when(repository.buscarPorId(10L)).thenReturn(Optional.of(orcamento));
        when(repository.salvar(any())).thenAnswer(invocation -> invocation.getArgument(0));

        OrcamentoResponse response = useCase.executar(10L, "Desistência do cliente");

        assertEquals(StatusOrcamento.CANCELADO, response.getStatus());
        assertEquals("Desistência do cliente", response.getMotivoCancelamento());

        verify(eventPublisher, times(1)).publicarStatusPagamento(any(PagamentoStatusEvent.class));
    }
}
