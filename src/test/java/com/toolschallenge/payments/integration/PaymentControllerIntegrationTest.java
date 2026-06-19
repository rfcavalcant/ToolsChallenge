package com.toolschallenge.payments.integration;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.toolschallenge.payments.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PaymentRepository paymentRepository;

    @BeforeEach
    void setUp() {
        paymentRepository.deleteAll();
    }

    @Test
    void shouldCreatePaymentAndReturnCreatedResponse() throws Exception {
        mockMvc.perform(post("/pagamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
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
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/pagamentos/100023568900001"))
                .andExpect(jsonPath("$.transacao.cartao").value("4444********1234"))
                .andExpect(jsonPath("$.transacao.id").value("100023568900001"))
                .andExpect(jsonPath("$.transacao.descricao.valor").value("500.50"))
                .andExpect(jsonPath("$.transacao.descricao.nsu", matchesPattern("\\d{10}")))
                .andExpect(jsonPath("$.transacao.descricao.codigoAutorizacao", matchesPattern("\\d{9}")))
                .andExpect(jsonPath("$.transacao.descricao.status").value("AUTORIZADO"));
    }

    @Test
    void shouldCreateDeniedPaymentWhenInstallmentsAreInvalidForCashPayment() throws Exception {
        mockMvc.perform(post("/pagamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "transacao": {
                                    "cartao": "4444********1234",
                                    "id": "100023568900002",
                                    "descricao": {
                                      "valor": 500.50,
                                      "dataHora": "01/05/2021 18:30:00",
                                      "estabelecimento": "PetShop Mundo cao"
                                    },
                                    "formaPagamento": {
                                      "tipo": "AVISTA",
                                      "parcelas": "2"
                                    }
                                  }
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transacao.descricao.status").value("NEGADO"));
    }

    @Test
    void shouldReturnConflictWhenTransactionIdAlreadyExists() throws Exception {
        String payload = """
                {
                  "transacao": {
                    "cartao": "4444********1234",
                    "id": "100023568900003",
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
                }
                """;

        mockMvc.perform(post("/pagamentos").contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/pagamentos").contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Transacao com id 100023568900003 ja existe"));
    }

    @Test
    void shouldRefundPayment() throws Exception {
        mockMvc.perform(post("/pagamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "transacao": {
                                    "cartao": "4444********1234",
                                    "id": "100023568900004",
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
                                }
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/pagamentos/100023568900004/estorno"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transacao.descricao.status").value("CANCELADO"));
    }

    @Test
    void shouldReturnConflictWhenRefundingCancelledPayment() throws Exception {
        mockMvc.perform(post("/pagamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "transacao": {
                                    "cartao": "4444********1234",
                                    "id": "100023568900005",
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
                                }
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/pagamentos/100023568900005/estorno"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/pagamentos/100023568900005/estorno"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Transacao com id 100023568900005 ja esta cancelada"));
    }

    @Test
    void shouldReturnAllPayments() throws Exception {
        mockMvc.perform(post("/pagamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "transacao": {
                                    "cartao": "4444********1234",
                                    "id": "100023568900006",
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
                                }
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/pagamentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void shouldReturnPaymentById() throws Exception {
        mockMvc.perform(post("/pagamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "transacao": {
                                    "cartao": "4444********1234",
                                    "id": "100023568900007",
                                    "descricao": {
                                      "valor": 500.50,
                                      "dataHora": "01/05/2021 18:30:00",
                                      "estabelecimento": "PetShop Mundo cao"
                                    },
                                    "formaPagamento": {
                                      "tipo": "PARCELADO LOJA",
                                      "parcelas": "2"
                                    }
                                  }
                                }
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/pagamentos/100023568900007"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transacao.formaPagamento.tipo").value("PARCELADO LOJA"))
                .andExpect(jsonPath("$.transacao.id").value("100023568900007"));
    }

    @Test
    void shouldReturnNotFoundWhenTransactionDoesNotExist() throws Exception {
        mockMvc.perform(get("/pagamentos/missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Transacao nao encontrada para id missing"));
    }

    @Test
    void shouldRejectInvalidPayload() throws Exception {
        mockMvc.perform(post("/pagamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "transacao": {
                                    "cartao": "44441234",
                                    "id": "",
                                    "descricao": {
                                      "valor": 500.50,
                                      "dataHora": "2021-05-01T18:30:00",
                                      "estabelecimento": ""
                                    },
                                    "formaPagamento": {
                                      "tipo": "PIX",
                                      "parcelas": "A"
                                    }
                                  }
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details", hasSize(6)));
    }
}
