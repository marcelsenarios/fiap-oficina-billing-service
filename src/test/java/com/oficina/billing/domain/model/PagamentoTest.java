package com.oficina.billing.domain.model;

import com.oficina.billing.domain.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PagamentoTest {

    @Test
    @DisplayName("Deve criar pagamento com status PENDENTE")
    void deveCriarPagamento() {
        Pagamento pagamento = Pagamento.criar(
                new BigDecimal("350.00"),
                FormaPagamento.PIX_MERCADO_PAGO,
                "MP-12345",
                "pix-code-fake",
                "https://mp.com/checkout"
        );

        assertNotNull(pagamento);
        assertEquals(StatusPagamento.PENDENTE, pagamento.getStatus());
        assertEquals("MP-12345", pagamento.getIdTransacaoMercadoPago());
        assertNotNull(pagamento.getDataCriacao());
    }

    @Test
    @DisplayName("Deve aprovar pagamento com sucesso")
    void deveAprovarPagamento() {
        Pagamento pagamento = Pagamento.criar(
                new BigDecimal("100.00"),
                FormaPagamento.PIX_MERCADO_PAGO,
                "MP-123",
                "pix",
                "link"
        );

        pagamento.aprovar();
        assertEquals(StatusPagamento.APROVADO, pagamento.getStatus());
        assertNotNull(pagamento.getDataConfirmacao());
    }

    @Test
    @DisplayName("Deve recusar pagamento com motivo")
    void deveRecusarPagamento() {
        Pagamento pagamento = Pagamento.criar(
                new BigDecimal("100.00"),
                FormaPagamento.PIX_MERCADO_PAGO,
                "MP-123",
                "pix",
                "link"
        );

        pagamento.recusar("Saldo insuficiente");
        assertEquals(StatusPagamento.RECUSADO, pagamento.getStatus());
        assertEquals("Saldo insuficiente", pagamento.getMotivoRecusa());
    }

    @Test
    @DisplayName("Não deve permitir aprovação de pagamento cancelado")
    void naoDeveAprovarCancelado() {
        Pagamento pagamento = Pagamento.criar(
                new BigDecimal("100.00"),
                FormaPagamento.PIX_MERCADO_PAGO,
                "MP-123",
                "pix",
                "link"
        );
        pagamento.cancelar("Cancelado");

        assertThrows(BusinessException.class, pagamento::aprovar);
    }
}
