package com.surgegate.backend.exceptions;


import com.surgegate.backend.domain.dtos.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // --- 404 Not Found ---
    @ExceptionHandler({
            EventNotFoundException.class,
            TicketNotFoundException.class,
            UserNotFoundException.class,
            TicketTypeNotFoundException.class,
            QrCodeNotFoundException.class
    })
    public ResponseEntity<ErrorDto> handleNotFound(RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorDto("NOT_FOUND", ex.getMessage()));
    }

    // --- 409 Conflict (Sold Out) ---
    @ExceptionHandler(TicketsSoldOutException.class)
    public ResponseEntity<ErrorDto> handleSoldOut(TicketsSoldOutException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorDto("SOLD_OUT", ex.getMessage()));
    }

    // --- 500 Internal Server Error ---
    @ExceptionHandler(QrCodeGenerationException.class)
    public ResponseEntity<ErrorDto> handleQrGenerationError(QrCodeGenerationException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorDto("QR_ERROR", "Unable to generate QR Code"));
    }

    // --- 400 Bad Request (Validation/Updates) ---
    @ExceptionHandler(EventUpdateException.class)
    public ResponseEntity<ErrorDto> handleUpdateError(EventUpdateException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorDto("UPDATE_ERROR", ex.getMessage()));
    }

    // --- Generic Fallback ---
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorDto> handleRuntime(RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorDto("GENERAL_ERROR", ex.getMessage()));
    }
}