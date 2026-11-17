package com.oficina.billing.domain.model;

import com.oficina.billing.domain.exception.BusinessException;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Orcamento {

    private Long id;
    private Long osId;
    private String numeroOs;
    private String clienteCpfCnpj;
    private StatusOrcamento status;
    @Builder.Default
    private List<ItemOrcamento> itens = new ArrayList<>();
    private BigDecimal valorTotal;
    private Pagamento pagamento;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAprovacao;
    private LocalDateTime dataCancelamento;
    private String motivoCancelamento;

    public static Orcamento iniciar(Long osId, String numeroOs, String clienteCpfCnpj) {
        if (osId == null) {
            throw new BusinessException("ID da Ordem de Serviço é obrigatório.");
        }
        if (numeroOs == null || numeroOs.isBlank()) {
            throw new BusinessException("Número da Ordem de Serviço é obrigatório.");
        }

        return Orcamento.builder()
                .osId(osId)
                .numeroOs(numeroOs)
                .clienteCpfCnpj(clienteCpfCnpj)
                .status(StatusOrcamento.EM_ELABORACAO)
                .itens(new ArrayList<>())
                .valorTotal(BigDecimal.ZERO)
                .dataCriacao(LocalDateTime.now())
                .build();
    }

    public void adicionarItem(ItemOrcamento item) {
        if (this.status != StatusOrcamento.EM_ELABORACAO) {
            throw new BusinessException("Não é possível alterar itens de um orçamento que não esteja em elaboração.");
        }
        if (item == null) {
            throw new BusinessException("Item de orçamento não pode ser nulo.");
        }
        item.validar();
        this.itens.add(item);
        this.calcularValorTotal();
    }

    public void removerItem(Long itemId) {
        if (this.status != StatusOrcamento.EM_ELABORACAO) {
            throw new BusinessException("Não é possível alterar itens de um orçamento que não esteja em elaboração.");
        }
        boolean removido = this.itens.removeIf(item -> item.getId() != null && item.getId().equals(itemId));
        if (!removido) {
            throw new BusinessException("Item com ID " + itemId + " não encontrado no orçamento.");
        }
        this.calcularValorTotal();
    }

    public void calcularValorTotal() {
        this.valorTotal = this.itens.stream()
                .map(ItemOrcamento::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void finalizarElaboracao() {
        if (this.status != StatusOrcamento.EM_ELABORACAO) {
            throw new BusinessException("Orçamento já foi finalizado ou processado.");
        }
        if (this.itens.isEmpty()) {
            throw new BusinessException("Orçamento deve conter pelo menos um item (peça ou serviço).");
        }
        this.calcularValorTotal();
        if (this.valorTotal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Valor total do orçamento deve ser maior que zero.");
        }
        this.status = StatusOrcamento.AGUARDANDO_PAGAMENTO;
    }

    public void vincularPagamento(Pagamento pagamento) {
        if (this.status != StatusOrcamento.AGUARDANDO_PAGAMENTO) {
            throw new BusinessException("Pagamento só pode ser vinculado a orçamentos aguardando pagamento.");
        }
        this.pagamento = pagamento;
    }

    public void confirmarPagamento() {
        if (this.status != StatusOrcamento.AGUARDANDO_PAGAMENTO) {
            throw new BusinessException("Não é possível confirmar pagamento para um orçamento fora do estado AGUARDANDO_PAGAMENTO.");
        }
        if (this.pagamento != null) {
            this.pagamento.aprovar();
        }
        this.status = StatusOrcamento.PAGO;
        this.dataAprovacao = LocalDateTime.now();
    }

    public void recusar(String motivo) {
        if (this.status == StatusOrcamento.PAGO) {
            throw new BusinessException("Não é possível recusar um orçamento já pago.");
        }
        this.status = StatusOrcamento.RECUSADO;
        this.motivoCancelamento = motivo;
        this.dataCancelamento = LocalDateTime.now();
        if (this.pagamento != null) {
            this.pagamento.recusar(motivo);
        }
    }

    public void cancelar(String motivo) {
        if (this.status == StatusOrcamento.PAGO) {
            throw new BusinessException("Não é possível cancelar um orçamento já pago.");
        }
        this.status = StatusOrcamento.CANCELADO;
        this.motivoCancelamento = motivo;
        this.dataCancelamento = LocalDateTime.now();
        if (this.pagamento != null) {
            this.pagamento.cancelar(motivo);
        }
    }
}
