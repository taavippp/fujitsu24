package com.taavippp.fujitsu24.repository;

import com.taavippp.fujitsu24.model.Fee.RegionalFee;
import com.taavippp.fujitsu24.model.Region;
import com.taavippp.fujitsu24.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/*
 * Different RegionalFee instances are stored in the database table that this interface creates.
 * The table will be called "REGIONAL_FEE"
 * */
@Repository("regional-fee-repository")
public interface RegionalFeeRepository extends JpaRepository<RegionalFee, Long> {
    @Query(value =
            "SELECT COST " +
            "FROM REGIONAL_FEE RF " +
            "WHERE RF.REGION = :region " +
            "AND RF.VEHICLE = :vehicle",
            nativeQuery = true
    )
    int findCostByRegion(@Param("region") Region region, @Param("vehicle") Vehicle vehicle);

    @Modifying
    @Query(value =
            "UPDATE REGIONAL_FEE RF " +
            "SET RF.COST = :cost " +
            "WHERE RF.REGION = :region " +
            "AND RF.VEHICLE = :vehicle",
            nativeQuery = true
    )
    void updateCostByRegionAndVehicle(
            @Param("cost") int cost,
            @Param("region") Region region,
            @Param("vehicle") Vehicle vehicle
    );
}
