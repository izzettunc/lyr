package com.lyr.cli.util;

import picocli.CommandLine;

public class VersionProvider implements CommandLine.IVersionProvider {

    public static String getVersionFromManifest() {
        final Package pkg = VersionProvider.class.getPackage();
        return (pkg != null) ? pkg.getImplementationVersion() : "unknown";
    }

    @Override
    public String[] getVersion() throws Exception {
        return new String[] {getVersionFromManifest()};
    }
}
