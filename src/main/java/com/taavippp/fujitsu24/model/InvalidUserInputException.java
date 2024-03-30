package com.taavippp.fujitsu24.model;

// This exception class is used in FeeController to ensure a constant error message.
public class InvalidUserInputException extends Exception {
    @Override
    public String getMessage() {
        return "Invalid input";
    }
}
