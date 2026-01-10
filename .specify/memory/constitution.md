# Wallet Pix Service Constitution

## Core Principles

### I. Clean Architecture (NON-NEGOTIABLE)
O projeto DEVE seguir Clean Architecture com camadas bem separadas:
- **Domain**: Entidades e regras de negócio puras (sem dependências externas)
- **Application**: Casos de uso e orquestração
- **Infrastructure**: Implementações de repositórios, APIs externas
- **Presentation**: Controllers e DTOs

Dependências DEVEM apontar apenas para dentro (Infrastructure → Application → Domain).

### II. Consistência e Exactly-Once (NON-NEGOTIABLE)
Sistema de missão crítica - inconsistências são INACEITÁVEIS:
- Débitos DEVEM ter efeito exactly-once
- Operações concorrentes no mesmo Pix DEVEM resultar em apenas uma execução efetiva
- Saldo NUNCA pode ficar negativo sem validação explícita
- Ledger DEVE fechar (soma de entradas = soma de saídas)

### III. Idempotência (NON-NEGOTIABLE)
Toda operação mutável DEVE ser idempotente:
- Tabela `idempotency(scope, key)` com constraint unique
- Requisições repetidas DEVEM retornar o mesmo resultado
- Webhooks duplicados DEVEM ser processados apenas uma vez
- `eventId` e `Idempotency-Key` DEVEM ser respeitados

### IV. Concorrência e Race Conditions
O sistema DEVE resistir a requisições simultâneas:
- Usar optimistic locking (version) ou pessimistic locking (SELECT FOR UPDATE)
- Duplo disparo de transferência com mesmo Idempotency-Key = um único débito
- Implementar retries curtos para conflitos de lock

### V. Rastreabilidade e Auditoria
Trilha completa de operações é OBRIGATÓRIA:
- Ledger com entradas imutáveis (+/−) vinculadas ao `endToEndId`
- Cada operação DEVE ser rastreável
- Logs DEVEM incluir `endToEndId`, `eventId`, `idempotencyKey`
- Histórico de saldo em qualquer timestamp passado

### VI. Observabilidade
Logs estruturados e métricas são OBRIGATÓRIOS:
- Logs em formato JSON estruturado
- Métricas mínimas de operações (contadores, latência)
- Correlação de requests via trace/span IDs
- Logs de erro com contexto completo

### VII. Testes (NON-NEGOTIABLE)
Código de produção exige cobertura de testes:
- Testes unitários para regras de negócio (Domain)
- Testes de integração para repositórios e APIs
- Testes de cenários de concorrência (race conditions)
- Testes de idempotência (reprocessamento)

## Stack Tecnológica

- **Linguagem**: Java 17+
- **Framework**: Spring Boot 3.x
- **Banco de Dados**: PostgreSQL
- **Build**: Maven ou Gradle
- **Containerização**: Docker + Docker Compose
- **Testes**: JUnit 5, Mockito, Testcontainers

## Máquina de Estados Pix

Estados válidos para transferências:
```
PENDING → CONFIRMED → (SETTLED)
PENDING → REJECTED
```

- Transições fora de ordem (REJECTED antes de CONFIRMED) DEVEM respeitar a máquina de estados
- Uma vez CONFIRMED ou REJECTED, estado é FINAL (exceto SETTLED após CONFIRMED)

## Requisitos de API

Headers obrigatórios para operações mutáveis:
- `Idempotency-Key: <uuid>` para transferências Pix

Respostas DEVEM incluir:
- `endToEndId` para transferências
- Status atual da operação
- Timestamps relevantes

## Governance

- Esta constituição SUPERA todas as outras práticas do projeto
- Alterações requerem documentação, aprovação e plano de migração
- Todo PR/review DEVE verificar compliance com estes princípios
- Complexidade DEVE ser justificada

**Version**: 1.0.0 | **Ratified**: 2026-01-08 | **Last Amended**: 2026-01-08
