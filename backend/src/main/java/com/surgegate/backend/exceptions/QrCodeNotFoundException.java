package com.surgegate.backend.exceptions;

public class QrCodeNotFoundException extends EventTicketException {
    public QrCodeNotFoundException(String message) { super(message); }
    public QrCodeNotFoundException() { super("QR Code not found"); }
}