package com.oficina.billing.domain.model;

import com.oficina.billing.domain.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ItemOrcamentoTest {

    @Test
    @DisplayName("Deve calcular subtotal corretamente")
    void deveCalcularSubtotal() {
        ItemOrcamento item = ItemOrcamento.builder()
                .descricao("Óleo Sintético 5W30")
                .tipo(TipoItem.PECA)
                .quantidade(4)
                .valorUnitario(new BigDecimal("45.00"))
                .build();

        assertEquals(new BigDecimal("180.00"), item.getSubtotal());
    }

    @Test
    @DisplayName("Deve validar item com sucesso")
    void deveValidarComSucesso() {
        ItemOrcamento item = ItemOrcamento.builder()
                .descricao("Troca de Pastilhas")
                .tipo(TipoItem.SERVICO)
                .quantidade(1)
                .valorUnitario(new BigDecimal("120.00"))
                .build();

        assertDoesNotThrow(item::validar);
    }

    @Test
    @DisplayName("Deve lançar exceção se descrição for vazia")
    void deveFalharSemDescricao() {
        ItemOrcamento item = ItemOrcamento.builder()
                .descricao("")
                .tipo(TipoItem.PECA)
                .quantidade(1)
                .valorUnitario(new BigDecimal("10.00"))
                .build();

        assertThrows(BusinessException.class, item::validar);
    }

    @Test
    @DisplayName("Deve lançar exceção se quantidade for zero ou negativa")
    void deveFalharQuantidadeInvalida() {
        ItemOrcamento item = ItemOrcamento.builder()
                .descricao("Filtro")
                .tipo(TipoItem.PECA)
                .quantidade(0)
                .valorUnitario(new BigDecimal("10.00"))
                .build();

        assertThrows(BusinessException.class, item::validar);
    }

    @Test
    @DisplayName("Deve lançar exceção se valor unitário for menor ou igual a zero")
    void deveFalharValorUnitarioInvalido() {
        ItemOrcamento item = ItemOrcamento.builder()
                .descricao("Filtro")
                .tipo(TipoItem.PECA)
                .quantidade(1)
                .valorUnitario(BigDecimal.ZERO)
                .build();

        assertThrows(BusinessException.class, item::validar);
    }
}
