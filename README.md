[![AR Phoenix](https://media.licdn.com/dms/image/v2/C4E1BAQHRA1zgPSQ6FQ/company-background_10000/company-background_10000/0/1584243318556/toolssoftware_cover?e=2147483647&v=beta&t=Vo-FwQh9Kbr9dkReYHfPKI6eQFmPnVoYlPBEEBzkyIA)](https://tools.com.br)
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

### Exemplo de pagamento

```bash
curl --request POST "http://localhost:8080/pagamentos" \
  --header "Content-Type: application/json" \
  --data '{
    "transacao": {
      "cartao": "4444********1234",
      "id": "100023568900001",
      "descricao": {
        "valor": 500.50,
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
