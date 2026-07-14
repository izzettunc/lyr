package com.lyr.util;

import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public final class TestUtil {

    public static String getAbsoluteFilePathOfResource(final String filePath) {
        var fileUrl = Thread.currentThread().getContextClassLoader().getResource(filePath);

        Path path;
        try {
            path = Paths.get(fileUrl.toURI());
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }

        if (!path.isAbsolute()) {
            path = path.toAbsolutePath();
        }

        return path.toString();
    }
}
