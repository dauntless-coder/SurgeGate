package com.surgegate.backend.exceptions;

public class QrCodeGenerationException extends EventTicketException {
    public QrCodeGenerationException(String message, Throwable cause) { super(message, cause); }
    public QrCodeGenerationException(String message) { super(message); }
}