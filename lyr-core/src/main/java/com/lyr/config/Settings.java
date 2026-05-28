package com.lyr.config;

import ch.qos.logback.classic.Level;
import com.lyr.exception.NotYetInitializedException;
import com.lyr.report.ReportType;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Settings {
    private static Settings appSettings;

    @Builder.Default
    final Optional<String> userRuleSetConfigPath = Optional.empty();

    @Builder.Default
    final ReportType reportType = ReportType.PLAIN_TEXT;

    @Builder.Default
    final Level logLevel = Level.INFO;

    public static Settings getAppSettings() {
        if (appSettings == null) {
            throw new NotYetInitializedException("App settings can not be accessed as it is not yet initialized.");
        }

        return appSettings;
    }

    public static void setAppSettings(final Settings newAppSettings) {
        Settings.appSettings = newAppSettings;
    }
}
