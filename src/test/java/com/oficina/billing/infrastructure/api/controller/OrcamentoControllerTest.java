package com.oficina.billing.infrastructure.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oficina.billing.application.dto.*;
import com.oficina.billing.application.usecase.*;
import com.oficina.billing.domain.model.FormaPagamento;
import com.oficina.billing.domain.model.StatusOrcamento;
import com.oficina.billing.domain.model.TipoItem;
import com.oficina.billing.infrastructure.api.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OrcamentoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CriarOrcamentoUseCase criarUseCase;

    @Mock
    private AdicionarItemOrcamentoUseCase adicionarItemUseCase;

    @Mock
    private RemoverItemOrcamentoUseCase removerItemUseCase;

    @Mock
    private FinalizarOrcamentoUseCase finalizarUseCase;

    @Mock
    private IniciarPagamentoUseCase iniciarPagamentoUseCase;

    @Mock
    private ConsultarOrcamentoUseCase consultarUseCase;

    @Mock
    private CancelarOrcamentoUseCase cancelarUseCase;

    @InjectMocks
    private OrcamentoController controller;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/orcamentos - Deve criar orçamento e retornar 201 Created")
    void deveCriarOrcamento() throws Exception {
        CriarOrcamentoRequest request = CriarOrcamentoRequest.builder()
                .osId(1L)
                .numeroOs("OS-1234")
                .clienteCpfCnpj("52998224725")
                .build();

        OrcamentoResponse response = OrcamentoResponse.builder()
                .id(1L)
                .osId(1L)
                .numeroOs("OS-1234")
                .status(StatusOrcamento.EM_ELABORACAO)
                .build();

        when(criarUseCase.executar(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/orcamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("EM_ELABORACAO"));
    }

    @Test
    @DisplayName("POST /api/v1/orcamentos/{id}/itens - Deve adicionar item")
    void deveAdicionarItem() throws Exception {
        AdicionarItemRequest request = AdicionarItemRequest.builder()
                .descricao("Pastilha")
                .tipo(TipoItem.PECA)
                .quantidade(2)
                .valorUnitario(new BigDecimal("90.00"))
                .build();

        OrcamentoResponse response = OrcamentoResponse.builder()
                .id(1L)
                .status(StatusOrcamento.EM_ELABORACAO)
                .valorTotal(new BigDecimal("180.00"))
                .build();

        when(adicionarItemUseCase.executar(eq(1L), any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/orcamentos/1/itens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valorTotal").value(180.00));
    }

    @Test
    @DisplayName("POST /api/v1/orcamentos/{id}/finalizar - Deve finalizar elaboração")
    void deveFinalizar() throws Exception {
        OrcamentoResponse response = OrcamentoResponse.builder()
                .id(1L)
                .status(StatusOrcamento.AGUARDANDO_PAGAMENTO)
                .valorTotal(new BigDecimal("180.00"))
                .build();

        when(finalizarUseCase.executar(1L)).thenReturn(response);

        mockMvc.perform(post("/api/v1/orcamentos/1/finalizar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("AGUARDANDO_PAGAMENTO"));
    }

    @Test
    @DisplayName("POST /api/v1/orcamentos/{id}/pagamento - Deve iniciar cobrança Mercado Pago")
    void deveIniciarPagamento() throws Exception {
        IniciarPagamentoRequest request = IniciarPagamentoRequest.builder()
                .formaPagamento(FormaPagamento.PIX_MERCADO_PAGO)
                .build();

        OrcamentoResponse response = OrcamentoResponse.builder()
                .id(1L)
                .status(StatusOrcamento.AGUARDANDO_PAGAMENTO)
                .pagamento(PagamentoResponse.builder().idTransacaoMercadoPago("MP-123").build())
                .build();

        when(iniciarPagamentoUseCase.executar(eq(1L), any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/orcamentos/1/pagamento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pagamento.idTransacaoMercadoPago").value("MP-123"));
    }

    @Test
    @DisplayName("GET /api/v1/orcamentos - Deve listar orçamentos")
    void deveListar() throws Exception {
        OrcamentoResponse response = OrcamentoResponse.builder()
                .id(1L)
                .status(StatusOrcamento.EM_ELABORACAO)
                .build();

        when(consultarUseCase.listar(any())).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/orcamentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
}
