package com.lyr.report.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class FindingTest {

    @Test
    void testThatFindingCanBeCreatedByProvidingOnlyId() {
        // Given
        final var identifier = "123";

        // When
        final var actualFinding = Finding.byId(identifier);

        // Then
        assertThat(actualFinding).isNotNull();
        assertThat(actualFinding.identifier()).isEqualTo(identifier);
        assertThat(actualFinding.reason()).isNull();
    }

    @Test
    void testThatFindingCanBeClonedFromAnotherOne() {
        // Given
        final var expectedFinding =
                Finding.builder().identifier("123").reason("reason").build();

        // When
        final var actualFinding = Finding.copyOf(expectedFinding);

        // Then
        assertThat(actualFinding).isEqualTo(expectedFinding);
        assertThat(actualFinding).isNotSameAs(expectedFinding);
    }
}
