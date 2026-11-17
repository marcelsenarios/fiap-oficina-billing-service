package com.oficina.billing.infrastructure.persistence.adapter;

import com.oficina.billing.domain.model.Orcamento;
import com.oficina.billing.domain.model.StatusOrcamento;
import com.oficina.billing.infrastructure.persistence.entity.OrcamentoEntity;
import com.oficina.billing.infrastructure.persistence.repository.SpringDataOrcamentoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrcamentoRepositoryAdapterTest {

    @Mock
    private SpringDataOrcamentoRepository springRepository;

    @InjectMocks
    private OrcamentoRepositoryAdapter adapter;

    @Test
    @DisplayName("Deve salvar e converter para domínio")
    void deveSalvar() {
        Orcamento domain = Orcamento.iniciar(1L, "OS-123", "52998224725");
        OrcamentoEntity entity = OrcamentoEntity.fromDomain(domain);
        entity.setId(10L);

        when(springRepository.save(any(OrcamentoEntity.class))).thenReturn(entity);

        Orcamento salvo = adapter.salvar(domain);

        assertNotNull(salvo);
        assertEquals(10L, salvo.getId());
        assertEquals(1L, salvo.getOsId());
    }

    @Test
    @DisplayName("Deve buscar por ID, OS ID, número OS, status e listar todos")
    void deveBuscar() {
        OrcamentoEntity entity = OrcamentoEntity.builder()
                .id(10L)
                .osId(1L)
                .numeroOs("OS-123")
                .clienteCpfCnpj("52998224725")
                .status(StatusOrcamento.EM_ELABORACAO)
                .valorTotal(BigDecimal.ZERO)
                .dataCriacao(LocalDateTime.now())
                .build();

        when(springRepository.findById(10L)).thenReturn(Optional.of(entity));
        when(springRepository.findByOsId(1L)).thenReturn(Optional.of(entity));
        when(springRepository.findByNumeroOs("OS-123")).thenReturn(Optional.of(entity));
        when(springRepository.findAll()).thenReturn(List.of(entity));
        when(springRepository.findByStatus(StatusOrcamento.EM_ELABORACAO)).thenReturn(List.of(entity));

        assertTrue(adapter.buscarPorId(10L).isPresent());
        assertTrue(adapter.buscarPorOsId(1L).isPresent());
        assertTrue(adapter.buscarPorNumeroOs("OS-123").isPresent());
        assertEquals(1, adapter.listarTodos().size());
        assertEquals(1, adapter.buscarPorStatus(StatusOrcamento.EM_ELABORACAO).size());
    }
}
