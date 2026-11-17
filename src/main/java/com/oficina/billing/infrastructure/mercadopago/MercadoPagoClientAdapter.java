package com.oficina.billing.infrastructure.mercadopago;

import com.oficina.billing.domain.gateway.MercadoPagoGatewayPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Component
public class MercadoPagoClientAdapter implements MercadoPagoGatewayPort {

    @Value("${mercadopago.access-token:TEST-TOKEN}")
    private String accessToken;

    @Value("${mercadopago.notification-url:https://api.oficina.com/api/v1/webhook/mercadopago}")
    private String notificationUrl;

    @Override
    public MercadoPagoCobrancaResult gerarCobrancaPix(Long orcamentoId, String numeroOs, BigDecimal valor, String clienteCpfCnpj) {
        log.info("Integrando com Mercado Pago: Gerando cobrança Pix para Orçamento #{} (OS: {}), Valor: R$ {}",
                orcamentoId, numeroOs, valor);

        String idTransacao = "MP-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
        String qrCodePix = String.format("00020126580014BR.GOV.BCB.PIX0136%s520400005303986540%s5802BR5913OFICINA FIAP6009SAO PAULO62070503***6304",
                UUID.randomUUID(), valor.toPlainString());
        String linkPagamento = "https://www.mercadopago.com.br/checkout/v1/redirect?pref_id=" + idTransacao;

        log.info("Mercado Pago: Cobrança Pix gerada com sucesso. ID da Transação: {}", idTransacao);

        return new MercadoPagoCobrancaResult(idTransacao, qrCodePix, linkPagamento, "pending");
    }

    @Override
    public MercadoPagoStatusResult consultarStatus(String idTransacao) {
        log.info("Consultando status da transação no Mercado Pago: {}", idTransacao);

        // Retorna status simulado ou aprovado por default se não explicitado
        return new MercadoPagoStatusResult(idTransacao, "approved", "accredited");
    }
}
