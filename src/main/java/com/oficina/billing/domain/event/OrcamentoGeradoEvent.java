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
public class OrcamentoGeradoEvent implements Serializable {
    private String eventId;
    private Long osId;
    private String numeroOs;
    private Long orcamentoId;
    private BigDecimal valorTotal;
    private LocalDateTime dataCriacao;
}
