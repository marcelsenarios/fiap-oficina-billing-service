package com.oficina.billing.domain.event;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagamentoStatusEvent implements Serializable {
    private String eventId;
    private Long osId;
    private String numeroOs;
    private Long orcamentoId;
    private String idTransacao;
    private String statusPagamento; // APROVADO, RECUSADO
    private BigDecimal valor;
    private String motivo;
    private LocalDateTime dataEvento;
}
