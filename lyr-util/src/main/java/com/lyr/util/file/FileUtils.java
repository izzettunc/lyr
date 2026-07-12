package com.lyr.util.file;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUtils {

    public static InputStream getInputStreamFromResource(final String filePath) throws FileNotFoundException {
        final var resourceAsStream =
                Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);

        if (resourceAsStream == null) {
            throw new FileNotFoundException("Resource not found: " + filePath);
        }

        return resourceAsStream;
    }

    public static InputStream getInputStreamFromSystem(final String filePath) throws IOException {
        var path = Paths.get(filePath);

        if (!path.isAbsolute()) {
            path = path.toAbsolutePath();
        }

        return Files.newInputStream(path);
    }
}
