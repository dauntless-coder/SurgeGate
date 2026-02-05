package com.surgegate.backend.exceptions;

public class TicketNotFoundException extends EventTicketException {
    public TicketNotFoundException(String message) { super(message); }
    public TicketNotFoundException() { super("Ticket not found"); }
}