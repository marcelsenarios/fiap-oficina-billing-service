package com.oficina.billing.bdd;

import com.oficina.billing.domain.model.*;
import io.cucumber.java.pt.*;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class OrcamentoCucumberSteps {

    private Long osId;
    private String numeroOs;
    private Orcamento orcamento;

    @Dado("que uma nova OS foi recebida com ID {int} e número {string}")
    public void queUmaNovaOsFoiRecebidaComIdENumero(Integer id, String numero) {
        this.osId = id.longValue();
        this.numeroOs = numero;
    }

    @Quando("eu inicio um orçamento para a OS")
    public void euInicioUmOrcamentoParaAOs() {
        this.orcamento = Orcamento.iniciar(this.osId, this.numeroOs, "52998224725");
    }

    @Quando("adiciono a peça {string} com quantidade {int} e valor unitário {string}")
    public void adicionoAPecaComQuantidadeEValorUnitario(String desc, Integer qtd, String valor) {
        orcamento.adicionarItem(ItemOrcamento.builder()
                .descricao(desc)
                .tipo(TipoItem.PECA)
                .quantidade(qtd)
                .valorUnitario(new BigDecimal(valor))
                .build());
    }

    @Quando("adiciono o serviço {string} com quantidade {int} e valor unitário {string}")
    public void adicionoOServicoComQuantidadeEValorUnitario(String desc, Integer qtd, String valor) {
        orcamento.adicionarItem(ItemOrcamento.builder()
                .descricao(desc)
                .tipo(TipoItem.SERVICO)
                .quantidade(qtd)
                .valorUnitario(new BigDecimal(valor))
                .build());
    }

    @Então("o valor total do orçamento deve ser {string}")
    public void oValorTotalDoOrcamentoDeveSer(String totalEsperado) {
        assertEquals(new BigDecimal(totalEsperado), orcamento.getValorTotal());
    }

    @Quando("eu finalizo a elaboração do orçamento")
    public void euFinalizoAElaboracaoDoOrcamento() {
        orcamento.finalizarElaboracao();
    }

    @Então("o status do orçamento deve ser {string}")
    @Então("o status do orçamento deve ser atualizado para {string}")
    public void oStatusDoOrcamentoDeveSer(String statusEsperado) {
        assertEquals(StatusOrcamento.valueOf(statusEsperado), orcamento.getStatus());
    }

    @Quando("eu gero a cobrança via Pix com o Mercado Pago")
    public void euGeroACobrancaViaPixComOMercadoPago() {
        Pagamento pagamento = Pagamento.criar(
                orcamento.getValorTotal(),
                FormaPagamento.PIX_MERCADO_PAGO,
                "MP-BDD-123",
                "pix-code",
                "https://mp.com"
        );
        orcamento.vincularPagamento(pagamento);
    }

    @Quando("a notificação de pagamento aprovado é processada")
    public void aNotificacaoDePagamentoAprovadoEProcessada() {
        orcamento.confirmarPagamento();
    }

    @Dado("que existe um orçamento com valor {string} aguardando pagamento")
    public void queExisteUmOrcamentoComValorAguardandoPagamento(String valor) {
        orcamento = Orcamento.iniciar(1L, "OS-123", "52998224725");
        orcamento.adicionarItem(ItemOrcamento.builder()
                .descricao("Item de teste")
                .tipo(TipoItem.SERVICO)
                .quantidade(1)
                .valorUnitario(new BigDecimal(valor))
                .build());
        orcamento.finalizarElaboracao();
    }

    @Quando("o webhook do Mercado Pago notifica recusa por {string}")
    public void oWebhookDoMercadoPagoNotificaRecusaPor(String motivo) {
        orcamento.recusar(motivo);
    }

    @Então("o motivo da recusa deve ser registrado")
    public void oMotivoDaRecusaDeveSerRegistrado() {
        assertNotNull(orcamento.getMotivoCancelamento());
        assertFalse(orcamento.getMotivoCancelamento().isBlank());
    }
}
