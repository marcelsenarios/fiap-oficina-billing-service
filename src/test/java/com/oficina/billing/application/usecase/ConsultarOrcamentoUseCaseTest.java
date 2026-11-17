package com.oficina.billing.application.usecase;

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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarOrcamentoUseCaseTest {

    @Mock
    private OrcamentoRepository repository;

    @InjectMocks
    private ConsultarOrcamentoUseCase useCase;

    @Test
    @DisplayName("Deve consultar orçamento por ID, por OS ID e listar todos")
    void deveConsultar() {
        Orcamento orcamento = Orcamento.iniciar(1L, "OS-123", "52998224725");
        orcamento.setId(10L);

        when(repository.buscarPorId(10L)).thenReturn(Optional.of(orcamento));
        when(repository.buscarPorOsId(1L)).thenReturn(Optional.of(orcamento));
        when(repository.listarTodos()).thenReturn(List.of(orcamento));
        when(repository.buscarPorStatus(StatusOrcamento.EM_ELABORACAO)).thenReturn(List.of(orcamento));

        OrcamentoResponse porId = useCase.buscarPorId(10L);
        assertNotNull(porId);

        OrcamentoResponse porOs = useCase.buscarPorOsId(1L);
        assertNotNull(porOs);

        assertEquals(1, useCase.listar(null).size());
        assertEquals(1, useCase.listar(StatusOrcamento.EM_ELABORACAO).size());
    }
}
