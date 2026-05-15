package com.lyr.report.model;

import static com.lyr.report.TestUtil.createImmutableListOfFindings;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;

class ExecutionTest {

    @Test
    void testThatExecutionFieldsCanNotBeModified() {
        // Given
        final var execution = Execution.builder()
                .code("code")
                .name("name")
                .findings(createImmutableListOfFindings("1", "2", "3"))
                .configuration(ImmutableMap.of("key1", "value1", "key2", "value2"))
                .build();

        // When & Then
        assertThatThrownBy(() -> execution.findings().add(Finding.byId("1")));
        assertThatThrownBy(() -> execution.configuration().put("key", "value"));
    }

    @Test
    void testThatExecutionCanBeClonedFromAnotherOne() {
        // Given
        final var expectedExecution = Execution.builder()
                .code("code")
                .name("name")
                .findings(createImmutableListOfFindings("1", "2", "3"))
                .configuration(ImmutableMap.of("key1", "value1", "key2", "value2"))
                .build();

        // When
        final var actualExecution = Execution.copyOf(expectedExecution);

        // Then
        assertThat(actualExecution).isEqualTo(expectedExecution);
        assertThat(actualExecution).isNotSameAs(expectedExecution);
    }
}
