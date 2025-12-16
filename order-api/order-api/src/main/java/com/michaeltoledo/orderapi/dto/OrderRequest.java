package com.michaeltoledo.orderapi.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO (Data Transfer Object) que representa um pedido recebido via API
 * e enviado para o Kafka.
 *
 * Responsabilidades:
 * - Representar o payload do evento
 * - Garantir validações básicas de entrada
 *
 * Importante:
 * - DTOs NÃO contêm lógica de negócio
 * - Apenas estrutura + validação
 */
public class OrderRequest {

    /**
     * Identificador do pedido.
     *
     * @NotBlank:
     * - Não pode ser null
     * - Não pode ser vazio
     * - Não pode conter apenas espaços
     *
     * Essa validação acontece ANTES de enviar para o Kafka,
     * evitando poluir o tópico com dados inválidos.
     */
    @NotBlank
    private String orderId;

    /**
     * Nome do produto.
     *
     * Também validado na borda da aplicação (controller),
     * garantindo dados minimamente consistentes.
     */
    @NotBlank
    private String product;

    /**
     * Quantidade do produto.
     *
     * @Min(1):
     * - Garante que valores <= 0 sejam rejeitados
     * - Evita regras defensivas repetidas no consumer
     */
    @Min(1)
    private int quantity;

    /* ======================
       Getters e Setters
       ====================== */

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * toString sobrescrito para:
     * - Logs legíveis
     * - Debug
     * - Observabilidade
     *
     * Muito útil ao consumir mensagens Kafka.
     */
    @Override
    public String toString() {
        return "OrderRequest{" +
                "orderId='" + orderId + '\'' +
                ", product='" + product + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
