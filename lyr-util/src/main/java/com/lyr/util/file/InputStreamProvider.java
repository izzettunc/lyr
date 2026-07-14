package com.lyr.util.file;

import java.io.IOException;
import java.io.InputStream;

@FunctionalInterface
public interface InputStreamProvider {
    InputStream getInputStream(final String path) throws IOException;
}
