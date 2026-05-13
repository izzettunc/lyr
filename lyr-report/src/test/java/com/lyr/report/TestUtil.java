package com.lyr.report;

import com.google.common.collect.ImmutableList;
import com.lyr.report.model.Finding;
import java.io.InputStream;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public final class TestUtil {
    public static final String DUMMY_STRING = "dummy";
    public static final String DUMMY2_STRING = "dummy2";

    public static ImmutableList<Finding> createImmutableListOfFindings(final String... identifier) {
        return Stream.of(identifier).map(Finding::byId).collect(ImmutableList.toImmutableList());
    }

    public static InputStream getInputStreamFromResource(final String filePath) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
    }
}
