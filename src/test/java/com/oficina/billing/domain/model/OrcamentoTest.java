package com.oficina.billing.domain.model;

import com.oficina.billing.domain.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class OrcamentoTest {

    private Orcamento orcamento;

    @BeforeEach
    void setUp() {
        orcamento = Orcamento.iniciar(1L, "OS-A1B2C3D4", "52998224725");
    }

    @Test
    @DisplayName("Deve iniciar orçamento no estado EM_ELABORACAO com total zero")
    void deveIniciarOrcamento() {
        assertNotNull(orcamento);
        assertEquals(StatusOrcamento.EM_ELABORACAO, orcamento.getStatus());
        assertEquals(1L, orcamento.getOsId());
        assertEquals(BigDecimal.ZERO, orcamento.getValorTotal());
        assertTrue(orcamento.getItens().isEmpty());
    }

    @Test
    @DisplayName("Deve adicionar itens e recalcular o valor total")
    void deveAdicionarItens() {
        ItemOrcamento peca = ItemOrcamento.builder()
                .descricao("Disco de Freio")
                .tipo(TipoItem.PECA)
                .quantidade(2)
                .valorUnitario(new BigDecimal("150.00"))
                .build();

        ItemOrcamento servico = ItemOrcamento.builder()
                .descricao("Instalação de Freios")
                .tipo(TipoItem.SERVICO)
                .quantidade(1)
                .valorUnitario(new BigDecimal("100.00"))
                .build();

        orcamento.adicionarItem(peca);
        orcamento.adicionarItem(servico);

        assertEquals(2, orcamento.getItens().size());
        assertEquals(new BigDecimal("400.00"), orcamento.getValorTotal());
    }

    @Test
    @DisplayName("Deve remover item do orçamento")
    void deveRemoverItem() {
        ItemOrcamento peca = ItemOrcamento.builder()
                .id(10L)
                .descricao("Óleo")
                .tipo(TipoItem.PECA)
                .quantidade(1)
                .valorUnitario(new BigDecimal("50.00"))
                .build();

        orcamento.adicionarItem(peca);
        assertEquals(1, orcamento.getItens().size());

        orcamento.removerItem(10L);
        assertEquals(0, orcamento.getItens().size());
        assertEquals(BigDecimal.ZERO, orcamento.getValorTotal());
    }

    @Test
    @DisplayName("Deve finalizar elaboração e transicionar para AGUARDANDO_PAGAMENTO")
    void deveFinalizarElaboracao() {
        ItemOrcamento peca = ItemOrcamento.builder()
                .descricao("Pneu")
                .tipo(TipoItem.PECA)
                .quantidade(2)
                .valorUnitario(new BigDecimal("350.00"))
                .build();

        orcamento.adicionarItem(peca);
        orcamento.finalizarElaboracao();

        assertEquals(StatusOrcamento.AGUARDANDO_PAGAMENTO, orcamento.getStatus());
        assertEquals(new BigDecimal("700.00"), orcamento.getValorTotal());
    }

    @Test
    @DisplayName("Não deve finalizar orçamento sem itens")
    void naoDeveFinalizarSemItens() {
        assertThrows(BusinessException.class, orcamento::finalizarElaboracao);
    }

    @Test
    @DisplayName("Deve vincular e confirmar pagamento com sucesso (Saga Pivot)")
    void deveConfirmarPagamento() {
        ItemOrcamento servico = ItemOrcamento.builder()
                .descricao("Alinhamento")
                .tipo(TipoItem.SERVICO)
                .quantidade(1)
                .valorUnitario(new BigDecimal("80.00"))
                .build();

        orcamento.adicionarItem(servico);
        orcamento.finalizarElaboracao();

        Pagamento pagamento = Pagamento.criar(
                orcamento.getValorTotal(),
                FormaPagamento.PIX_MERCADO_PAGO,
                "MP-999",
                "pix",
                "link"
        );

        orcamento.vincularPagamento(pagamento);
        orcamento.confirmarPagamento();

        assertEquals(StatusOrcamento.PAGO, orcamento.getStatus());
        assertEquals(StatusPagamento.APROVADO, orcamento.getPagamento().getStatus());
        assertNotNull(orcamento.getDataAprovacao());
    }

    @Test
    @DisplayName("Deve permitir recusa ou cancelamento do orçamento")
    void devePermitirCancelamento() {
        ItemOrcamento servico = ItemOrcamento.builder()
                .descricao("Alinhamento")
                .tipo(TipoItem.SERVICO)
                .quantidade(1)
                .valorUnitario(new BigDecimal("80.00"))
                .build();

        orcamento.adicionarItem(servico);
        orcamento.finalizarElaboracao();

        orcamento.recusar("Cliente achou caro");
        assertEquals(StatusOrcamento.RECUSADO, orcamento.getStatus());
        assertEquals("Cliente achou caro", orcamento.getMotivoCancelamento());
    }
}
