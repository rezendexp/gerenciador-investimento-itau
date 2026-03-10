package com.itau.investimento.api.exception;

import com.itau.investimento.api.controller.InvestimentoController;
import com.itau.investimento.domain.service.InvestimentoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InvestimentoController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InvestimentoService investimentoService;

    @Test
    @DisplayName("Deve retornar erro amigável quando o JSON enviado for inválido")
    void deveRetornar400QuandoJsonInvalido() throws Exception {
        String jsonInvalido = "{\"clienteId\": \"\", \"valor\": 0, \"tipo\": \"INVALIDO\"}";

        mockMvc.perform(post("/api/investimentos/comprar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.valor").exists())
                .andExpect(jsonPath("$.clienteId").exists());
    }

    @Test
    @DisplayName("Deve retornar erro 500 quando ocorrer uma exceção inesperada")
    void deveRetornar500QuandoOcorrerExcecaoGenerica() throws Exception {
        // Simula erro no service
        doThrow(new RuntimeException("Erro imprevisto"))
                .when(investimentoService).processarInvestimento(any());

        // Usando um tipo VÁLIDO - COMPRA_ACOES em vez de CDB
        String jsonValido = "{\"clienteId\": \"cli-123\", \"valor\": 100.0, \"tipo\": \"COMPRA_ACOES\"}";

        mockMvc.perform(post("/api/investimentos/comprar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonValido))
                .andExpect(status().isInternalServerError());
    }
}