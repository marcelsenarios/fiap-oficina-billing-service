package com.oficina.billing.domain.repository;

import com.oficina.billing.domain.model.Pagamento;

import java.util.Optional;

public interface PagamentoRepository {

    Pagamento salvar(Pagamento pagamento);

    Optional<Pagamento> buscarPorId(Long id);

    Optional<Pagamento> buscarPorIdTransacaoMercadoPago(String idTransacao);
}
