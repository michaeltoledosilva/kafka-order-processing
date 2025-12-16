package com.michaeltoledo.orderapi.exception;

/**
 * Exceção para falhas TEMPORÁRIAS.
 *
 * Representa erros que NÃO são de negócio e NÃO são definitivos.
 * Exemplo:
 * - Timeout em API externa
 * - Banco momentaneamente indisponível
 * - Erro de rede
 * - Circuit breaker aberto
 *
 * Quando essa exceção é lançada no Consumer:
 * - O Kafka tenta reprocessar (retry)
 * - Se estourar o número de tentativas → DLT
 *
 * Diferença importante:
 * - BusinessException → não faz retry (erro definitivo)
 * - TemporaryException → faz retry
 */
public class TemporaryException extends RuntimeException {

    public TemporaryException(String message) {
        super(message);
    }
}
