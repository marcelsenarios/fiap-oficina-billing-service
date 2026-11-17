package com.oficina.billing.application.usecase;

import com.oficina.billing.application.dto.OrcamentoResponse;
import com.oficina.billing.domain.event.BillingEventPublisherPort;
import com.oficina.billing.domain.event.OrcamentoGeradoEvent;
import com.oficina.billing.domain.model.ItemOrcamento;
import com.oficina.billing.domain.model.Orcamento;
import com.oficina.billing.domain.model.StatusOrcamento;
import com.oficina.billing.domain.model.TipoItem;
import com.oficina.billing.domain.repository.OrcamentoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinalizarOrcamentoUseCaseTest {

    @Mock
    private OrcamentoRepository repository;

    @Mock
    private BillingEventPublisherPort eventPublisher;

    @InjectMocks
    private FinalizarOrcamentoUseCase useCase;

    @Test
    @DisplayName("Deve finalizar orçamento e publicar evento para o Saga (OS Service)")
    void deveFinalizarOrcamento() {
        Orcamento orcamento = Orcamento.iniciar(1L, "OS-123", "52998224725");
        orcamento.setId(10L);
        orcamento.adicionarItem(ItemOrcamento.builder()
                .descricao("Serviço de Alinhamento")
                .tipo(TipoItem.SERVICO)
                .quantidade(1)
                .valorUnitario(new BigDecimal("150.00"))
                .build());

        when(repository.buscarPorId(10L)).thenReturn(Optional.of(orcamento));
        when(repository.salvar(any())).thenAnswer(invocation -> invocation.getArgument(0));

        OrcamentoResponse response = useCase.executar(10L);

        assertEquals(StatusOrcamento.AGUARDANDO_PAGAMENTO, response.getStatus());
        assertEquals(new BigDecimal("150.00"), response.getValorTotal());

        verify(eventPublisher, times(1)).publicarOrcamentoGerado(any(OrcamentoGeradoEvent.class));
    }
}
