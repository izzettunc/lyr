package com.lyr.report.json;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.lyr.report.TestUtil;
import com.lyr.report.io.OutputStrategy;
import com.lyr.report.model.Execution;
import com.lyr.report.model.Finding;
import com.lyr.util.RuleDefinition;
import com.networknt.schema.InputFormat;
import com.networknt.schema.SchemaRegistry;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class JsonReporterTest {

    JsonReporter testObject;
    OutputStrategy mockedOutputStrategy = mock(OutputStrategy.class);

    @BeforeEach
    void beforeEach() {
        testObject = new JsonReporter(mockedOutputStrategy);
        reset(mockedOutputStrategy);
    }

    @Test
    void testThatJsonReporterPrintsReport() {
        // Given
        final var expectedJsonString = """
                {
                  "version" : "0.0.0",
                  "executions" : [ {
                    "name" : "scan.dynamodb.table.idle",
                    "code" : "AWS-DDB-001",
                    "configuration" : {
                      "a" : "1",
                      "b" : 2,
                      "c" : true,
                      "d" : [ "123", 456, false ]
                    },
                    "findings" : [ {
                      "identifier" : "123"
                    }, {
                      "identifier" : "456"
                    }, {
                      "identifier" : "789",
                      "reason" : "dummy"
                    } ]
                  }, {
                    "name" : "scan.lambda.function.withUnboundedConcurrency",
                    "code" : "AWS-LMD-001",
                    "configuration" : { },
                    "findings" : [ ]
                  } ]
                }""";

        final var executions = List.of(
                Execution.builder()
                        .name(RuleDefinition.SCAN_DYNAMODB_TABLE_IDLE.getRuleName())
                        .code(RuleDefinition.SCAN_DYNAMODB_TABLE_IDLE.getRuleCode())
                        .configuration(ImmutableMap.of("a", "1", "b", 2, "c", true, "d", List.of("123", 456, false)))
                        .findings(ImmutableList.of(
                                Finding.byId("123"),
                                Finding.byId("456"),
                                Finding.builder()
                                        .identifier("789")
                                        .reason("dummy")
                                        .build()))
                        .build(),
                Execution.builder()
                        .name(RuleDefinition.SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY.getRuleName())
                        .code(RuleDefinition.SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY.getRuleCode())
                        .configuration(ImmutableMap.of())
                        .findings(ImmutableList.of())
                        .build());

        // When
        testObject.report(executions);

        // Then
        verify(mockedOutputStrategy, times(1)).write(expectedJsonString);
    }

    @Test
    @SneakyThrows
    void testThatJsonReporterFollowsTheSchema() {
        // Given
        try (final var schemaInputStream = TestUtil.getInputStreamFromResource("lyr-json-report-schema-1.0.0.json")) {
            final var schemaRegistry = SchemaRegistry.builder().build();
            final var schema = schemaRegistry.getSchema(schemaInputStream);

            final var executions = List.of(
                    Execution.builder()
                            .name(RuleDefinition.SCAN_DYNAMODB_TABLE_IDLE.getRuleName())
                            .code(RuleDefinition.SCAN_DYNAMODB_TABLE_IDLE.getRuleCode())
                            .configuration(
                                    ImmutableMap.of("a", "1", "b", 2, "c", true, "d", List.of("123", 456, false)))
                            .findings(ImmutableList.of(
                                    Finding.byId("123"),
                                    Finding.byId("456"),
                                    Finding.builder()
                                            .identifier("789")
                                            .reason("dummy")
                                            .build()))
                            .build(),
                    Execution.builder()
                            .name(RuleDefinition.SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY.getRuleName())
                            .code(RuleDefinition.SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY.getRuleCode())
                            .configuration(ImmutableMap.of())
                            .findings(ImmutableList.of())
                            .build());

            ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);

            // When
            testObject.report(executions);

            verify(mockedOutputStrategy).write(argumentCaptor.capture());

            final var writtenJsonString = argumentCaptor.getValue();

            // Then
            final var actualErrors = schema.validate(writtenJsonString, InputFormat.JSON);

            assertThat(actualErrors).hasSize(0);
        }
    }
}
