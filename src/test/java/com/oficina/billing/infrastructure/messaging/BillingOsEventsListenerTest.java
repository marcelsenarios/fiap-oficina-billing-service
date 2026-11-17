package com.oficina.billing.infrastructure.messaging;

import com.oficina.billing.application.dto.CriarOrcamentoRequest;
import com.oficina.billing.application.usecase.CancelarOrcamentoUseCase;
import com.oficina.billing.application.usecase.CriarOrcamentoUseCase;
import com.oficina.billing.domain.model.Orcamento;
import com.oficina.billing.domain.repository.OrcamentoRepository;
import com.oficina.billing.infrastructure.messaging.dto.OsCanceladaIntegrationMsg;
import com.oficina.billing.infrastructure.messaging.dto.OsCriadaIntegrationMsg;
import com.oficina.billing.infrastructure.messaging.rabbitmq.BillingOsEventsListener;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillingOsEventsListenerTest {

    @Mock
    private CriarOrcamentoUseCase criarOrcamentoUseCase;

    @Mock
    private CancelarOrcamentoUseCase cancelarOrcamentoUseCase;

    @Mock
    private OrcamentoRepository orcamentoRepository;

    @InjectMocks
    private BillingOsEventsListener listener;

    @Test
    @DisplayName("Saga Step: Ao receber evento de OS criada, deve inicializar orçamento")
    void deveInicializarOrcamento() {
        OsCriadaIntegrationMsg msg = OsCriadaIntegrationMsg.builder()
                .osId(1L)
                .numeroOs("OS-1234")
                .clienteCpfCnpj("52998224725")
                .build();

        when(orcamentoRepository.buscarPorOsId(1L)).thenReturn(Optional.empty());

        listener.processarEventoOs(msg);

        verify(criarOrcamentoUseCase, times(1)).executar(any(CriarOrcamentoRequest.class));
    }

    @Test
    @DisplayName("Saga Rollback: Ao receber evento de OS cancelada, deve cancelar orçamento")
    void deveCancelarOrcamento() {
        OsCanceladaIntegrationMsg msg = OsCanceladaIntegrationMsg.builder()
                .osId(1L)
                .numeroOs("OS-1234")
                .motivo("Cancelada pelo cliente")
                .build();

        Orcamento orcamento = Orcamento.iniciar(1L, "OS-1234", "52998224725");
        orcamento.setId(10L);

        when(orcamentoRepository.buscarPorOsId(1L)).thenReturn(Optional.of(orcamento));

        listener.processarEventoOs(msg);

        verify(cancelarOrcamentoUseCase, times(1)).executar(eq(10L), eq("Cancelada pelo cliente"));
    }
}
