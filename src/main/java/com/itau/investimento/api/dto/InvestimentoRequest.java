package com.itau.investimento.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class InvestimentoRequest {

    @NotBlank(message = "O ID do cliente é obrigatório")
    private String clienteId;

    @NotNull(message = "O valor não pode ser nulo")
    @Min(value = 1, message = "O valor mínimo de investimento é R$ 1,00")
    private Double valor;

    @NotBlank(message = "O tipo de investimento é obrigatório")
    @Pattern(regexp = "COMPRA_ACOES|RESGATE_FUNDO|COMPRA_CDB",
            message = "Tipo de investimento inválido. Use: COMPRA_ACOES, RESGATE_FUNDO ou COMPRA_CDB")
    private String tipo;
}