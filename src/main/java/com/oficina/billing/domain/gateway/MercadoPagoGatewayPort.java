package com.oficina.billing.domain.gateway;

import java.math.BigDecimal;

public interface MercadoPagoGatewayPort {

    MercadoPagoCobrancaResult gerarCobrancaPix(Long orcamentoId, String numeroOs, BigDecimal valor, String clienteCpfCnpj);

    MercadoPagoStatusResult consultarStatus(String idTransacao);

    record MercadoPagoCobrancaResult(
            String idTransacao,
            String qrCodePix,
            String linkPagamento,
            String status
    ) {}

    record MercadoPagoStatusResult(
            String idTransacao,
            String status, // approved, rejected, pending, cancelled
            String statusDetail
    ) {}
}
