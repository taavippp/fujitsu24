package com.taavippp.fujitsu24.repository;

import com.taavippp.fujitsu24.model.Fee.ExtraFee;
import org.springframework.data.jpa.repository.JpaRepository;

/*
* Different ExtraFee instances are stored in the database table that this interface creates.
* The table will be called "EXTRA_FEE"
* */
public interface ExtraFeeRepository extends JpaRepository<ExtraFee, Long> {}
