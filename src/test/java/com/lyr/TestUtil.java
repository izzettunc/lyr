package com.lyr;

import com.google.common.collect.ImmutableList;
import com.lyr.rule.outcome.ScanOutcome;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse;
import software.amazon.awssdk.services.dynamodb.model.ListTablesResponse;
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
    public static final String VALUE = "value";
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
    public static final String DISABLED = "disabled";
    public static final String ENABLED = "enabled";
    public static final String ENABLING = "enabling";
    public static final String DISABLING = "disabling";
    public static final String FUNCTION_1 = "function1";
    public static final String FUNCTION_2 = "function2";
    public static final String FUNCTION_3 = "function3";
    public static final String FUNCTION_4 = "function4";
    public static final String FUNCTION_5 = "function5";
    public static final String FUNCTION_6 = "function6";
    public static final String FUNCTION_7 = "function7";
    public static final String FUNCTION_8 = "function8";
    public static final String FUNCTION_9 = "function9";
    public static final String PARAMETER_1 = "parameter1";
    public static final String PARAMETER_2 = "parameter2";
    public static final String PARAMETER_3 = "parameter3";
    public static final String PARAMETER_4 = "parameter4";
    public static final String VALUE_1 = "value1";
    public static final String VALUE_2 = "value2";
    public static final String VALUE_3 = "value3";
    public static final String VALUE_4 = "value4";

    public static ListTablesResponse createDummyListTableResponse(final String... tableNames) {
        return ListTablesResponse.builder().tableNames(tableNames).build();
    }

    public static List<ScanOutcome> createImmutableListOfScanOutcome(final String... outcome) {
        return Stream.of(outcome).map(ScanOutcome::new).collect(ImmutableList.toImmutableList());
    }

    public static Optional<DescribeTableResponse> createOptionalDescribeTableResponse(final long tableSizeBytes) {
        return Optional.of(DescribeTableResponse.builder()
                .table(TableDescription.builder().tableSizeBytes(tableSizeBytes).build())
                .build());
    }
}
