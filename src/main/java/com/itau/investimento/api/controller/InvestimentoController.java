package com.itau.investimento.api.controller;

import com.itau.investimento.api.dto.InvestimentoRequest;
import com.itau.investimento.domain.service.InvestimentoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/investimentos")
public class InvestimentoController {

    private final InvestimentoService investimentoService;

    public InvestimentoController(InvestimentoService investimentoService) {
        this.investimentoService = investimentoService;
    }

    @PostMapping("/comprar")
    public ResponseEntity<String> realizarInvestimento(@Valid @RequestBody InvestimentoRequest request) {
        // Agora passamos o DTO para o service
        investimentoService.processarInvestimento(request);

        return ResponseEntity.ok("Investimento de " + request.getTipo() + " enviado para processamento!");
    }
}