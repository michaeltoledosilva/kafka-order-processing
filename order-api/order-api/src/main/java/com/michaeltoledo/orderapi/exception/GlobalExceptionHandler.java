package com.michaeltoledo.orderapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Handler global de exceções REST.
 *
 * Centraliza o tratamento de erros da API,
 * evitando try/catch espalhado pelos controllers.
 *
 * Boa prática:
 * - Controller só orquestra
 * - Validação + erro ficam aqui
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Trata erros de validação disparados pelo @Valid / Bean Validation.
     *
     * Exemplo:
     * - quantity < 1
     * - campos @NotBlank vazios
     *
     * O Spring lança automaticamente MethodArgumentNotValidException
     * antes mesmo de entrar no método do controller.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        // Mapa campo -> mensagem de erro
        Map<String, String> errors = new HashMap<>();

        // Percorre todos os erros de validação
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(
                                error.getField(),        // nome do campo
                                error.getDefaultMessage() // mensagem amigável
                        )
                );

        // Retorna HTTP 400 com os erros
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errors);
    }
}
