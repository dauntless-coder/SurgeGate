package com.surgegate.backend.exceptions;

public class EventNotFoundException extends EventTicketException {
    public EventNotFoundException(String message) { super(message); }
    public EventNotFoundException() { super("Event not found"); }
}