package com.michaeltoledo.orderapi.consumer;

import com.michaeltoledo.orderapi.dto.OrderRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * Consumer responsável por ler mensagens da Dead Letter Topic.
 *
 * Uso comum:
 * - Auditoria
 * - Monitoramento
 * - Reprocessamento manual
 */
@Slf4j
@Component
public class OrderDltConsumer {

    /**
     * Consome mensagens da DLT.
     *
     * @param order payload (pode estar vazio)
     * @param exceptionMessage mensagem da exceção original
     * @param originalTopic tópico original
     * @param offset offset original
     */
    @KafkaListener(
            topics = "orders.DLT",
            groupId = "order-dlt-group"
    )
    public void consumeDlt(
            OrderRequest order,
            @Header(KafkaHeaders.DLT_EXCEPTION_MESSAGE) String exceptionMessage,
            @Header(KafkaHeaders.DLT_ORIGINAL_TOPIC) String originalTopic,
            @Header(KafkaHeaders.OFFSET) long offset
    ) {

        log.error("🔥 MENSAGEM NA DLT");
        log.error("Payload recebido (pode estar vazio): {}", order);
        log.error("Erro original: {}", exceptionMessage);
        log.error("Tópico original: {}", originalTopic);
        log.error("Offset original: {}", offset);
    }
}
