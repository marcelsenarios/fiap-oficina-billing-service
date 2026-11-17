package com.oficina.billing.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Requisição para início de um novo Orçamento")
public class CriarOrcamentoRequest {

    @NotNull(message = "ID da Ordem de Serviço é obrigatório")
    @Schema(description = "ID numérico da OS correspondente", example = "1")
    private Long osId;

    @NotBlank(message = "Número da OS é obrigatório")
    @Schema(description = "Código alfanumérico da OS", example = "OS-A1B2C3D4")
    private String numeroOs;

    @Schema(description = "CPF/CNPJ do cliente para cobrança", example = "52998224725")
    private String clienteCpfCnpj;
}
