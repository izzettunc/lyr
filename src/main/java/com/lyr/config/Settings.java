package com.lyr.config;

import com.lyr.exception.config.AppSettingsNotYetInitializedException;
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
    final ReportType reportType = ReportType.CONSOLE;

    public static Settings getAppSettings() {
        if (appSettings == null) {
            throw new AppSettingsNotYetInitializedException(
                    "App settings can not be accessed as it is not yet initialized.");
        }

        return appSettings;
    }

    public static void setAppSettings(final Settings newAppSettings) {
        Settings.appSettings = newAppSettings;
    }
}
