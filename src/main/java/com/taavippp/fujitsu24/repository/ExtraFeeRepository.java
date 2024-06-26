package com.taavippp.fujitsu24.repository;

import com.taavippp.fujitsu24.model.ExtraFeeCategory;
import com.taavippp.fujitsu24.model.Fee.ExtraFee;
import com.taavippp.fujitsu24.model.Vehicle;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/*
* Different ExtraFee instances are stored in the database table that this interface creates.
* The table will be called "EXTRA_FEE"
* */
@Repository("extra-fee-repository")
public interface ExtraFeeRepository extends JpaRepository<ExtraFee, Long> {
    // Returns the fee from the database depending on the fee category and vehicle chosen.
    @Query(value =
            "SELECT COST " +
            "FROM EXTRA_FEE EF " +
            "WHERE EF.CATEGORY = :category " +
            "AND EF.VEHICLE = :vehicle",
            nativeQuery = true
    )
    int findCostByCategoryAndVehicle(@Param("category") ExtraFeeCategory category, @Param("vehicle") Vehicle vehicle);

    // Updates an extra fee. Cost cannot be below 0.
    @Modifying @Transactional
    @Query(value =
            "UPDATE EXTRA_FEE EF " +
            "SET EF.COST = :cost " +
            "WHERE EF.CATEGORY = :category " +
            "AND EF.VEHICLE = :vehicle",
            nativeQuery = true
    )
    void updateCostByCategoryAndVehicle(
            @Param("cost") int cost,
            @Param("category") ExtraFeeCategory category,
            @Param("vehicle") Vehicle vehicle
    );
}
