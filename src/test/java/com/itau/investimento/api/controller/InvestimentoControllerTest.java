package com.itau.investimento.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itau.investimento.domain.service.InvestimentoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InvestimentoController.class)
class InvestimentoControllerTest {

    @Autowired
    private MockMvc mockMvc; // Simula requisições HTTP

    @MockBean
    private InvestimentoService investimentoService; // Simula o Service

    @Autowired
    private ObjectMapper objectMapper; // Converte objeto para JSON

    @Test
    @DisplayName("Deve retornar 200 OK ao receber um investimento válido")
    void deveRetornar200AoEnviarInvestimentoValido() throws Exception {
        // Dados de teste
        String json = "{\"clienteId\": \"cli-123\", \"valor\": 1000.0, \"tipo\": \"COMPRA_ACOES\"}";

        mockMvc.perform(post("/api/investimentos/comprar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        // Verifica se o Controller realmente passou o trabalho para o Service
        verify(investimentoService, times(1)).processarInvestimento(any());
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request quando o valor for zero")
    void deveRetornar400QuandoValorZero() throws Exception {
        String json = "{\"clienteId\": \"cli-123\", \"valor\": 0, \"tipo\": \"COMPRA_ACOES\"}";

        mockMvc.perform(post("/api/investimentos/comprar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }
}