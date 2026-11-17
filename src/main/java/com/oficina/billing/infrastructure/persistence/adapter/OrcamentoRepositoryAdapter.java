package com.oficina.billing.infrastructure.persistence.adapter;

import com.oficina.billing.domain.model.Orcamento;
import com.oficina.billing.domain.model.StatusOrcamento;
import com.oficina.billing.domain.repository.OrcamentoRepository;
import com.oficina.billing.infrastructure.persistence.entity.OrcamentoEntity;
import com.oficina.billing.infrastructure.persistence.repository.SpringDataOrcamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OrcamentoRepositoryAdapter implements OrcamentoRepository {

    private final SpringDataOrcamentoRepository springRepository;

    @Override
    public Orcamento salvar(Orcamento orcamento) {
        OrcamentoEntity entity = OrcamentoEntity.fromDomain(orcamento);
        OrcamentoEntity salvo = springRepository.save(entity);
        return salvo.toDomain();
    }

    @Override
    public Optional<Orcamento> buscarPorId(Long id) {
        return springRepository.findById(id).map(OrcamentoEntity::toDomain);
    }

    @Override
    public Optional<Orcamento> buscarPorOsId(Long osId) {
        return springRepository.findByOsId(osId).map(OrcamentoEntity::toDomain);
    }

    @Override
    public Optional<Orcamento> buscarPorNumeroOs(String numeroOs) {
        return springRepository.findByNumeroOs(numeroOs).map(OrcamentoEntity::toDomain);
    }

    @Override
    public List<Orcamento> listarTodos() {
        return springRepository.findAll().stream()
                .map(OrcamentoEntity::toDomain)
                .toList();
    }

    @Override
    public List<Orcamento> buscarPorStatus(StatusOrcamento status) {
        return springRepository.findByStatus(status).stream()
                .map(OrcamentoEntity::toDomain)
                .toList();
    }
}
