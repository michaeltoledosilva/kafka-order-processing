package com.michaeltoledo.orderapi.exception;

/**
 * Exceção de negócio.
 *
 * Representa erros esperados pela regra de negócio,
 * e NÃO erros técnicos (infra, serialização, timeout, etc).
 *
 * Exemplos:
 * - Quantidade inválida
 * - Pedido duplicado
 * - Produto indisponível
 *
 * Importante:
 * - Estende RuntimeException para NÃO forçar try/catch
 * - Permite integração direta com retry, DLT e errorHandler do Kafka
 */
public class BusinessException extends RuntimeException {

    /**
     * Construtor simples recebendo apenas a mensagem.
     *
     * A mensagem:
     * - Vai para logs
     * - Vai para headers da DLT
     * - Ajuda observabilidade e troubleshooting
     */
    public BusinessException(String message) {
        super(message);
    }
}
