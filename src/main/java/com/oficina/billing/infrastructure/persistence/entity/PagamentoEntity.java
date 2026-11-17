package com.oficina.billing.infrastructure.persistence.entity;

import com.oficina.billing.domain.model.FormaPagamento;
import com.oficina.billing.domain.model.Pagamento;
import com.oficina.billing.domain.model.StatusPagamento;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagamentos", indexes = {
        @Index(name = "idx_pagamento_mp_id", columnList = "id_transacao_mercado_pago")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagamentoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_transacao_mercado_pago", unique = true, length = 64)
    private String idTransacaoMercadoPago;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusPagamento status;

    @Enumerated(EnumType.STRING)
    @Column(name = "forma_pagamento", nullable = false, length = 40)
    private FormaPagamento formaPagamento;

    @Column(name = "qr_code_pix", columnDefinition = "TEXT")
    private String qrCodePix;

    @Column(name = "link_pagamento", columnDefinition = "TEXT")
    private String linkPagamento;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_confirmacao")
    private LocalDateTime dataConfirmacao;

    @Column(name = "motivo_recusa")
    private String motivoRecusa;

    public Pagamento toDomain() {
        return Pagamento.builder()
                .id(this.id)
                .idTransacaoMercadoPago(this.idTransacaoMercadoPago)
                .valor(this.valor)
                .status(this.status)
                .formaPagamento(this.formaPagamento)
                .qrCodePix(this.qrCodePix)
                .linkPagamento(this.linkPagamento)
                .dataCriacao(this.dataCriacao)
                .dataConfirmacao(this.dataConfirmacao)
                .motivoRecusa(this.motivoRecusa)
                .build();
    }

    public static PagamentoEntity fromDomain(Pagamento pagamento) {
        if (pagamento == null) return null;
        return PagamentoEntity.builder()
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
