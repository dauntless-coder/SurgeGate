package com.surgegate.backend.exceptions;

public class UserNotFoundException extends EventTicketException {
    public UserNotFoundException(String message) { super(message); }
    public UserNotFoundException() { super("User not found"); }
}