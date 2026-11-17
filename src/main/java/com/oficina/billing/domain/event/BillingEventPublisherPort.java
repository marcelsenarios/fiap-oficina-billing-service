package com.oficina.billing.domain.event;

public interface BillingEventPublisherPort {

    void publicarOrcamentoGerado(OrcamentoGeradoEvent event);

    void publicarStatusPagamento(PagamentoStatusEvent event);
}
