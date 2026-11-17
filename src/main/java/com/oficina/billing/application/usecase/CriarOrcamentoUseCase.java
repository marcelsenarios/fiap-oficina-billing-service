package com.oficina.billing.application.usecase;

import com.oficina.billing.application.dto.CriarOrcamentoRequest;
import com.oficina.billing.application.dto.OrcamentoResponse;
import com.oficina.billing.domain.model.Orcamento;
import com.oficina.billing.domain.repository.OrcamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CriarOrcamentoUseCase {

    private final OrcamentoRepository repository;

    @Transactional
    public OrcamentoResponse executar(CriarOrcamentoRequest request) {
        Orcamento orcamento = Orcamento.iniciar(request.getOsId(), request.getNumeroOs(), request.getClienteCpfCnpj());
        Orcamento salvo = repository.salvar(orcamento);
        return OrcamentoResponse.fromDomain(salvo);
    }
}
