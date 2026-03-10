package com.itau.investimento.domain.repository;

import com.itau.investimento.domain.model.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface InvestimentoRepository extends JpaRepository<Transacao, UUID> {
}