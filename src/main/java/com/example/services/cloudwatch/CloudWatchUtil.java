package com.example.services.cloudwatch;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CloudWatchUtil {
    public static final int DAY_IN_DAYS = 1;
    public static final int WEEK_IN_DAYS = 7;
    public static final int MONTH_IN_DAYS = 30;
    public static final int SEASON_IN_DAYS = 90;
    public static final int YEAR_IN_DAYS = 365;

    public static int getAppropriateTimeWindowForPeriod(final int periodInDays) {
        if (periodInDays < DAY_IN_DAYS) {
            throw new IllegalArgumentException("Period in days must be greater than or equal to " + DAY_IN_DAYS
                    + ". periodInDays: " + periodInDays);
        }

        if (periodInDays <= 2 * WEEK_IN_DAYS) {
            return DAY_IN_DAYS;
        } else if (periodInDays <= 2 * MONTH_IN_DAYS) {
            return WEEK_IN_DAYS;
        } else if (periodInDays <= 2 * SEASON_IN_DAYS) {
            return MONTH_IN_DAYS;
        } else if (periodInDays <= 2 * YEAR_IN_DAYS) {
            return SEASON_IN_DAYS;
        } else {
            return YEAR_IN_DAYS;
        }
    }
}
