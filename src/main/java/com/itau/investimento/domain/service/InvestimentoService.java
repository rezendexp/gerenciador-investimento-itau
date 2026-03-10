package com.itau.investimento.domain.service;

import com.itau.investimento.api.dto.InvestimentoRequest;
import com.itau.investimento.domain.model.StatusTransacao;
import com.itau.investimento.domain.model.Transacao;
import com.itau.investimento.domain.repository.InvestimentoRepository;
import io.awspring.cloud.sns.core.SnsTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InvestimentoService {

    private final SnsTemplate snsTemplate;
    private final InvestimentoRepository repository;
    private static final Logger log = LoggerFactory.getLogger(InvestimentoService.class);

    public InvestimentoService(SnsTemplate snsTemplate, InvestimentoRepository repository) {
        this.snsTemplate = snsTemplate;
        this.repository = repository;
    }

    @Transactional
    @Retryable(
            retryFor = { RuntimeException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000)
    )
    public String processarInvestimento(InvestimentoRequest request) {
        Transacao transacao = new Transacao(request);
        transacao.setStatus(StatusTransacao.RECEBIDO);
        repository.save(transacao);

        try {
            enviarParaSns(transacao);
            transacao.setStatus(StatusTransacao.PROCESSADO);
            repository.save(transacao);

            return "Transação " + transacao.getId() + " processada com sucesso!";

        } catch (RuntimeException e) {
            log.error("Falha definitiva ao processar transação: {}", transacao.getId());
            transacao.setStatus(StatusTransacao.ERRO);
            repository.save(transacao);
            throw e;
        }
    }

    public void enviarParaSns(Transacao transacao) {
        log.info("Publicando no SNS | Cliente: {} | Transação: {}",
                transacao.getClienteId(), transacao.getId());
        snsTemplate.sendNotification("topico-investimentos-itau", transacao, "Transacao");
    }
}