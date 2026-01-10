# Wallet Pix Service - Roadmap (Vertical Slices)

## Fase 1: Fundação (Concluída)
- [x] Configuração Maven e Dependências
- [x] Infraestrutura Docker (Postgres)
- [x] Estrutura de pacotes e Application Main

## Slice 1: Gestão de Identidade
- [x] Implementar `POST /wallets` (Criação de carteira)
- [x] Implementar `POST /wallets/{id}/pix-keys` (Registro de chaves)
- [x] Camadas Clean Arch implementadas (Domain, App, Infra, Presentation)
- [x] Testes de integração da Slice 1

## Slice 2: Movimentação de Saldo (Concluída)
- [x] Implementar `POST /wallets/{id}/deposit`
- [x] Implementar `POST /wallets/{id}/withdraw`
- [x] Validar Optimistic Locking e Ledger inicial

## Slice 3: Consulta e Auditoria
- [ ] Implementar `GET /wallets/{id}/balance` (Atual)
- [ ] Implementar `GET /wallets/{id}/balance?at=...` (Histórico)

## Slice 4: Fluxo Pix e Idempotência
- [ ] Implementar `POST /pix/transfers`
- [ ] Implementar `POST /pix/webhook`
- [ ] Validar Idempotência por `eventId` e `Idempotency-Key`
