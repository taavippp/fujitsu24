package com.taavippp.fujitsu24.repository;

import com.taavippp.fujitsu24.model.Fee.RegionalFee;
import com.taavippp.fujitsu24.model.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/*
 * Different RegionalFee instances are stored in the database table that this interface creates.
 * The table will be called "REGIONAL_FEE"
 * */
public interface RegionalFeeRepository extends JpaRepository<RegionalFee, Long> {
    @Query(value =
            "SELECT COST " +
            "FROM REGIONAL_FEE RF " +
            "WHERE RF.REGION = :region",
            nativeQuery = true
    )
    int findCostByRegion(@Param("region") Region region);
}
