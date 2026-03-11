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

### 🔄 Fluxo de Processamento (End-to-End)

```mermaid
sequenceDiagram
    participant C as Cliente (Swagger/API)
    participant J as Java Service (Spring)
    participant DB as SQL Database
    participant SNS as AWS SNS
    participant SQS as AWS SQS
    participant L as Lambda (Python)
    participant DY as DynamoDB

    C->>J: POST /investimentos/comprar
    J->>DB: Salva Transação (STATUS: RECEBIDO)
    
    loop Resiliência (Spring Retry)
        J->>SNS: Publica Evento de Investimento
    end

    SNS->>SQS: Distribui Mensagem (Fan-out)
    SQS->>L: Trigger de Processamento
    L->>DY: Grava Auditoria (STATUS: FINALIZADO_PYTHON)
    
    J->>DB: Atualiza Transação (STATUS: PROCESSADO)
    J-->>C: Retorna 200 OK