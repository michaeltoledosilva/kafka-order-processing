package com.michaeltoledo.orderapi.producer;

import com.michaeltoledo.orderapi.dto.OrderRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderProducer {

    /**
     * KafkaTemplate:
     * - Serializa automaticamente o payload em JSON
     * - Envia mensagens de forma assíncrona
     */
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Tópico principal de pedidos
     */
    private static final String TOPIC = "orders";

    /**
     * Envia o pedido para o Kafka
     *
     * @param order Pedido recebido via API
     * @param correlationId ID único para rastrear a mensagem
     */
    public void send(OrderRequest order, String correlationId) {

        /**
         * Construção da mensagem Kafka.
         *
         * IMPORTANTE:
         * - __TypeId__ informa ao consumer qual classe usar na desserialização
         * - correlationId permite rastrear Controller → Producer → Consumer → DLT
         */
        Message<OrderRequest> message = MessageBuilder
                .withPayload(order)
                .setHeader(KafkaHeaders.TOPIC, TOPIC)
                .setHeader("__TypeId__", OrderRequest.class.getName())
                .setHeader("correlationId", correlationId)
                .build();

        /**
         * Envio assíncrono para o Kafka
         */
        kafkaTemplate.send(message);

        log.info(
                "📤 Pedido enviado para Kafka | topic={} | orderId={} | correlationId={}",
                TOPIC,
                order.getOrderId(),
                correlationId
        );
    }
}
