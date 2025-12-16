package com.michaeltoledo.orderapi.config;

import com.michaeltoledo.orderapi.exception.BusinessException;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuração central dos consumidores Kafka.
 *
 * Aqui definimos:
 * - Deserialização segura (ErrorHandlingDeserializer)
 * - Estratégia de retry
 * - Envio automático para Dead Letter Topic (DLT)
 * - Exceções que NÃO devem ser reprocessadas
 */
@Configuration
public class KafkaConsumerConfig {

    /**
     * Cria o ConsumerFactory.
     *
     * Responsável por:
     * - Definir bootstrap server
     * - Configurar deserializadores
     * - Garantir que erros de deserialização NÃO derrubem o consumer
     */
    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {

        Map<String, Object> props = new HashMap<>();

        // Endereço do broker Kafka
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "13.58.13.178:9092");

        // Grupo padrão de consumidores
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "order-group");

        // Commit automático desligado (controle fino de processamento)
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        // Deserializador de chave
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        // Deserializador de valor com tratamento de erro
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);

        // Delegação para JsonDeserializer
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);

        // Pacotes confiáveis para desserialização
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.michaeltoledo.orderapi.dto");

        // Evita dependência de headers de tipo
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);

        // Tipo padrão do payload
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "com.michaeltoledo.orderapi.dto.OrderRequest");

        return new DefaultKafkaConsumerFactory<>(props);
    }

    /**
     * ContainerFactory usada pelos @KafkaListener.
     *
     * Aqui conectamos:
     * - ConsumerFactory
     * - ErrorHandler
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
            ConsumerFactory<String, Object> consumerFactory,
            DefaultErrorHandler errorHandler
    ) {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory);

        // Aplica a política de erro (retry + DLT)
        factory.setCommonErrorHandler(errorHandler);

        return factory;
    }

    /**
     * Estratégia global de tratamento de erro.
     *
     * Fluxo:
     * 1. Erro acontece no consumer
     * 2. Retry automático (3 tentativas)
     * 3. Se continuar falhando → envia para DLT
     *
     * BusinessException:
     * - NÃO deve ter retry
     * - Vai direto para DLT
     */
    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<Object, Object> kafkaTemplate) {

        // Responsável por publicar a mensagem na Dead Letter Topic
        DeadLetterPublishingRecoverer recoverer =
                new DeadLetterPublishingRecoverer(kafkaTemplate);

        // Backoff fixo: 3 tentativas com intervalo de 2 segundos
        FixedBackOff backOff = new FixedBackOff(2000L, 3);

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, backOff);

        // Exceções de negócio NÃO devem ser reprocessadas
        errorHandler.addNotRetryableExceptions(BusinessException.class);

        return errorHandler;
    }
}
