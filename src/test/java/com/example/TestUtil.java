package com.example;

import com.example.rule.outcome.ScanOutcome;
import com.google.common.collect.ImmutableList;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse;
import software.amazon.awssdk.services.dynamodb.model.ListTablesResponse;
import software.amazon.awssdk.services.dynamodb.model.TableDescription;

import java.util.Optional;
import java.util.stream.Stream;

public class TestUtil {

    public static ListTablesResponse createDummyListTableResponse(String... tableNames) {
        return ListTablesResponse.builder().tableNames(tableNames).build();
    }

    public static ImmutableList<ScanOutcome> createImmutableListOfScanOutcome(String... outcome) {
        return Stream.of(outcome)
                .map(ScanOutcome::new)
                .collect(ImmutableList.toImmutableList());
    }

    public static Optional<DescribeTableResponse> createOptionalDescribeTableResponse(long tableSizeBytes) {
        return Optional.of(DescribeTableResponse.builder()
                .table(TableDescription.builder()
                        .tableSizeBytes(tableSizeBytes).build()
                ).build());
    }
}
