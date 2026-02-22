package com.example.services.cloudwatch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class CloudWatchUtilTest {

    @ParameterizedTest
    @MethodSource("getValidPeriods")
    void testThatGetAppropriateTimeWindowForPeriodReturnAppropriateTimeWindow(
            final int periodInDays, final int expectedAppropriateTimeWindow) {
        // Given periodInDays and expectedAppropriateTimeWindow
        // When
        final var actualAppropriateTimeWindow = CloudWatchUtil.getAppropriateTimeWindowForPeriod(periodInDays);

        // Then
        assertThat(actualAppropriateTimeWindow).isEqualTo(expectedAppropriateTimeWindow);
    }

    @ParameterizedTest
    @MethodSource("getInvalidPeriods")
    void testThatGetAppropriateTimeWindowForPeriodThrowsIllegalArgumentExceptionGivenInvalidPeriodInDays(
            final int periodInDays) {
        // Given periodInDays
        // When & Then
        assertThatThrownBy(() -> CloudWatchUtil.getAppropriateTimeWindowForPeriod(periodInDays))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Period in days must be greater than or equal to " + CloudWatchUtil.DAY_IN_DAYS
                        + ". periodInDays: " + periodInDays);
    }

    public static Stream<Arguments> getInvalidPeriods() {
        return Stream.of(Arguments.of(-1), Arguments.of(0));
    }

    public static Stream<Arguments> getValidPeriods() {
        final var tenDays = 10;
        final var tenYearsMultiplier = 10;
        final var extraSixDays = 6;
        return Stream.of(
                Arguments.of(tenDays, CloudWatchUtil.DAY_IN_DAYS),
                Arguments.of(2 * CloudWatchUtil.WEEK_IN_DAYS, CloudWatchUtil.DAY_IN_DAYS),
                Arguments.of(2 * CloudWatchUtil.WEEK_IN_DAYS + extraSixDays, CloudWatchUtil.WEEK_IN_DAYS),
                Arguments.of(2 * CloudWatchUtil.MONTH_IN_DAYS, CloudWatchUtil.WEEK_IN_DAYS),
                Arguments.of(2 * CloudWatchUtil.MONTH_IN_DAYS + extraSixDays, CloudWatchUtil.MONTH_IN_DAYS),
                Arguments.of(2 * CloudWatchUtil.SEASON_IN_DAYS, CloudWatchUtil.MONTH_IN_DAYS),
                Arguments.of(2 * CloudWatchUtil.SEASON_IN_DAYS + extraSixDays, CloudWatchUtil.SEASON_IN_DAYS),
                Arguments.of(2 * CloudWatchUtil.YEAR_IN_DAYS, CloudWatchUtil.SEASON_IN_DAYS),
                Arguments.of(2 * CloudWatchUtil.YEAR_IN_DAYS + extraSixDays, CloudWatchUtil.YEAR_IN_DAYS),
                Arguments.of(tenYearsMultiplier * CloudWatchUtil.YEAR_IN_DAYS, CloudWatchUtil.YEAR_IN_DAYS));
    }
}
