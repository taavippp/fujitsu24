package com.taavippp.fujitsu24.model;

/*
Different fees can be differentiated by their prefix acronym:
ATEF - Air Temperature Extra Fee (different cost for COLD or VERY_COLD)
WSEF - Wind Speed Extra Fee (different cost for FAST or VERY_FAST)
WPEF - Weather Phenomenon Extra Fee (different cost depending on SNOW_SLEET, RAIN or GLAZE_HAIL_THUNDER)
* */
public enum ExtraFeeCategory {
    ATEF_COLD,
    ATEF_VERY_COLD,
    WSEF_FAST,
    WSEF_VERY_FAST,
    WPEF_SNOW_SLEET,
    WPEF_RAIN,
    WPEF_GLAZE_HAIL_THUNDER
}
