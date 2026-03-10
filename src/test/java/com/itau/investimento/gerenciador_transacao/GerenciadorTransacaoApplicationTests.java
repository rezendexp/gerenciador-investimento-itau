package com.itau.investimento.gerenciador_transacao;

import com.itau.investimento.domain.service.InvestimentoService;
import io.awspring.cloud.sns.core.SnsTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class GerenciadorTransacaoApplicationTests {

	@MockBean
	private SnsTemplate snsTemplate;

	@Test
	void contextLoads(){
	}
}