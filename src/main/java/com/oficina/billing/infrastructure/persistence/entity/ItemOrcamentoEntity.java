package com.oficina.billing.infrastructure.persistence.entity;

import com.oficina.billing.domain.model.ItemOrcamento;
import com.oficina.billing.domain.model.TipoItem;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "orcamento_itens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemOrcamentoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orcamento_id", nullable = false)
    private OrcamentoEntity orcamento;

    @Column(nullable = false)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoItem tipo;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(name = "valor_unitario", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorUnitario;

    public ItemOrcamento toDomain() {
        return ItemOrcamento.builder()
                .id(this.id)
                .descricao(this.descricao)
                .tipo(this.tipo)
                .quantidade(this.quantidade)
                .valorUnitario(this.valorUnitario)
                .build();
    }

    public static ItemOrcamentoEntity fromDomain(ItemOrcamento item, OrcamentoEntity orcamento) {
        return ItemOrcamentoEntity.builder()
                .id(item.getId())
                .orcamento(orcamento)
                .descricao(item.getDescricao())
                .tipo(item.getTipo())
                .quantidade(item.getQuantidade())
                .valorUnitario(item.getValorUnitario())
                .build();
    }
}
