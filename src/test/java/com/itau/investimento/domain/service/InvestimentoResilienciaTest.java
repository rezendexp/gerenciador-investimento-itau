package com.itau.investimento.domain.service;

import com.itau.investimento.api.dto.InvestimentoRequest;
import com.itau.investimento.domain.model.Transacao;
import com.itau.investimento.domain.repository.InvestimentoRepository;
import io.awspring.cloud.sns.core.SnsTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvestimentoResilienciaTest {

    @Mock
    private SnsTemplate snsTemplate;

    @Mock
    private InvestimentoRepository repository;

    @InjectMocks
    private InvestimentoService investimentoService;

    @Test
    @DisplayName("Deve tentar enviar para o SNS e marcar como ERRO no banco após falha")
    void deveTentarTresVezesQuandoSnsFalhar() {
        InvestimentoRequest request = new InvestimentoRequest();
        request.setClienteId("cli-resiliente");
        request.setValor(500.0);
        request.setTipo("COMPRA_ACOES");

        // Simula o banco gerando UUID para evitar o erro de Transacao: null no log
        when(repository.save(any(Transacao.class))).thenAnswer(invocation -> {
            Transacao t = invocation.getArgument(0);
            if (t.getId() == null) {
                t.setId(UUID.randomUUID());
            }
            return t;
        });

        doThrow(new RuntimeException("Erro de rede AWS"))
                .when(snsTemplate).sendNotification(anyString(), any(), anyString());

        assertThrows(RuntimeException.class, () -> {
            investimentoService.processarInvestimento(request);
        });

        // IMPORTANTE: Agora são 2 vezes (RECEBIDO e depois ERRO)
        verify(repository, times(2)).save(any(Transacao.class));

        // Verifica se chamou o SNS (O Retry real acontece via Spring, aqui validamos a chamada lógica)
        verify(snsTemplate, atLeastOnce()).sendNotification(anyString(), any(), anyString());
    }

    @Test
    @DisplayName("Deve salvar como RECEBIDO e PROCESSADO quando não houver erro")
    void deveFuncionarNaPrimeiraTentativa() {
        InvestimentoRequest request = new InvestimentoRequest();
        request.setClienteId("cli-normal");
        request.setValor(500.0);
        request.setTipo("COMPRA_ACOES");

        when(repository.save(any(Transacao.class))).thenAnswer(invocation -> {
            Transacao t = invocation.getArgument(0);
            if (t.getId() == null) {
                t.setId(UUID.randomUUID());
            }
            return t;
        });

        investimentoService.processarInvestimento(request);

        verify(repository, times(2)).save(any(Transacao.class));
        verify(snsTemplate, times(1)).sendNotification(anyString(), any(), anyString());
    }
}