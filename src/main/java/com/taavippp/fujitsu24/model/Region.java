package com.taavippp.fujitsu24.model;

/*
* This enum exists to distinguish the different regions that this delivery service application works in.
* A region is represented by its weather station.
* One could simply add a new region/city by modifying this file and the InitialRegionalFees.xml file.
* */
public enum Region {
    TALLINN("Tallinn-Harku", 26038),
    TARTU("Tartu-Tõravere", 26242),
    PARNU("Pärnu", 41803);

    public final String station;
    public final int wmoCode;

    Region(String station, int wmoCode) {
        this.station = station;
        this.wmoCode = wmoCode;
    }
}
