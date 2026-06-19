package com.toolschallenge.payments.exception;

public class InvalidRefundException extends RuntimeException {

    public InvalidRefundException(String id) {
        super("Transacao com id " + id + " ja esta cancelada");
    }
}
