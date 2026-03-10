package com.itau.investimento.domain.model;

import com.itau.investimento.api.dto.InvestimentoRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class TransacaoTest {

    @Test
    @DisplayName("Deve testar getters e setters da Transacao")
    void testGetterAndSetter() {
        InvestimentoRequest request = new InvestimentoRequest();
        request.setClienteId("123");
        request.setValor(100.0);
        request.setTipo("CDB");

        Transacao t = new Transacao(request);
        LocalDateTime agora = LocalDateTime.now();

        UUID id = UUID.randomUUID();
        t.setId(id);
        t.setClienteId("123");
        t.setValor(100.0);
        t.setTipo("CDB");
        t.setDataCriacao(agora);

        assertEquals(id, t.getId());
        assertEquals("123", t.getClienteId());
        assertEquals(100.0, t.getValor());
        assertEquals("CDB", t.getTipo());
        assertEquals(agora, t.getDataCriacao());
    }

    @Test
    @DisplayName("Deve criar Transacao a partir de um InvestimentoRequest")
    void deveCriarTransacaoAPartirDoRequest() {
        InvestimentoRequest request = new InvestimentoRequest();
        request.setClienteId("cli-itau");
        request.setValor(200.0);
        request.setTipo("COMPRA_CDB");

        Transacao t = new Transacao(request);

        assertEquals("cli-itau", t.getClienteId());
        assertEquals(200.0, t.getValor());
        assertEquals("COMPRA_CDB", t.getTipo());
    }
}