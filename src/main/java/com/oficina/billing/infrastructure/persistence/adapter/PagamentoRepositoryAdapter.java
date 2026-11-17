package com.oficina.billing.infrastructure.persistence.adapter;

import com.oficina.billing.domain.model.Pagamento;
import com.oficina.billing.domain.repository.PagamentoRepository;
import com.oficina.billing.infrastructure.persistence.entity.PagamentoEntity;
import com.oficina.billing.infrastructure.persistence.repository.SpringDataPagamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PagamentoRepositoryAdapter implements PagamentoRepository {

    private final SpringDataPagamentoRepository springRepository;

    @Override
    public Pagamento salvar(Pagamento pagamento) {
        PagamentoEntity entity = PagamentoEntity.fromDomain(pagamento);
        PagamentoEntity salvo = springRepository.save(entity);
        return salvo.toDomain();
    }

    @Override
    public Optional<Pagamento> buscarPorId(Long id) {
        return springRepository.findById(id).map(PagamentoEntity::toDomain);
    }

    @Override
    public Optional<Pagamento> buscarPorIdTransacaoMercadoPago(String idTransacao) {
        return springRepository.findByIdTransacaoMercadoPago(idTransacao).map(PagamentoEntity::toDomain);
    }
}
