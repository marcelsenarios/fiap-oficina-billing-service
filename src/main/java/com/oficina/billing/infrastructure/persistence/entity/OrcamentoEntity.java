package com.oficina.billing.infrastructure.persistence.entity;

import com.oficina.billing.domain.model.Orcamento;
import com.oficina.billing.domain.model.StatusOrcamento;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orcamentos", indexes = {
        @Index(name = "idx_orcamento_os_id", columnList = "os_id"),
        @Index(name = "idx_orcamento_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrcamentoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "os_id", nullable = false)
    private Long osId;

    @Column(name = "numero_os", nullable = false, length = 32)
    private String numeroOs;

    @Column(name = "cliente_cpf_cnpj", length = 14)
    private String clienteCpfCnpj;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusOrcamento status;

    @Column(name = "valor_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorTotal;

    @OneToMany(mappedBy = "orcamento", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private List<ItemOrcamentoEntity> itens = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "pagamento_id")
    private PagamentoEntity pagamento;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_aprovacao")
    private LocalDateTime dataAprovacao;

    @Column(name = "data_cancelamento")
    private LocalDateTime dataCancelamento;

    @Column(name = "motivo_cancelamento")
    private String motivoCancelamento;

    public Orcamento toDomain() {
        Orcamento domain = Orcamento.builder()
                .id(this.id)
                .osId(this.osId)
                .numeroOs(this.numeroOs)
                .clienteCpfCnpj(this.clienteCpfCnpj)
                .status(this.status)
                .valorTotal(this.valorTotal)
                .dataCriacao(this.dataCriacao)
                .dataAprovacao(this.dataAprovacao)
                .dataCancelamento(this.dataCancelamento)
                .motivoCancelamento(this.motivoCancelamento)
                .itens(new ArrayList<>())
                .build();

        if (this.itens != null) {
            domain.setItens(new ArrayList<>(this.itens.stream().map(ItemOrcamentoEntity::toDomain).toList()));
        }

        if (this.pagamento != null) {
            domain.setPagamento(this.pagamento.toDomain());
        }

        return domain;
    }

    public static OrcamentoEntity fromDomain(Orcamento domain) {
        OrcamentoEntity entity = OrcamentoEntity.builder()
                .id(domain.getId())
                .osId(domain.getOsId())
                .numeroOs(domain.getNumeroOs())
                .clienteCpfCnpj(domain.getClienteCpfCnpj())
                .status(domain.getStatus())
                .valorTotal(domain.getValorTotal())
                .dataCriacao(domain.getDataCriacao())
                .dataAprovacao(domain.getDataAprovacao())
                .dataCancelamento(domain.getDataCancelamento())
                .motivoCancelamento(domain.getMotivoCancelamento())
                .itens(new ArrayList<>())
                .pagamento(PagamentoEntity.fromDomain(domain.getPagamento()))
                .build();

        if (domain.getItens() != null) {
            for (var item : domain.getItens()) {
                entity.getItens().add(ItemOrcamentoEntity.fromDomain(item, entity));
            }
        }

        return entity;
    }
}
