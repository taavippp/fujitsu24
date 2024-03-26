package com.taavippp.fujitsu24.model.Fee;

import com.taavippp.fujitsu24.model.ExtraFeeCategory;
import com.taavippp.fujitsu24.model.Vehicle;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jdom2.Element;

/*
* This class is used for modelling any sort of extra fee that goes to the DB.
* Extra fees include:
* ATEF (air temperature)
* WSEF (wind speed)
* WPEF (weather phenomenon)
* The enum ExtraFeeCategory exists to distinguish the different extra fees.
* */
@Entity
@Getter @NoArgsConstructor
public class ExtraFee extends BaseFee {
    private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
    private ExtraFeeCategory category;

    /*
    * This constructor exists to parse the initial fees from the .xml files in the resources folder.
    * */
    public ExtraFee(Element xmlRoot) {
        this.cost = Short.parseShort(xmlRoot.getChildTextTrim("cost"));
        this.category = ExtraFeeCategory.valueOf(xmlRoot.getChildTextTrim("category"));
        this.vehicle = Vehicle.valueOf(xmlRoot.getChildTextTrim("vehicle"));
    }
}
