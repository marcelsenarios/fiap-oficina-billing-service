package com.oficina.billing.application.usecase;

import com.oficina.billing.application.dto.CriarOrcamentoRequest;
import com.oficina.billing.application.dto.OrcamentoResponse;
import com.oficina.billing.domain.model.Orcamento;
import com.oficina.billing.domain.model.StatusOrcamento;
import com.oficina.billing.domain.repository.OrcamentoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CriarOrcamentoUseCaseTest {

    @Mock
    private OrcamentoRepository repository;

    @InjectMocks
    private CriarOrcamentoUseCase useCase;

    @Test
    @DisplayName("Deve criar novo orçamento com sucesso")
    void deveCriarOrcamento() {
        CriarOrcamentoRequest request = CriarOrcamentoRequest.builder()
                .osId(1L)
                .numeroOs("OS-1234")
                .clienteCpfCnpj("52998224725")
                .build();

        when(repository.salvar(any(Orcamento.class))).thenAnswer(invocation -> {
            Orcamento o = invocation.getArgument(0);
            o.setId(10L);
            return o;
        });

        OrcamentoResponse response = useCase.executar(request);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals(1L, response.getOsId());
        assertEquals("OS-1234", response.getNumeroOs());
        assertEquals(StatusOrcamento.EM_ELABORACAO, response.getStatus());

        verify(repository, times(1)).salvar(any(Orcamento.class));
    }
}
