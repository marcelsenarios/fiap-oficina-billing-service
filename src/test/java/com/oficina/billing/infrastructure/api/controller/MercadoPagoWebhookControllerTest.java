package com.oficina.billing.infrastructure.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oficina.billing.application.dto.WebhookMercadoPagoRequest;
import com.oficina.billing.application.usecase.ProcessarWebhookPagamentoUseCase;
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

import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MercadoPagoWebhookControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProcessarWebhookPagamentoUseCase webhookUseCase;

    @InjectMocks
    private MercadoPagoWebhookController controller;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("POST /api/v1/webhook/mercadopago - Deve processar webhook com body JSON")
    void deveProcessarWebhookBody() throws Exception {
        WebhookMercadoPagoRequest payload = WebhookMercadoPagoRequest.builder()
                .action("payment.created")
                .data(Map.of("id", "MP-12345"))
                .build();

        mockMvc.perform(post("/api/v1/webhook/mercadopago")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk());

        verify(webhookUseCase, times(1)).executar("MP-12345");
    }

    @Test
    @DisplayName("POST /api/v1/webhook/mercadopago?id=MP-999&topic=payment - Deve processar webhook com query param")
    void deveProcessarWebhookQueryParam() throws Exception {
        mockMvc.perform(post("/api/v1/webhook/mercadopago")
                        .param("id", "MP-999")
                        .param("topic", "payment"))
                .andExpect(status().isOk());

        verify(webhookUseCase, times(1)).executar("MP-999");
    }
}
