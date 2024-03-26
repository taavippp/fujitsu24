package com.taavippp.fujitsu24.model.Fee;

import com.taavippp.fujitsu24.model.Vehicle;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

/*
* Since both the regional base fee and extra fees depend on the vehicle used among other things,
* this base class exists.
* */
@MappedSuperclass
@Getter
public class BaseFee {
    short cost;
    Vehicle vehicle;
}
