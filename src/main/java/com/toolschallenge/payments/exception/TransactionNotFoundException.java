package com.toolschallenge.payments.exception;

public class TransactionNotFoundException extends RuntimeException {

    public TransactionNotFoundException(String id) {
        super("Transacao nao encontrada para id " + id);
    }
}
