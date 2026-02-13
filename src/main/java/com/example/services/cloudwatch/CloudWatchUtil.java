package com.example.services.cloudwatch;

public class CloudWatchUtil {
    private static final int DAY_IN_DAYS = 1;
    private static final int WEEK_IN_DAYS = 7;
    private static final int MONT_IN_DAYS = 30;
    private static final int SEASON_IN_DAYS = 90;
    private static final int YEAR_IN_DAYS = 365;

    public static int getAppropriateTimeWindowForPeriod(int periodInDays){
        if (periodInDays <= 2 * WEEK_IN_DAYS) {
            return DAY_IN_DAYS;
        } else if (periodInDays <= 2 * MONT_IN_DAYS) {
            return WEEK_IN_DAYS;
        } else if (periodInDays <= 2 * SEASON_IN_DAYS) {
            return MONT_IN_DAYS;
        } else if (periodInDays <= 2 * YEAR_IN_DAYS) {
            return SEASON_IN_DAYS;
        } else {
            return YEAR_IN_DAYS;
        }
    }
}
