package com.oficina.billing.application.usecase;

import com.oficina.billing.application.dto.IniciarPagamentoRequest;
import com.oficina.billing.application.dto.OrcamentoResponse;
import com.oficina.billing.domain.gateway.MercadoPagoGatewayPort;
import com.oficina.billing.domain.model.FormaPagamento;
import com.oficina.billing.domain.model.ItemOrcamento;
import com.oficina.billing.domain.model.Orcamento;
import com.oficina.billing.domain.model.TipoItem;
import com.oficina.billing.domain.repository.OrcamentoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IniciarPagamentoUseCaseTest {

    @Mock
    private OrcamentoRepository repository;

    @Mock
    private MercadoPagoGatewayPort mercadoPagoGateway;

    @InjectMocks
    private IniciarPagamentoUseCase useCase;

    @Test
    @DisplayName("Deve gerar cobrança Pix via Mercado Pago e vincular ao orçamento")
    void deveIniciarPagamento() {
        Orcamento orcamento = Orcamento.iniciar(1L, "OS-123", "52998224725");
        orcamento.setId(10L);
        orcamento.adicionarItem(ItemOrcamento.builder()
                .descricao("Troca de Óleo")
                .tipo(TipoItem.SERVICO)
                .quantidade(1)
                .valorUnitario(new BigDecimal("200.00"))
                .build());
        orcamento.finalizarElaboracao();

        when(repository.buscarPorId(10L)).thenReturn(Optional.of(orcamento));
        when(mercadoPagoGateway.gerarCobrancaPix(any(), any(), any(), any()))
                .thenReturn(new MercadoPagoGatewayPort.MercadoPagoCobrancaResult(
                        "MP-9988", "qr-code-pix-payload", "https://link.com", "pending"));
        when(repository.salvar(any())).thenAnswer(invocation -> invocation.getArgument(0));

        IniciarPagamentoRequest request = IniciarPagamentoRequest.builder()
                .formaPagamento(FormaPagamento.PIX_MERCADO_PAGO)
                .build();

        OrcamentoResponse response = useCase.executar(10L, request);

        assertNotNull(response.getPagamento());
        verify(mercadoPagoGateway, times(1)).gerarCobrancaPix(eq(10L), eq("OS-123"), any(), any());
    }
}
