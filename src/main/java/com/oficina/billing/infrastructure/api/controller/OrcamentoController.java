package com.oficina.billing.infrastructure.api.controller;

import com.oficina.billing.application.dto.*;
import com.oficina.billing.application.usecase.*;
import com.oficina.billing.domain.model.StatusOrcamento;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/orcamentos")
@RequiredArgsConstructor
@Tag(name = "Orçamentos e Pagamentos", description = "Endpoints para gestão de orçamentos e pagamentos integrados ao Mercado Pago")
public class OrcamentoController {

    private final CriarOrcamentoUseCase criarUseCase;
    private final AdicionarItemOrcamentoUseCase adicionarItemUseCase;
    private final RemoverItemOrcamentoUseCase removerItemUseCase;
    private final FinalizarOrcamentoUseCase finalizarUseCase;
    private final IniciarPagamentoUseCase iniciarPagamentoUseCase;
    private final ConsultarOrcamentoUseCase consultarUseCase;
    private final CancelarOrcamentoUseCase cancelarUseCase;

    @PostMapping
    @Operation(summary = "Criar novo Orçamento", description = "Inicia elaboração de orçamento para uma Ordem de Serviço")
    public ResponseEntity<OrcamentoResponse> criar(@Valid @RequestBody CriarOrcamentoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(criarUseCase.executar(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar Orçamento por ID", description = "Recupera dados detalhados do orçamento")
    public ResponseEntity<OrcamentoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(consultarUseCase.buscarPorId(id));
    }

    @GetMapping("/os/{osId}")
    @Operation(summary = "Buscar Orçamento por ID da OS", description = "Localiza o orçamento associado à OS")
    public ResponseEntity<OrcamentoResponse> buscarPorOsId(@PathVariable Long osId) {
        return ResponseEntity.ok(consultarUseCase.buscarPorOsId(osId));
    }

    @GetMapping
    @Operation(summary = "Listar Orçamentos", description = "Lista orçamentos com filtro opcional por status")
    public ResponseEntity<List<OrcamentoResponse>> listar(@RequestParam(required = false) StatusOrcamento status) {
        return ResponseEntity.ok(consultarUseCase.listar(status));
    }

    @PostMapping("/{id}/itens")
    @Operation(summary = "Adicionar item ao orçamento", description = "Insere peça ou serviço no orçamento em elaboração")
    public ResponseEntity<OrcamentoResponse> adicionarItem(
            @PathVariable Long id,
            @Valid @RequestBody AdicionarItemRequest request) {
        return ResponseEntity.ok(adicionarItemUseCase.executar(id, request));
    }

    @DeleteMapping("/{id}/itens/{itemId}")
    @Operation(summary = "Remover item do orçamento", description = "Remove um item do orçamento em elaboração")
    public ResponseEntity<OrcamentoResponse> removerItem(
            @PathVariable Long id,
            @PathVariable Long itemId) {
        return ResponseEntity.ok(removerItemUseCase.executar(id, itemId));
    }

    @PostMapping("/{id}/finalizar")
    @Operation(summary = "Finalizar elaboração de orçamento", description = "Fecha o orçamento e publica evento para o Saga Pattern (OS Service)")
    public ResponseEntity<OrcamentoResponse> finalizar(@PathVariable Long id) {
        return ResponseEntity.ok(finalizarUseCase.executar(id));
    }

    @PostMapping("/{id}/pagamento")
    @Operation(summary = "Iniciar pagamento via Mercado Pago", description = "Gera cobrança com QR Code Pix no Mercado Pago")
    public ResponseEntity<OrcamentoResponse> iniciarPagamento(
            @PathVariable Long id,
            @Valid @RequestBody IniciarPagamentoRequest request) {
        return ResponseEntity.ok(iniciarPagamentoUseCase.executar(id, request));
    }

    @PostMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar orçamento", description = "Executa cancelamento manual ou compensatório no Saga Pattern")
    public ResponseEntity<OrcamentoResponse> cancelar(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        String motivo = (body != null) ? body.get("motivo") : null;
        return ResponseEntity.ok(cancelarUseCase.executar(id, motivo));
    }
}
