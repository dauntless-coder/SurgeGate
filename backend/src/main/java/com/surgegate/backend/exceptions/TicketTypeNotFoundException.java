package com.surgegate.backend.exceptions;

public class TicketTypeNotFoundException extends EventTicketException {
    public TicketTypeNotFoundException(String message) { super(message); }
    public TicketTypeNotFoundException() { super("Ticket Type not found"); }
}