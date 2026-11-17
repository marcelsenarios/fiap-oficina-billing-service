package com.oficina.billing.application.dto;

import com.oficina.billing.domain.model.FormaPagamento;
import com.oficina.billing.domain.model.Pagamento;
import com.oficina.billing.domain.model.StatusPagamento;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Detalhes da transação de pagamento com Mercado Pago")
public class PagamentoResponse {

    private Long id;
    private String idTransacaoMercadoPago;
    private BigDecimal valor;
    private StatusPagamento status;
    private FormaPagamento formaPagamento;
    private String qrCodePix;
    private String linkPagamento;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataConfirmacao;
    private String motivoRecusa;

    public static PagamentoResponse fromDomain(Pagamento pagamento) {
        if (pagamento == null) return null;
        return PagamentoResponse.builder()
                .id(pagamento.getId())
                .idTransacaoMercadoPago(pagamento.getIdTransacaoMercadoPago())
                .valor(pagamento.getValor())
                .status(pagamento.getStatus())
                .formaPagamento(pagamento.getFormaPagamento())
                .qrCodePix(pagamento.getQrCodePix())
                .linkPagamento(pagamento.getLinkPagamento())
                .dataCriacao(pagamento.getDataCriacao())
                .dataConfirmacao(pagamento.getDataConfirmacao())
                .motivoRecusa(pagamento.getMotivoRecusa())
                .build();
    }
}
