package com.lyr.cli.util;

import org.apache.commons.lang3.StringUtils;
import picocli.CommandLine;

public class VersionProvider implements CommandLine.IVersionProvider {

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

    @Override
    public String[] getVersion() {
        return new String[] {getVersionFromManifest()};
    }
}
