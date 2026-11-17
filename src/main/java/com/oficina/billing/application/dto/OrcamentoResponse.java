package com.oficina.billing.application.dto;

import com.oficina.billing.domain.model.Orcamento;
import com.oficina.billing.domain.model.StatusOrcamento;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Dados consolidados do Orçamento")
public class OrcamentoResponse {

    private Long id;
    private Long osId;
    private String numeroOs;
    private String clienteCpfCnpj;
    private StatusOrcamento status;
    private BigDecimal valorTotal;
    private List<ItemOrcamentoResponse> itens;
    private PagamentoResponse pagamento;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAprovacao;
    private LocalDateTime dataCancelamento;
    private String motivoCancelamento;

    public static OrcamentoResponse fromDomain(Orcamento orcamento) {
        if (orcamento == null) return null;
        return OrcamentoResponse.builder()
                .id(orcamento.getId())
                .osId(orcamento.getOsId())
                .numeroOs(orcamento.getNumeroOs())
                .clienteCpfCnpj(orcamento.getClienteCpfCnpj())
                .status(orcamento.getStatus())
                .valorTotal(orcamento.getValorTotal())
                .itens(orcamento.getItens() != null
                        ? orcamento.getItens().stream().map(ItemOrcamentoResponse::fromDomain).toList()
                        : List.of())
                .pagamento(PagamentoResponse.fromDomain(orcamento.getPagamento()))
                .dataCriacao(orcamento.getDataCriacao())
                .dataAprovacao(orcamento.getDataAprovacao())
                .dataCancelamento(orcamento.getDataCancelamento())
                .motivoCancelamento(orcamento.getMotivoCancelamento())
                .build();
    }
}
