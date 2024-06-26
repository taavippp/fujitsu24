package com.taavippp.fujitsu24.model;

// This exception class is used in FeeController to ensure a constant error message.
public class ForbiddenVehicleTypeException extends Exception {
    @Override
    public String getMessage() {
        return "Usage of selected vehicle type is forbidden";
    }
}
