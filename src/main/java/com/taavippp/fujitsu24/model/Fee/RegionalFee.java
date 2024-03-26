package com.taavippp.fujitsu24.model.Fee;

import com.taavippp.fujitsu24.model.Region;
import com.taavippp.fujitsu24.model.Vehicle;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jdom2.Element;

/*
* This class exists for modelling the base regional fees.
* The different regions are Tallinn, Tartu and Pärnu.
* The enum Region exists to distinguish different regions,
*   however, the member names are capitalized and no dotted letters are permitted.
* */
@Entity
@Getter @NoArgsConstructor
public class RegionalFee extends BaseFee {
    private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
    private Region region;

    /*
     * This constructor exists to parse the initial fees from the .xml files in the resources folder.
     * */
    public RegionalFee(Element xmlRoot) {
        this.cost = Short.parseShort(xmlRoot.getChildTextTrim("cost"));
        this.region = Region.valueOf(xmlRoot.getChildTextTrim("region"));
        this.vehicle = Vehicle.valueOf(xmlRoot.getChildTextTrim("vehicle"));
    }
}
