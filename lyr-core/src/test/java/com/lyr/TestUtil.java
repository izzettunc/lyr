package com.lyr;

import com.google.common.collect.ImmutableList;
import com.lyr.report.model.Finding;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse;
import software.amazon.awssdk.services.dynamodb.model.TableDescription;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public final class TestUtil {
    public static final String DUMMY_STRING = "dummy";
    public static final String DUMMY2_STRING = "dummy2";
    public static final String UPDATED_DUMMY_STRING = "updatedDummy";
    public static final String UPDATED_DUMMY2_STRING = "updatedDummy2";
    public static final String FINAL_UPDATED_DUMMY_STRING = "finalUpdatedDummy";
    public static final String FINAL_UPDATED_DUMMY2_STRING = "finalUpdatedDummy2";
    public static final String TABLE_1 = "table1";
    public static final String TABLE_2 = "table2";
    public static final String TABLE_3 = "table3";
    public static final String TABLE_4 = "table4";
    public static final String TABLE_WITH_DATA = "tableWithData";
    public static final String TABLE_WITH_DATA_OTHER = "tableWithDataOther";
    public static final String SESSION_1 = "session1";
    public static final String SESSION_2 = "session2";
    public static final String SESSION_3 = "session3";
    public static final String SESSION_4 = "session4";
    public static final String SESSION_READY = "sessionReady";
    public static final String SESSION_PROVISIONING = "sessionProvisioning";
    public static final String SESSION_LT_MAX_IDLE_TIMEOUT = "sessionLTMaxIdleTimeout";
    public static final String FUNCTION_1 = "function1";
    public static final String FUNCTION_2 = "function2";
    public static final String FUNCTION_3 = "function3";
    public static final String FUNCTION_4 = "function4";
    public static final String LOG_GROUP_1 = "logGroup1";
    public static final String LOG_GROUP_2 = "logGroup2";
    public static final String LOG_GROUP_3 = "logGroup3";
    public static final String LOG_GROUP_4 = "logGroup4";

    public static List<Finding> createImmutableListOfFindings(final String... identifiers) {
        return Stream.of(identifiers).map(Finding::byId).collect(ImmutableList.toImmutableList());
    }

    public static Optional<DescribeTableResponse> createOptionalDescribeTableResponse(final long tableSizeBytes) {
        return Optional.of(DescribeTableResponse.builder()
                .table(TableDescription.builder().tableSizeBytes(tableSizeBytes).build())
                .build());
    }

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
