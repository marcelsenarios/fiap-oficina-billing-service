package com.oficina.billing.application.usecase;

import com.oficina.billing.application.dto.IniciarPagamentoRequest;
import com.oficina.billing.application.dto.OrcamentoResponse;
import com.oficina.billing.domain.exception.EntityNotFoundException;
import com.oficina.billing.domain.gateway.MercadoPagoGatewayPort;
import com.oficina.billing.domain.model.Orcamento;
import com.oficina.billing.domain.model.Pagamento;
import com.oficina.billing.domain.repository.OrcamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IniciarPagamentoUseCase {

    private final OrcamentoRepository repository;
    private final MercadoPagoGatewayPort mercadoPagoGateway;

    @Transactional
    public OrcamentoResponse executar(Long orcamentoId, IniciarPagamentoRequest request) {
        Orcamento orcamento = repository.buscarPorId(orcamentoId)
                .orElseThrow(() -> new EntityNotFoundException("Orçamento não encontrado com ID: " + orcamentoId));

        // Integração com Mercado Pago (geração de QR Code Pix e link de pagamento)
        MercadoPagoGatewayPort.MercadoPagoCobrancaResult cobranca = mercadoPagoGateway.gerarCobrancaPix(
                orcamento.getId(),
                orcamento.getNumeroOs(),
                orcamento.getValorTotal(),
                orcamento.getClienteCpfCnpj()
        );

        Pagamento pagamento = Pagamento.criar(
                orcamento.getValorTotal(),
                request.getFormaPagamento(),
                cobranca.idTransacao(),
                cobranca.qrCodePix(),
                cobranca.linkPagamento()
        );

        orcamento.vincularPagamento(pagamento);
        Orcamento salvo = repository.salvar(orcamento);

        return OrcamentoResponse.fromDomain(salvo);
    }
}
