# Kafka Order Processing API

Projeto de estudo e demonstração de arquitetura assíncrona utilizando **Spring Boot + Apache Kafka**, com foco em boas práticas usadas no mercado.

## 🚀 Tecnologias
- Java 17
- Spring Boot 3
- Spring Kafka
- Apache Kafka (Docker)
- Gradle
- Docker / Docker Compose

## 📦 Fluxo da Aplicação

1. API REST recebe um pedido (`POST /orders`)
2. Producer publica o evento no tópico Kafka `orders`
3. Consumer processa o pedido
4. Em caso de erro:
   - Retry automático
   - Envio para Dead Letter Topic (`orders.DLT`)
5. Consumer específico processa mensagens da DLT

## 🔄 Estratégias Implementadas

- Serialização JSON com controle de tipo (`__TypeId__`)
- Idempotência de consumo
- Retry com backoff
- Dead Letter Topic (DLT)
- Correlação de mensagens (`correlationId`)
- Tratamento de erros técnicos vs erros de negócio
- Logs para observabilidade

## ▶️ Como executar

```bash
docker-compose up -d
./gradlew bootRun
