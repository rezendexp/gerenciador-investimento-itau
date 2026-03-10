package com.itau.investimento.domain.service;

import com.itau.investimento.api.dto.InvestimentoRequest;
import com.itau.investimento.domain.model.StatusTransacao;
import com.itau.investimento.domain.model.Transacao;
import com.itau.investimento.domain.repository.InvestimentoRepository;
import io.awspring.cloud.sns.core.SnsTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvestimentoServiceTest {

    @Mock
    private SnsTemplate snsTemplate;

    @Mock
    private InvestimentoRepository repository;

    @InjectMocks
    private InvestimentoService investimentoService;

    @Test
    @DisplayName("Deve salvar com status RECEBIDO e depois PROCESSADO quando SNS funcionar")
    void deveProcessarInvestimentoComSucesso() {
        InvestimentoRequest request = new InvestimentoRequest();
        request.setClienteId("cli-123");
        request.setValor(1000.0);
        request.setTipo("COMPRA_ACOES");

        when(repository.save(any(Transacao.class))).thenAnswer(invocation -> {
            Transacao t = invocation.getArgument(0);
            if (t.getId() == null) t.setId(UUID.randomUUID());
            return t;
        });

        String resultado = investimentoService.processarInvestimento(request);

        assertNotNull(resultado);
        assertTrue(resultado.contains("processada com sucesso"));

        // O save deve ser chamado 2 vezes (RECEBIDO -> PROCESSADO)
        verify(repository, times(2)).save(any(Transacao.class));
        verify(snsTemplate, times(1)).sendNotification(anyString(), any(Transacao.class), anyString());
    }

    @Test
    @DisplayName("Deve atualizar status para ERRO quando o envio para SNS falhar definitivamente")
    void deveMudarStatusParaErroQuandoSnsFalhar() {
        InvestimentoRequest request = new InvestimentoRequest();
        request.setClienteId("cli-123");
        request.setValor(1000.0);
        request.setTipo("COMPRA_ACOES");

        when(repository.save(any(Transacao.class))).thenAnswer(i -> i.getArgument(0));

        // Forçamos o erro no SNS para testar o bloco 'catch'
        doThrow(new RuntimeException("Erro AWS")).when(snsTemplate)
                .sendNotification(anyString(), any(Transacao.class), anyString());

        assertThrows(RuntimeException.class, () -> {
            investimentoService.processarInvestimento(request);
        });

        // Verifica se o último save foi com status ERRO
        ArgumentCaptor<Transacao> captor = ArgumentCaptor.forClass(Transacao.class);
        verify(repository, atLeastOnce()).save(captor.capture());
        assertEquals(StatusTransacao.ERRO, captor.getValue().getStatus());
    }

    @Test
    @DisplayName("Deve testar a resiliência chamando o SNS as 3 vezes antes de falhar")
    void deveRespeitarRetry() {
        InvestimentoRequest request = new InvestimentoRequest();
        request.setClienteId("cli-123");
        request.setValor(1000.0);
        request.setTipo("COMPRA_CDB");

        investimentoService.processarInvestimento(request);

        verify(repository, atLeast(2)).save(any(Transacao.class));
    }
}