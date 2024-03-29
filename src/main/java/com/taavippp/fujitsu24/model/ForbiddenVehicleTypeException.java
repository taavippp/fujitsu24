package com.taavippp.fujitsu24.model;

public class ForbiddenVehicleTypeException extends Exception {
    @Override
    public String getMessage() {
        return "Usage of selected vehicle type is forbidden";
    }
}
