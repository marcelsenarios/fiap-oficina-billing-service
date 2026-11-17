# language: pt
Funcionalidade: Elaboração de Orçamento e Cobrança com Mercado Pago (Saga Pattern)
  Como responsável financeiro da oficina
  Quero compor orçamentos com peças e serviços e gerar cobrança via Mercado Pago
  Para garantir o pagamento antes da liberação do veículo para a produção

  Cenário: Elaboração de orçamento e confirmação de pagamento Pix (Transação Pivot)
    Dado que uma nova OS foi recebida com ID 1 e número "OS-12345678"
    Quando eu inicio um orçamento para a OS
    E adiciono a peça "Pastilha de Freio Cerâmica" com quantidade 2 e valor unitário "150.00"
    E adiciono o serviço "Troca de Pastilhas e Sangria" com quantidade 1 e valor unitário "120.00"
    Então o valor total do orçamento deve ser "420.00"
    Quando eu finalizo a elaboração do orçamento
    Então o status do orçamento deve ser "AGUARDANDO_PAGAMENTO"
    Quando eu gero a cobrança via Pix com o Mercado Pago
    E a notificação de pagamento aprovado é processada
    Então o status do orçamento deve ser atualizado para "PAGO"

  Cenário: Recusa de pagamento pelo Mercado Pago (Compensação Saga)
    Dado que existe um orçamento com valor "800.00" aguardando pagamento
    Quando o webhook do Mercado Pago notifica recusa por "Saldo insuficiente"
    Então o status do orçamento deve ser atualizado para "RECUSADO"
    E o motivo da recusa deve ser registrado
