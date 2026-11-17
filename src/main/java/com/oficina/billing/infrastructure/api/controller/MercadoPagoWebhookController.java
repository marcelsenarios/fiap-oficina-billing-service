package com.oficina.billing.infrastructure.api.controller;

import com.oficina.billing.application.dto.WebhookMercadoPagoRequest;
import com.oficina.billing.application.usecase.ProcessarWebhookPagamentoUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/webhook/mercadopago")
@RequiredArgsConstructor
@Tag(name = "Webhook Mercado Pago", description = "Endpoint para recepção assíncrona de notificações de pagamento do Mercado Pago")
public class MercadoPagoWebhookController {

    private final ProcessarWebhookPagamentoUseCase webhookUseCase;

    @PostMapping
    @Operation(summary = "Webhook Mercado Pago", description = "Recebe notificação de alteração de status de pagamento e aciona o Saga Pattern")
    public ResponseEntity<Void> receberNotificacao(
            @RequestBody(required = false) WebhookMercadoPagoRequest payload,
            @RequestParam(value = "id", required = false) String paramId,
            @RequestParam(value = "topic", required = false) String topic) {

        log.info("Webhook Mercado Pago recebido. Param ID: {}, Topic: {}, Payload: {}", paramId, topic, payload);

        String idTransacao = null;
        if (payload != null && payload.getData() != null && payload.getData().get("id") != null) {
            idTransacao = String.valueOf(payload.getData().get("id"));
        } else if (payload != null && payload.getId() != null) {
            idTransacao = payload.getId();
        } else if (paramId != null) {
            idTransacao = paramId;
        }

        if (idTransacao != null) {
            webhookUseCase.executar(idTransacao);
        }

        return ResponseEntity.ok().build();
    }
}
