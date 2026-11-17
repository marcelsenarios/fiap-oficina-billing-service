package com.oficina.billing.application.usecase;

import com.oficina.billing.application.dto.AdicionarItemRequest;
import com.oficina.billing.application.dto.OrcamentoResponse;
import com.oficina.billing.domain.model.Orcamento;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdicionarItemOrcamentoUseCaseTest {

    @Mock
    private OrcamentoRepository repository;

    @InjectMocks
    private AdicionarItemOrcamentoUseCase useCase;

    @Test
    @DisplayName("Deve adicionar item ao orçamento")
    void deveAdicionarItem() {
        Orcamento orcamento = Orcamento.iniciar(1L, "OS-123", "52998224725");
        orcamento.setId(5L);

        when(repository.buscarPorId(5L)).thenReturn(Optional.of(orcamento));
        when(repository.salvar(any())).thenAnswer(invocation -> invocation.getArgument(0));

        AdicionarItemRequest request = AdicionarItemRequest.builder()
                .descricao("Vela de Ignição")
                .tipo(TipoItem.PECA)
                .quantidade(4)
                .valorUnitario(new BigDecimal("25.00"))
                .build();

        OrcamentoResponse response = useCase.executar(5L, request);

        assertNotNull(response);
        assertEquals(1, response.getItens().size());
        assertEquals(new BigDecimal("100.00"), response.getValorTotal());
    }
}
