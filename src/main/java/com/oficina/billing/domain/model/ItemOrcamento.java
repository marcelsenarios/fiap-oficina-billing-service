package com.oficina.billing.domain.model;

import com.oficina.billing.domain.exception.BusinessException;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class ItemOrcamento {

    private Long id;
    private String descricao;
    private TipoItem tipo;
    private Integer quantidade;
    private BigDecimal valorUnitario;

    public BigDecimal getSubtotal() {
        if (quantidade == null || valorUnitario == null) {
            return BigDecimal.ZERO;
        }
        return valorUnitario.multiply(BigDecimal.valueOf(quantidade));
    }

    public void validar() {
        if (descricao == null || descricao.trim().isEmpty()) {
            throw new BusinessException("Descrição do item de orçamento é obrigatória.");
        }
        if (tipo == null) {
            throw new BusinessException("Tipo do item (PECA ou SERVICO) é obrigatório.");
        }
        if (quantidade == null || quantidade <= 0) {
            throw new BusinessException("Quantidade do item deve ser maior que zero.");
        }
        if (valorUnitario == null || valorUnitario.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Valor unitário do item deve ser maior que zero.");
        }
    }
}
