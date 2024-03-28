package com.taavippp.fujitsu24.repository;

import com.taavippp.fujitsu24.model.ExtraFeeCategory;
import com.taavippp.fujitsu24.model.Fee.ExtraFee;
import com.taavippp.fujitsu24.model.Fee.RegionalFee;
import com.taavippp.fujitsu24.model.Region;
import com.taavippp.fujitsu24.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/*
* Different ExtraFee instances are stored in the database table that this interface creates.
* The table will be called "EXTRA_FEE"
* */
public interface ExtraFeeRepository extends JpaRepository<ExtraFee, Long> {
    @Query(value =
            "SELECT COST " +
            "FROM EXTRA_FEE EF " +
            "WHERE EF.CATEGORY = :category " +
            "AND EF.VEHICLE = :vehicle",
            nativeQuery = true
    )
    int findCostByCategoryAndVehicle(@Param("category") ExtraFeeCategory category, @Param("vehicle") Vehicle vehicle);
}
