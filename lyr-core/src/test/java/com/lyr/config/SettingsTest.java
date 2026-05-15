package com.lyr.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.lyr.exception.config.NotYetInitializedException;
import com.lyr.report.ReportType;
import org.junit.jupiter.api.Test;

class SettingsTest {

    @Test
    void testThatWhenASettingsBuiltAllFieldsHasDefaultValues() {
        // Given nothing
        // When
        var actualSettings = Settings.builder().build();

        // Then
        assertThat(actualSettings).hasNoNullFieldsOrProperties();
        assertThat(actualSettings.getReportType()).isEqualTo(ReportType.PLAIN_TEXT);
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
