package com.example.services.cloudwatch;


import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CloudWatchUtilTest {

    @ParameterizedTest
    @MethodSource("getValidPeriods")
    void testThatGetAppropriateTimeWindowForPeriodReturnAppropriateTimeWindow(int periodInDays, int expectedAppropriateTimeWindow) {
        // Given periodInDays and expectedAppropriateTimeWindow
        // When
        var actualAppropriateTimeWindow = CloudWatchUtil.getAppropriateTimeWindowForPeriod(periodInDays);

        // Then
        assertThat(actualAppropriateTimeWindow).isEqualTo(expectedAppropriateTimeWindow);
    }

    @ParameterizedTest
    @MethodSource("getInvalidPeriods")
    void testThatGetAppropriateTimeWindowForPeriodThrowsIllegalArgumentExceptionGivenInvalidPeriodInDays(int periodInDays) {
        // Given periodInDays
        // When & Then
        assertThatThrownBy(() -> CloudWatchUtil.getAppropriateTimeWindowForPeriod(periodInDays))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Period in days must be greater than or equal to "
                        + CloudWatchUtil.DAY_IN_DAYS + ". periodInDays: " + periodInDays);
    }

    public static Stream<Arguments> getInvalidPeriods() {
        return Stream.of(
                Arguments.of(-1),
                Arguments.of(0));
    }

    public static Stream<Arguments> getValidPeriods() {
        return Stream.of(
                Arguments.of(10, CloudWatchUtil.DAY_IN_DAYS),
                Arguments.of(2 * CloudWatchUtil.WEEK_IN_DAYS, CloudWatchUtil.DAY_IN_DAYS),
                Arguments.of(2 * CloudWatchUtil.WEEK_IN_DAYS + 6, CloudWatchUtil.WEEK_IN_DAYS),
                Arguments.of(2 * CloudWatchUtil.MONTH_IN_DAYS, CloudWatchUtil.WEEK_IN_DAYS),
                Arguments.of(2 * CloudWatchUtil.MONTH_IN_DAYS + 6, CloudWatchUtil.MONTH_IN_DAYS),
                Arguments.of(2 * CloudWatchUtil.SEASON_IN_DAYS, CloudWatchUtil.MONTH_IN_DAYS),
                Arguments.of(2 * CloudWatchUtil.SEASON_IN_DAYS + 6, CloudWatchUtil.SEASON_IN_DAYS),
                Arguments.of(2 * CloudWatchUtil.YEAR_IN_DAYS, CloudWatchUtil.SEASON_IN_DAYS),
                Arguments.of(2 * CloudWatchUtil.YEAR_IN_DAYS + 6, CloudWatchUtil.YEAR_IN_DAYS),
                Arguments.of(10 * CloudWatchUtil.YEAR_IN_DAYS, CloudWatchUtil.YEAR_IN_DAYS));
    }
}