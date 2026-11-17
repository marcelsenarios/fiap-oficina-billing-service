package com.oficina.billing.application.dto;

import com.oficina.billing.domain.model.ItemOrcamento;
import com.oficina.billing.domain.model.TipoItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Detalhes de um item do orçamento")
public class ItemOrcamentoResponse {

    private Long id;
    private String descricao;
    private TipoItem tipo;
    private Integer quantidade;
    private BigDecimal valorUnitario;
    private BigDecimal subtotal;

    public static ItemOrcamentoResponse fromDomain(ItemOrcamento item) {
        if (item == null) return null;
        return ItemOrcamentoResponse.builder()
                .id(item.getId())
                .descricao(item.getDescricao())
                .tipo(item.getTipo())
                .quantidade(item.getQuantidade())
                .valorUnitario(item.getValorUnitario())
                .subtotal(item.getSubtotal())
                .build();
    }
}
