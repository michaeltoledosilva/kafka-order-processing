package com.michaeltoledo.orderapi.controller;

import com.michaeltoledo.orderapi.dto.OrderRequest;
import com.michaeltoledo.orderapi.producer.OrderProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller responsável por receber requisições HTTP relacionadas a pedidos.
 *
 * Importante:
 * - Este controller NÃO processa regras de negócio
 * - Ele apenas valida, loga e encaminha a mensagem para o Kafka
 *
 * Esse padrão desacopla a API HTTP do processamento assíncrono.
 */
@RestController
@RequestMapping("/orders")
public class OrderController {

    /**
     * Logger padrão SLF4J.
     * Usado para rastreabilidade e observabilidade.
     */
    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    /**
     * Producer responsável por publicar mensagens no Kafka.
     * O Controller não conhece detalhes do Kafka (topic, retries, headers etc).
     * Isso mantém o controller simples e testável.
     */
    private final OrderProducer producer;

    /**
     * Injeção por construtor (boa prática).
     * Facilita testes unitários e segue o princípio de imutabilidade.
     */
    public OrderController(OrderProducer producer) {
        this.producer = producer;
    }

    /**
     * Endpoint para criação de pedidos.
     *
     * Fluxo:
     * 1. Recebe o pedido via HTTP
     * 2. Gera um correlationId para rastreamento
     * 3. Publica o pedido no Kafka
     * 4. Retorna HTTP 202 (Accepted)
     *
     * Observação importante:
     * - Retornamos 202 porque o processamento é assíncrono
     * - Não garantimos que o pedido foi processado neste momento
     */
    @PostMapping
    public ResponseEntity<Void> create(@RequestBody OrderRequest request) {

        /**
         * correlationId:
         * - Usado para rastrear o pedido em logs distribuídos
         * - Permite correlacionar logs do Controller, Producer e Consumer
         */
        String correlationId = UUID.randomUUID().toString();

        log.info(
                "📦 Pedido recebido {} | correlationId={}",
                request.getOrderId(),
                correlationId
        );

        /**
         * Envia o pedido para o Kafka.
         * A responsabilidade de serialização, headers e topic
         * fica encapsulada no OrderProducer.
         */
        producer.send(request, correlationId);

        /**
         * HTTP 202 Accepted:
         * - Indica que a requisição foi aceita para processamento
         * - Não bloqueia o cliente aguardando o consumer
         */
        return ResponseEntity.accepted().build();
    }
}
