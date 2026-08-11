package com.lyr.cli.util;

import org.apache.commons.lang3.StringUtils;
import picocli.CommandLine;

public class VersionProvider implements CommandLine.IVersionProvider {

    private static final String SCHEMA_VERSION = "1.0.0";

    public static Package getPackage() {
        return VersionProvider.class.getPackage();
    }

    public static String getVersionFromManifest() {
        final Package pkg = getPackage();
        if (pkg != null && !StringUtils.isBlank(pkg.getImplementationVersion())) {
            return pkg.getImplementationVersion();
        } else {
            return "unknown";
        }
    }

    public static String getSchemaVersion() {
        return SCHEMA_VERSION;
    }

    @Override
    public String[] getVersion() {
        return new String[] {"Version " + getVersionFromManifest(), "Schema version " + getSchemaVersion()};
    }
}
