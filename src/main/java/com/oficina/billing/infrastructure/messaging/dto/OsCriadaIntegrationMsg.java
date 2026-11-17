package com.oficina.billing.infrastructure.messaging.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OsCriadaIntegrationMsg implements Serializable {
    private Long osId;
    private String numeroOs;
    private String clienteNome;
    private String clienteCpfCnpj;
    private String veiculoPlaca;
    private String descricaoProblema;
}
