package com.oficina.billing.domain.model;

import com.oficina.billing.domain.exception.BusinessException;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Pagamento {

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

    public static Pagamento criar(BigDecimal valor, FormaPagamento formaPagamento, String idTransacao, String qrCodePix, String linkPagamento) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Valor para pagamento deve ser maior que zero.");
        }
        if (formaPagamento == null) {
            throw new BusinessException("Forma de pagamento é obrigatória.");
        }

        return Pagamento.builder()
                .idTransacaoMercadoPago(idTransacao)
                .valor(valor)
                .status(StatusPagamento.PENDENTE)
                .formaPagamento(formaPagamento)
                .qrCodePix(qrCodePix)
                .linkPagamento(linkPagamento)
                .dataCriacao(LocalDateTime.now())
                .build();
    }

    public void aprovar() {
        if (this.status == StatusPagamento.APROVADO) {
            return;
        }
        if (this.status == StatusPagamento.CANCELADO) {
            throw new BusinessException("Não é possível aprovar pagamento previamente cancelado.");
        }
        this.status = StatusPagamento.APROVADO;
        this.dataConfirmacao = LocalDateTime.now();
    }

    public void recusar(String motivo) {
        if (this.status == StatusPagamento.APROVADO) {
            throw new BusinessException("Não é possível recusar pagamento já aprovado.");
        }
        this.status = StatusPagamento.RECUSADO;
        this.motivoRecusa = (motivo != null && !motivo.isBlank()) ? motivo : "Pagamento recusado pela operadora.";
    }

    public void cancelar(String motivo) {
        if (this.status == StatusPagamento.APROVADO) {
            throw new BusinessException("Não é possível cancelar pagamento já aprovado.");
        }
        this.status = StatusPagamento.CANCELADO;
        this.motivoRecusa = motivo;
    }
}
