package com.lyr.report.model;

import static com.lyr.report.TestUtil.createImmutableListOfFindings;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;

class ReportTest {

    @Test
    void testThatReportFieldsCanNotBeModified() {
        // Given
        final var execution = Execution.builder()
                .code("code")
                .name("name")
                .findings(createImmutableListOfFindings("1", "2", "3"))
                .configuration(ImmutableMap.of("key1", "value1", "key2", "value2"))
                .build();
        final var secondExecution = Execution.builder()
                .code("code")
                .name("name")
                .findings(createImmutableListOfFindings("1", "2", "3"))
                .configuration(ImmutableMap.of("key1", "value1", "key2", "value2"))
                .build();

        final var report = Report.builder()
                .version("1.2.3")
                .executions(ImmutableList.of(execution, secondExecution))
                .build();

        // When & Then
        assertThatThrownBy(() -> report.executions().add(Execution.builder().build()));
    }

    @Test
    void testThatReportCanBeClonedFromAnotherOne() {
        // Given
        final var execution = Execution.builder()
                .code("code")
                .name("name")
                .findings(createImmutableListOfFindings("1", "2", "3"))
                .configuration(ImmutableMap.of("key1", "value1", "key2", "value2"))
                .build();
        final var secondExecution = Execution.builder()
                .code("code")
                .name("name")
                .findings(createImmutableListOfFindings("1", "2", "3"))
                .configuration(ImmutableMap.of("key1", "value1", "key2", "value2"))
                .build();

        final var expectedReport = Report.builder()
                .version("1.2.3")
                .executions(ImmutableList.of(execution, secondExecution))
                .build();

        // When
        final var actualReport = Report.copyOf(expectedReport);

        // Then
        assertThat(actualReport).isEqualTo(expectedReport);
        assertThat(actualReport).isNotSameAs(expectedReport);
    }
}
