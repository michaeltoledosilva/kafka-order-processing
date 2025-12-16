package com.michaeltoledo.orderapi.service;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Serviço responsável por garantir IDEMPOTÊNCIA no consumo de mensagens Kafka.
 *
 * Problema que resolve:
 * - Em Kafka, uma mesma mensagem pode ser reprocessada:
 *   - retry automático
 *   - rebalanceamento de consumer
 *   - falhas de rede
 *   - commit manual / automático
 *
 * Objetivo:
 * - Garantir que um mesmo pedido (orderId) seja processado apenas UMA vez
 */
@Service
public class ProcessedOrderService {

    /**
     * Estrutura thread-safe para armazenar pedidos já processados.
     *
     * ConcurrentHashMap.newKeySet():
     * - Não bloqueia leitura/escrita
     * - Seguro para múltiplas threads
     * - Performance muito melhor que Collections.synchronizedSet
     *
     * Observação:
     * - Funciona bem para exemplos, testes e entrevistas
     * - Em produção, normalmente isso seria persistido (Redis / DB)
     */
    private final Set<String> processedOrders = ConcurrentHashMap.newKeySet();

    /**
     * Verifica se um pedido já foi processado.
     *
     * @param orderId Identificador único do pedido
     * @return true  → já foi processado
     *         false → primeira vez que está sendo processado
     *
     * Como funciona:
     * - add() retorna FALSE se o elemento JÁ EXISTIR
     * - Invertendo (!add):
     *   - true  → duplicado
     *   - false → novo
     *
     * Esse método é atômico e thread-safe.
     */
    public boolean alreadyProcessed(String orderId) {
        return !processedOrders.add(orderId);
    }
}
