package com.lyr.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ch.qos.logback.classic.Level;
import com.lyr.exception.NotYetInitializedException;
import com.lyr.report.ReportType;
import org.junit.jupiter.api.Test;

class SettingsTest {

    @Test
    void testThatWhenASettingsBuiltAllFieldsHasDefaultValues() {
        // Given
        final var expectedReportType = ReportType.PLAIN_TEXT;
        final var expectedProfile = "default";
        final var expectedLogLevel = Level.INFO;

        // When
        var actualSettings = Settings.builder().build();

        // Then
        assertThat(actualSettings).hasNoNullFieldsOrProperties();
        assertThat(actualSettings.getReportType()).isEqualTo(expectedReportType);
        assertThat(actualSettings.getProfile()).isEqualTo(expectedProfile);
        assertThat(actualSettings.getLogLevel()).isEqualTo(expectedLogLevel);
        assertThat(actualSettings.getUserRuleSetConfigPath()).isEmpty();
    }

    @Test
    void testThatAppSettingsNotYetInitializedExceptionThrownWhenAppSettingsGetRunBeforeSet() {
        // Given
        Settings.setAppSettings(null);

        // When & Then
        assertThatThrownBy(Settings::getAppSettings)
                .isInstanceOf(NotYetInitializedException.class)
                .hasMessage("App settings can not be accessed as it is not yet initialized.");
    }

    @Test
    void testThatSettingsSetIsSettingsGot() {
        // Given
        final var expectedSettings = Settings.builder().build();

        // When
        Settings.setAppSettings(expectedSettings);
        final var actualSettings = Settings.getAppSettings();

        // Then
        assertThat(actualSettings).isSameAs(expectedSettings);
    }
}
