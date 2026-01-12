# Wallet Pix Service

Microserviço de carteira digital com suporte a transferências Pix, construído com foco em **Consistência**, **Resiliência** e **Escalabilidade**.

## 🚀 Tecnologias
- **Java 17** & **Spring Boot 3.2.2**
- **PostgreSQL** (Persistência)
- **Flyway** (Migrations)
- **Micrometer & Prometheus** (Métricas)
- **Logstash** (Logs JSON estruturados)
- **Spring Retry** (Resiliência em concorrência)
- **Testcontainers** (Testes de integração reais)

## 🏗️ Arquitetura
O projeto segue os princípios da **Clean Architecture**:
- **Domain**: Entidades (Wallet, PixTransfer, LedgerEntry) e Regras de Negócio Puras.
- **Application**: Casos de Uso (SendPix, Deposit, etc.) e interfaces de repositório.
- **Infrastructure**: Implementações JPA, Repositórios e Configurações.
- **Presentation**: Controllers REST e DTOs, documentados via Swagger.

## 🛠️ Como Executar

### Pré-requisitos
- Docker & Docker Compose
- JDK 17
- Maven 3.8+

### Passo 1: Subir o Banco de Dados
```bash
docker-compose up -d
```

### Passo 2: Executar a Aplicação
```bash
mvn spring-boot:run
```
A API estará disponível em `http://localhost:8080`.
Swagger UI: `http://localhost:8080/swagger-ui.html`

## 🧪 Testes
Para executar todos os testes (Unitários, Integração e Concorrência):
```bash
mvn test
```

## 📐 Decisões de Design & Requisitos

### 1. Consistência e Concorrência
- Utilizado **Optimistic Locking** (`version`) na entidade `Wallet` para evitar o problema do "Lost Update".
- Implementado **Retry com Backoff Exponencial e Jitter** nos casos de uso de saldo para garantir que, sob alta carga, as requisições eventualmente persistam sem erro para o usuário.

### 2. Idempotência (Exactly-Once)
- Tabela `idempotency` com chave única `(scope, key)`. 
- Todas as operações mutáveis (Transferência, Webhook) consultam e registram a chave de idempotência antes de processar. No caso do Webhook, o `eventId` serve como chave única.

### 3. Rastreabilidade (Ledger)
- Toda movimentação de saldo gera uma entrada imutável na tabela `ledger_entries`. O saldo pode ser reconstruído ou consultado em qualquer ponto do tempo.

### 4. Observabilidade
- Logs formatados em **JSON** para integração com stacks ELK/Datadog.
- Métricas exportadas para **Prometheus** no endpoint `/actuator/prometheus`.

## ⚖️ Trade-offs e Premissas
- **Simulação de Bacen**: O webhook simula a resposta do sistema do Banco Central. Não há integração externa real.
- **Transferência Interna**: Neste escopo, as chaves Pix devem pertencer a carteiras dentro do próprio sistema.
- **Segurança**: Autenticação (OAuth2/JWT) não foi implementada por brevidade, focando nos desafios de concorrência e consistência.

## ⏱️ Time Tracking
- **Planejamento e Arquitetura**: 2h
- **Implementação do Core (Slices 1-3)**: 6h
- **Resiliência e Concorrência (Slice 5)**: 4h
- **Testes e Documentação**: 3h
- **Total**: ~15h distribuídas em 3 dias.
