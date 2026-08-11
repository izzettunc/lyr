package com.lyr.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class StringUtilTest {

    @Test
    void testThatCollectionToStringConvertsListCorrectly() {
        // Given
        final List<Object> collection = new ArrayList<>(List.of(123, "abc", true));
        final var object = new Object();
        collection.add(object);
        collection.add(null);

        final var expectedString = "[123, \"abc\", true, " + object + ", null]";

        // When
        final var actualString = StringUtil.collectionToString(collection);

        // Then
        assertThat(actualString).isEqualTo(expectedString);
    }

    @Test
    void testThatCollectionToStringConvertsEmptyListCorrectly() {
        // Given
        final var expectedString = "[]";

        // When
        final var actualString = StringUtil.collectionToString(List.of());

        // Then
        assertThat(actualString).isEqualTo(expectedString);
    }

    @Test
    void testThatCollectionToStringConvertsSetCorrectly() {
        // Given
        final Set<Object> collection = new LinkedHashSet<>(List.of(123, "abc", true));
        final var object = new Object();
        collection.add(object);
        collection.add(null);

        final var expectedString = "{123, \"abc\", true, " + object + ", null}";

        // When
        final var actualString = StringUtil.collectionToString(collection);

        // Then
        assertThat(actualString).isEqualTo(expectedString);
    }

    @Test
    void testThatCollectionToStringConvertsEmptySetCorrectly() {
        // Given
        final var expectedString = "{}";

        // When
        final var actualString = StringUtil.collectionToString(Set.of());

        // Then
        assertThat(actualString).isEqualTo(expectedString);
    }

    @Test
    void testThatToLowerConvertsCollectionOfStringsCorrectly() {
        // Given
        final var strings = List.of("UPPER", "lower", "camelCase", "PascalCase", "RaNDoM");
        final var expectedLowerCaseStrings = List.of("upper", "lower", "camelcase", "pascalcase", "random");

        // When
        final var actualLowerCaseStrings = StringUtil.toLower(strings);

        // Then
        assertThat(actualLowerCaseStrings).usingRecursiveComparison().isEqualTo(expectedLowerCaseStrings);
    }

    @Test
    void testThatContainsIgnoreCaseChecksCorrectly() {
        // Given
        final var source = List.of("UPPER", "lower", "camelCase", "PascalCase", "RaNDoM");
        final var valueThatExists = "UppER";
        final var valueThatDoesNotExist = "middle";

        // When
        final var resultForExist = StringUtil.containsIgnoreCase(source, valueThatExists);
        final var resultForDoesNotExist = StringUtil.containsIgnoreCase(source, valueThatDoesNotExist);

        // Then
        assertThat(resultForExist).isTrue();
        assertThat(resultForDoesNotExist).isFalse();
    }

    @Test
    void testThatContainsIgnoreCaseChecksEmptyListCorrectly() {
        // Given
        final var source = new ArrayList<String>();
        final var valueThatDoesNotExist = "middle";

        // When
        final var resultForDoesNotExist = StringUtil.containsIgnoreCase(source, valueThatDoesNotExist);

        // Then
        assertThat(resultForDoesNotExist).isFalse();
    }

    @Test
    void testThatContainsAllIgnoreCaseChecksCorrectly() {
        // Given
        final var source = List.of("UPPER", "lower", "camelCase", "PascalCase", "RaNDoM");
        final var valuesThatAllExist = List.of("uPPer", "loWeR", "cAmeLCaSe");
        final var valuesThatAllDoesNotExist = List.of("uPPer", "loWeR", "cAmeLCaSe", "middle");
        final var noValues = new ArrayList<String>();

        // When
        final var resultForAllExist = StringUtil.containsAllIgnoreCase(source, valuesThatAllExist);
        final var resultForAllDoesNotExist = StringUtil.containsAllIgnoreCase(source, valuesThatAllDoesNotExist);
        final var resultForNoValues = StringUtil.containsAllIgnoreCase(noValues, valuesThatAllExist);

        // Then
        assertThat(resultForAllExist).isTrue();
        assertThat(resultForAllDoesNotExist).isFalse();
        assertThat(resultForNoValues).isFalse();
    }

    @Test
    void testThatContainsAllIgnoreCaseChecksEmptyListCorrectly() {
        // Given
        final var source = new ArrayList<String>();
        final var valuesThatAllExist = new ArrayList<String>();
        final var valuesThatAllDoesNotExist = List.of("middle");

        // When
        final var resultForAllExist = StringUtil.containsAllIgnoreCase(source, valuesThatAllExist);
        final var resultForAllDoesNotExist = StringUtil.containsAllIgnoreCase(source, valuesThatAllDoesNotExist);

        // Then
        assertThat(resultForAllExist).isTrue();
        assertThat(resultForAllDoesNotExist).isFalse();
    }

    @Test
    void testThatObjectToTypeAwareStringConvertsCorrectly() {
        final var number = 123;
        final var expectedNumberAsTypeAwareString = "123";
        final var text = "123";
        final var expectedTextAsTypeAwareString = "\"123\"";
        final var bool = true;
        final var expectedBoolAsTypeAwareString = "true";
        final String nullAsString = null;
        final Object nullAsObject = null;
        final var expectedNullAsTypeAwareString = "null";

        final var actualNumberAsTypeAwareString = StringUtil.objectToTypeAwareString(number);
        final var actualTextAsTypeAwareString = StringUtil.objectToTypeAwareString(text);
        final var actualBoolAsTypeAwareString = StringUtil.objectToTypeAwareString(bool);
        final var actualNullAsStringAsTypeAwareString = StringUtil.objectToTypeAwareString(nullAsString);
        final var actualNullAsObjectAsTypeAwareString = StringUtil.objectToTypeAwareString(nullAsObject);

        assertThat(actualNumberAsTypeAwareString).isEqualTo(expectedNumberAsTypeAwareString);
        assertThat(actualTextAsTypeAwareString).isEqualTo(expectedTextAsTypeAwareString);
        assertThat(actualBoolAsTypeAwareString).isEqualTo(expectedBoolAsTypeAwareString);
        assertThat(actualNullAsStringAsTypeAwareString).isEqualTo(expectedNullAsTypeAwareString);
        assertThat(actualNullAsObjectAsTypeAwareString).isEqualTo(expectedNullAsTypeAwareString);
    }
}
