package com.oficina.billing.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Payload recebido via Webhook do Mercado Pago")
public class WebhookMercadoPagoRequest {

    private String id;
    private String action;
    private String type;

    @JsonProperty("data")
    private Map<String, Object> data;
}
