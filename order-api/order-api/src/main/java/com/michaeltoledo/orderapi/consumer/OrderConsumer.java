package com.michaeltoledo.orderapi.consumer;

import com.michaeltoledo.orderapi.dto.OrderRequest;
import com.michaeltoledo.orderapi.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Consumer responsável por processar pedidos.
 *
 * Qualquer exceção lançada aqui será tratada pelo ErrorHandler.
 */
@Slf4j
@Component
public class OrderConsumer {

    /**
     * Consome mensagens do tópico "orders".
     *
     * @param order pedido recebido do Kafka
     */
    @KafkaListener(
            topics = "orders",
            groupId = "order-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(OrderRequest order) {

        log.info("📦 Consumindo pedido: {}", order);

        // Regra de negócio simulada
        if (order.getQuantity() <= 0) {
            throw new BusinessException("Quantidade inválida");
        }

        // Simulação de processamento com sucesso
        log.info("✅ Pedido processado com sucesso");
    }
}
