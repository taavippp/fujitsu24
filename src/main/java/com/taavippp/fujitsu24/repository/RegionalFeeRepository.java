package com.taavippp.fujitsu24.repository;

import com.taavippp.fujitsu24.model.Fee.RegionalFee;
import org.springframework.data.jpa.repository.JpaRepository;

/*
 * Different RegionalFee instances are stored in the database table that this interface creates.
 * The table will be called "REGIONAL_FEE"
 * */
public interface RegionalFeeRepository extends JpaRepository<RegionalFee, Long> {}
