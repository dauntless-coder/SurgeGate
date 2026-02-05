package com.surgegate.backend.exceptions;

public class TicketsSoldOutException extends EventTicketException {
    public TicketsSoldOutException(String message) { super(message); }
    public TicketsSoldOutException() { super("Tickets are sold out"); }
}