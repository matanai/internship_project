package com.booking.consumer.service;

public class TransactionException extends RuntimeException
{
    public TransactionException(String message) {
        super(message);
    }
}
