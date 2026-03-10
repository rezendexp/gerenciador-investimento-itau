package com.itau.investimento.domain.model;

import com.itau.investimento.api.dto.InvestimentoRequest;
import jakarta.persistence.*;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "tb_investimentos")
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(org.springframework.data.jpa.domain.support.AuditingEntityListener.class)
public class Transacao {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String clienteId;
    private Double valor;
    private String tipo;

    @CreatedDate
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Enumerated(EnumType.STRING)
    private StatusTransacao status;

    public Transacao(InvestimentoRequest request) {
        this.clienteId = request.getClienteId();
        this.valor = request.getValor();
        this.tipo = request.getTipo();
    }
}