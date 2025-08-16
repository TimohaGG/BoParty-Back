package com.bezkoder.springjwt.security;

import com.bezkoder.springjwt.security.Exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@ControllerAdvice
public class ExceptionHandlerController {

    private static final Logger logger = Logger.getLogger(ExceptionHandlerController.class.getName());

    @ExceptionHandler({UserRegistrationException.class})
    public ResponseEntity<ExceptionMessage> handleException(UserRegistrationException e) {
        ExceptionMessage msg = new ExceptionMessage(HttpStatus.BAD_REQUEST,e.getMessage());
        logger.log(Level.SEVERE, e.getMessage());
        return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({NoContentException.class})
    public ResponseEntity<ExceptionMessage> handleException(NoContentException e) {
        ExceptionMessage msg = new ExceptionMessage(HttpStatus.NO_CONTENT,e.getMessage());
        logger.log(Level.SEVERE, e.getMessage());
        return new ResponseEntity<ExceptionMessage>(msg, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({UserNotFoundException.class})
    public ResponseEntity<ExceptionMessage> handleException(UserNotFoundException e) {
        ExceptionMessage msg = new ExceptionMessage(HttpStatus.NOT_FOUND,e.getMessage());
        logger.log(Level.SEVERE, e.getMessage());
        return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({CategoryNotFoundException.class})
    public ResponseEntity<ExceptionMessage> handleException(CategoryNotFoundException e) {
        ExceptionMessage msg = new ExceptionMessage(HttpStatus.NOT_FOUND,e.getMessage());
        logger.log(Level.SEVERE, e.getMessage());
        return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({IngredientCreationException.class})
    public ResponseEntity<ExceptionMessage> handleException(IngredientCreationException e) {
        ExceptionMessage msg = new ExceptionMessage(HttpStatus.BAD_REQUEST,e.getMessage());
        logger.log(Level.SEVERE, e.getMessage());
        return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({CategoryDeleteException.class})
    public ResponseEntity<ExceptionMessage> handleException(CategoryDeleteException e) {
        ExceptionMessage msg = new ExceptionMessage(HttpStatus.BAD_REQUEST,e.getMessage());
        logger.log(Level.SEVERE, e.getMessage());
        return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({IngredientDeleteException.class})
    public ResponseEntity<ExceptionMessage> handleException(IngredientDeleteException e) {
        ExceptionMessage msg = new ExceptionMessage(HttpStatus.BAD_REQUEST,e.getMessage());
        logger.log(Level.SEVERE, e.getMessage());
        return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({PositionCreateException.class})
    public ResponseEntity<ExceptionMessage> handleException(PositionCreateException e) {
        ExceptionMessage msg = new ExceptionMessage(HttpStatus.BAD_REQUEST,e.getMessage());
        logger.log(Level.SEVERE, e.getMessage());
        return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({PositionDeleteException.class})
    public ResponseEntity<ExceptionMessage> handleException(PositionDeleteException e) {
        ExceptionMessage msg = new ExceptionMessage(HttpStatus.BAD_REQUEST,e.getMessage());
        logger.log(Level.SEVERE, e.getMessage());
        return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({OrderCreateException.class})
    public ResponseEntity<ExceptionMessage> handleException(OrderCreateException e) {
        ExceptionMessage msg = new ExceptionMessage(HttpStatus.BAD_REQUEST,e.getMessage());
        logger.log(Level.SEVERE, e.getMessage());
        return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
    }


}
