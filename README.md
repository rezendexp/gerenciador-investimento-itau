# 🏦 Investment Event Manager (Itaú Simulation)

Este projeto simula um sistema de processamento de eventos de investimento de alta disponibilidade, focado em padrões rigorosos de **Engenharia de Software** e **SRE (Site Reliability Engineering)**. O objetivo é garantir que nenhuma transação financeira seja perdida, mesmo em cenários de instabilidade na nuvem.

## 🚀 Diferenciais de Engenharia

- **Resiliência Avançada:** Implementação de `Spring Retry` com política de backoff para garantir que falhas momentâneas de rede na AWS não causem perda de dados.
- **State Machine (Controle de Estados):** Rastreabilidade total da transação através de status (`RECEBIDO` -> `PROCESSADO` ou `ERRO`), garantindo integridade no banco de dados SQL.
- **Qualidade de Elite:** Cobertura de testes unitários e de integração de **100%**, validada via plugin `JaCoCo`.
- **Observabilidade:** Endpoints de monitoramento via `Spring Actuator` (health, metrics) prontos para integração com DataDog/CloudWatch.
- **Auditoria Automática:** Uso de `JPA Auditing` para registro automático de timestamps de criação.
- **Documentação Viva:** API documentada visualmente via `Swagger/OpenAPI`.

## 📐 Arquitetura e Fluxo

O sistema segue uma arquitetura em camadas (Web, Service, Domain, Infrastructure), isolando a lógica de negócio das integrações externas.

### 🔄 Fluxo de Processamento (Sequence Diagram)

```mermaid
sequenceDiagram
    participant C as Cliente (API)
    participant S as InvestimentoService
    participant DB as Banco de Dados (SQL)
    participant AWS as AWS SNS

    C->>S: Realiza Investimento (POST)
    S->>DB: Salva com status 'RECEBIDO'
    
    rect rgb(240, 240, 240)
    Note right of S: Política de Resiliência
    loop Retry Policy (3x tentativas)
        S->>AWS: Tenta enviar notificação SNS
    end
    end

    alt Sucesso no SNS
        S->>DB: Atualiza para 'PROCESSADO'
        S-->>C: Retorna 200 OK
    else Falha Definitiva (após retries)
        S->>DB: Atualiza para 'ERRO'
        S-->>C: Retorna 500 Internal Error
    end
