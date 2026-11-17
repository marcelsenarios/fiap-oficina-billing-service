package com.oficina.billing.infrastructure.messaging.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OsCanceladaIntegrationMsg implements Serializable {
    private Long osId;
    private String numeroOs;
    private String motivo;
}
