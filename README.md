# ToolsChallenge

API REST de pagamentos de cartao de credito desenvolvida como resposta ao desafio tecnico proposto. O projeto foi implementado com Spring Boot 3.5, Java 21 e persistencia em memoria, mantendo o escopo estrito do enunciado: pagamento, estorno e consulta de transacoes.

## Como executar

```bash
mvn spring-boot:run
```

A aplicacao sobe por padrao em `http://localhost:8080`.

## Como testar

```bash
mvn test
```

Para validar build completa:

```bash
mvn clean test
```

## Endpoints

| Metodo | Endpoint | Descricao |
| --- | --- | --- |
| POST | `/pagamentos` | Cria uma transacao e retorna dados autorizados ou negados |
| POST | `/pagamentos/{id}/estorno` | Cancela uma transacao existente |
| GET | `/pagamentos` | Lista todas as transacoes |
| GET | `/pagamentos/{id}` | Consulta uma transacao por id |

## Roteiro rapido de demonstracao manual

Se voce quiser demonstrar a API ao vivo em entrevista, esta sequencia cobre o fluxo feliz e os casos de borda mais provaveis.

### 1. Subir a aplicacao

```bash
mvn spring-boot:run
```

### 2. Criar um pagamento autorizado

```bash
curl --request POST "http://localhost:8080/pagamentos" \
  --header "Content-Type: application/json" \
  --data '{
    "transacao": {
      "cartao": "4444********1234",
      "id": "100023568900001",
      "descricao": {
        "valor": "500.50",
        "dataHora": "01/05/2021 18:30:00",
        "estabelecimento": "PetShop Mundo cao"
      },
      "formaPagamento": {
        "tipo": "AVISTA",
        "parcelas": "1"
      }
    }
  }'
```

### 3. Consultar a transacao criada

```bash
curl "http://localhost:8080/pagamentos/100023568900001"
```

### 4. Listar todas as transacoes

```bash
curl "http://localhost:8080/pagamentos"
```

### 5. Estornar a transacao

```bash
curl --request POST "http://localhost:8080/pagamentos/100023568900001/estorno"
```

### 6. Tentar estornar de novo e validar o `409 Conflict`

```bash
curl --request POST "http://localhost:8080/pagamentos/100023568900001/estorno"
```

### 7. Tentar recriar o mesmo id e validar o `409 Conflict`

```bash
curl --request POST "http://localhost:8080/pagamentos" \
  --header "Content-Type: application/json" \
  --data '{
    "transacao": {
      "cartao": "4444********1234",
      "id": "100023568900001",
      "descricao": {
        "valor": "500.50",
        "dataHora": "01/05/2021 18:30:00",
        "estabelecimento": "PetShop Mundo cao"
      },
      "formaPagamento": {
        "tipo": "AVISTA",
        "parcelas": "1"
      }
    }
  }'
```

### 8. Enviar payload invalido e validar o `400 Bad Request`

```bash
curl --request POST "http://localhost:8080/pagamentos" \
  --header "Content-Type: application/json" \
  --data '{
    "transacao": {
      "cartao": "44441234",
      "id": "",
      "descricao": {
        "valor": "500.50",
        "dataHora": "2021-05-01T18:30:00",
        "estabelecimento": ""
      },
      "formaPagamento": {
        "tipo": "PIX",
        "parcelas": "A"
      }
    }
  }'
```

### 9. Consultar id inexistente e validar o `404 Not Found`

```bash
curl "http://localhost:8080/pagamentos/nao-existe"
```

### Exemplo de pagamento

```bash
curl --request POST "http://localhost:8080/pagamentos" \
  --header "Content-Type: application/json" \
  --data '{
    "transacao": {
      "cartao": "4444********1234",
      "id": "100023568900001",
      "descricao": {
        "valor": "500.50",
        "dataHora": "01/05/2021 18:30:00",
        "estabelecimento": "PetShop Mundo cao"
      },
      "formaPagamento": {
        "tipo": "AVISTA",
        "parcelas": "1"
      }
    }
  }'
```

### Exemplo de estorno

```bash
curl --request POST "http://localhost:8080/pagamentos/100023568900001/estorno"
```

### Exemplo de consulta por id

```bash
curl "http://localhost:8080/pagamentos/100023568900001"
```

### Exemplo de consulta geral

```bash
curl "http://localhost:8080/pagamentos"
```

## Arquitetura

O projeto segue arquitetura em camadas:

- `controller`: expoe os endpoints REST e lida com contratos HTTP.
- `service`: concentra as regras de negocio de pagamento, estorno e consulta.
- `repository`: abstrai a persistencia em memoria usando `ConcurrentHashMap`.
- `api/dto`: separa o contrato HTTP do dominio interno.
- `api/mapper`: converte DTOs para entidades de dominio e vice-versa.

## Strategy Pattern

As regras de autorizacao por tipo de pagamento foram implementadas com `PaymentAuthorizationStrategy`. Cada tipo possui sua propria estrategia:

- `CashPaymentAuthorizationStrategy`
- `StoreInstallmentAuthorizationStrategy`
- `IssuerInstallmentAuthorizationStrategy`

O `PaymentAuthorizationStrategyFactory` seleciona a estrategia correta com base no tipo informado. Isso permite adicionar novos tipos de pagamento sem alterar o fluxo principal do `PaymentService`.

## Regra de autorizacao adotada

Para tornar o criterio de `AUTORIZADO` versus `NEGADO` simples e explicito:

- `AVISTA`: autoriza apenas se `valor > 0` e `parcelas = 1`.
- `PARCELADO LOJA`: autoriza apenas se `valor > 0` e `parcelas` estiver entre `2` e `12`.
- `PARCELADO EMISSOR`: autoriza apenas se `valor > 0` e `parcelas` estiver entre `2` e `24`.
- Qualquer transacao fora dessas regras e persistida com status `NEGADO`.
- O estorno altera o status para `CANCELADO` e nao pode ser executado duas vezes para a mesma transacao.

## Tratamento de erros

As respostas de erro sao centralizadas em `@RestControllerAdvice`, com o seguinte comportamento:

- `201 Created` para pagamento.
- `200 OK` para consulta e estorno.
- `400 Bad Request` para erros de validacao e payloads invalidos.
- `404 Not Found` para transacoes inexistentes.
- `409 Conflict` para id duplicado e estorno de transacao ja cancelada.

## Testes

Os testes cobrem:

- unitarios do service com Mockito;
- unitarios das strategies;
- integracao dos endpoints com MockMvc, incluindo payloads reais, validacao e codigos HTTP.
