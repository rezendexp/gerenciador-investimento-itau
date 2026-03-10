# 🏦 Investiment Event Manager (Itaú Simulation)

Este projeto simula um sistema de processamento de eventos de investimento de alta disponibilidade, focado em padrões de **Engenharia de Software** e **SRE**.

## 🚀 Diferenciais do Projeto
- **Resiliência:** Implementação de `Spring Retry` para garantir que falhas momentâneas na AWS não percam transações.
- **Observabilidade:** Uso de `Spring Actuator` para monitoramento de saúde do sistema.
- **Qualidade:** 100% de cobertura de testes (Unitários e Integração) via `JaCoCo`.
- **Integridade:** Uso de `JPA Auditing` e controle de estados (`RECEBIDO`, `PROCESSADO`, `ERRO`).

## 🛠️ Tecnologias
- Java 21 / Spring Boot 3.5
- AWS SNS (Messaging)
- H2 Database (SQL Persistence)
- JUnit 5 / Mockito

## 🧪 Cobertura de Testes
O projeto segue a régua de qualidade do Itaú, mantendo a cobertura acima de 90%.
