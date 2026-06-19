package com.toolschallenge.payments.exception;

public class DuplicateTransactionException extends RuntimeException {

    public DuplicateTransactionException(String id) {
        super("Transacao com id " + id + " ja existe");
    }
}
