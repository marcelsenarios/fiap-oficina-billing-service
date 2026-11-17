package com.oficina.billing.application.dto;

import com.oficina.billing.domain.model.FormaPagamento;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Requisição para gerar cobrança e checkout via Mercado Pago")
public class IniciarPagamentoRequest {

    @NotNull(message = "Forma de pagamento é obrigatória")
    @Schema(description = "Meio de pagamento desejado", example = "PIX_MERCADO_PAGO")
    private FormaPagamento formaPagamento;
}
