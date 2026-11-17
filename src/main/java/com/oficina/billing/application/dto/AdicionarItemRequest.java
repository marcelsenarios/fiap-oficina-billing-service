package com.oficina.billing.application.dto;

import com.oficina.billing.domain.model.TipoItem;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Requisição para inclusão de item (peça ou serviço) no orçamento")
public class AdicionarItemRequest {

    @NotBlank(message = "Descrição do item é obrigatória")
    @Schema(description = "Descrição detalhada da peça ou mão de obra", example = "Pastilhas de freio dianteiras")
    private String descricao;

    @NotNull(message = "Tipo do item é obrigatório (PECA ou SERVICO)")
    @Schema(description = "Classificação do item", example = "PECA")
    private TipoItem tipo;

    @NotNull(message = "Quantidade é obrigatória")
    @Positive(message = "Quantidade deve ser maior que zero")
    @Schema(description = "Quantidade de peças ou horas de serviço", example = "1")
    private Integer quantidade;

    @NotNull(message = "Valor unitário é obrigatório")
    @Positive(message = "Valor unitário deve ser maior que zero")
    @Schema(description = "Valor unitário cobrado", example = "180.00")
    private BigDecimal valorUnitario;
}
