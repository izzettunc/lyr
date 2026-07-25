package com.lyr.rule.lambda;

import static com.lyr.TestUtil.FUNCTION_1;
import static com.lyr.TestUtil.FUNCTION_2;
import static com.lyr.TestUtil.FUNCTION_3;
import static com.lyr.TestUtil.FUNCTION_4;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import com.lyr.report.model.Finding;
import com.lyr.rule.lambda.config.ScanLambdaFunctionWithoutAnyActiveTriggerRuleConfig;
import com.lyr.services.lambda.LambdaConnector;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import software.amazon.awssdk.services.lambda.model.EventSourceMappingConfiguration;
import software.amazon.awssdk.services.lambda.model.FunctionConfiguration;

class ScanLambdaFunctionWithoutAnyActiveTriggerRuleExecutionTest {

    static MockedStatic<LambdaConnector> mockedLambdaConnector = Mockito.mockStatic(LambdaConnector.class);
    static LambdaConnector mockedLambdaConnectorInstance = Mockito.mock(LambdaConnector.class);
    static ScanLambdaFunctionWithoutAnyActiveTriggerRuleExecution testObject;

    @BeforeEach
    public void beforeEach() {
        mockedLambdaConnector.when(LambdaConnector::create).thenReturn(mockedLambdaConnectorInstance);
        testObject = new ScanLambdaFunctionWithoutAnyActiveTriggerRuleExecution();
    }

    @AfterEach
    void afterEach() {
        mockedLambdaConnector.reset();
        reset(mockedLambdaConnectorInstance);
    }

    @AfterAll
    static void afterAll() {
        mockedLambdaConnector.closeOnDemand();
    }

    @Test
    void testThatRuleExecutesSuccessfully() {
        // Given
        final var config =
                ScanLambdaFunctionWithoutAnyActiveTriggerRuleConfig.builder().build();

        final var listOfFunctionConfigurations = List.of(
                createFunctionConfiguration(FUNCTION_1),
                createFunctionConfiguration(FUNCTION_2),
                createFunctionConfiguration(FUNCTION_3),
                createFunctionConfiguration(FUNCTION_4));

        final var listOfEventSourceMappingConfigurations = createEventSourceMappingConfiguration(List.of());

        final var expectedResult = Stream.of(FUNCTION_1, FUNCTION_2, FUNCTION_3, FUNCTION_4)
                .map(Finding::byId)
                .collect(ImmutableList.toImmutableList());

        // When
        when(mockedLambdaConnectorInstance.listLambdaFunctionConfigurations()).thenReturn(listOfFunctionConfigurations);
        when(mockedLambdaConnectorInstance.listEventSourceMappings(anyString()))
                .thenReturn(listOfEventSourceMappingConfigurations);
        final var actualResult = testObject.execute(config);

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedResult);
    }

    @Test
    void testThatRuleReturnsOnlyFunctionsWithoutAnyActiveTrigger() {
        // Given
        final var config = ScanLambdaFunctionWithoutAnyActiveTriggerRuleConfig.builder()
                .triggerStatesConsideredAsActive(List.of("active", "enabled", "ready"))
                .build();

        final var listOfFunctionConfigurations = List.of(
                createFunctionConfiguration(FUNCTION_1),
                createFunctionConfiguration(FUNCTION_2),
                createFunctionConfiguration(FUNCTION_3),
                createFunctionConfiguration(FUNCTION_4));

        final var function1AllValidMappings = createEventSourceMappingConfiguration(List.of("active", "enabled"));
        final var function2PartiallyValidMappings =
                createEventSourceMappingConfiguration(List.of("active", "disabled"));
        final var function3AllInvalidMappings =
                createEventSourceMappingConfiguration(List.of("inactive", "disabled", "random"));
        final var function4EmptyMappings = createEventSourceMappingConfiguration(List.of());

        final var expectedResult =
                Stream.of(FUNCTION_3, FUNCTION_4).map(Finding::byId).collect(ImmutableList.toImmutableList());

        // When
        when(mockedLambdaConnectorInstance.listLambdaFunctionConfigurations()).thenReturn(listOfFunctionConfigurations);
        when(mockedLambdaConnectorInstance.listEventSourceMappings(eq(FUNCTION_1)))
                .thenReturn(function1AllValidMappings);
        when(mockedLambdaConnectorInstance.listEventSourceMappings(eq(FUNCTION_2)))
                .thenReturn(function2PartiallyValidMappings);
        when(mockedLambdaConnectorInstance.listEventSourceMappings(eq(FUNCTION_3)))
                .thenReturn(function3AllInvalidMappings);
        when(mockedLambdaConnectorInstance.listEventSourceMappings(eq(FUNCTION_4)))
                .thenReturn(function4EmptyMappings);

        final var actualResult = testObject.execute(config);

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedResult);
    }

    @Test
    void testThatRuleReturnsEmptyListWhenNoLambdaFunctionsArePresent() {
        // Given
        final var config =
                ScanLambdaFunctionWithoutAnyActiveTriggerRuleConfig.builder().build();

        final List<FunctionConfiguration> listOfFunctionConfigurations = List.of();

        final var expectedResult = ImmutableList.of();

        // When
        when(mockedLambdaConnectorInstance.listLambdaFunctionConfigurations()).thenReturn(listOfFunctionConfigurations);
        final var actualResult = testObject.execute(config);

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedResult);
    }

    @Test
    void testThatRuleReturnsCorrectResultByIgnoringTheCasingOfStates() {
        // Given
        final var config = ScanLambdaFunctionWithoutAnyActiveTriggerRuleConfig.builder()
                .triggerStatesConsideredAsActive(List.of("Active", "enabled", "reaDy"))
                .build();

        final var listOfFunctionConfigurations = List.of(
                createFunctionConfiguration(FUNCTION_1),
                createFunctionConfiguration(FUNCTION_2),
                createFunctionConfiguration(FUNCTION_3),
                createFunctionConfiguration(FUNCTION_4));

        final var function1AllLowerCase = createEventSourceMappingConfiguration(List.of("active", "enabled"));
        final var function2RandomAndUpperCase = createEventSourceMappingConfiguration(List.of("ENABLED", "rEaDy"));
        final var function3AllUpperCase = createEventSourceMappingConfiguration(List.of("ACTIVE"));
        final var function4EmptyMappings = createEventSourceMappingConfiguration(List.of());

        final var expectedResult = ImmutableList.of(Finding.byId(FUNCTION_4));

        // When
        when(mockedLambdaConnectorInstance.listLambdaFunctionConfigurations()).thenReturn(listOfFunctionConfigurations);
        when(mockedLambdaConnectorInstance.listEventSourceMappings(eq(FUNCTION_1)))
                .thenReturn(function1AllLowerCase);
        when(mockedLambdaConnectorInstance.listEventSourceMappings(eq(FUNCTION_2)))
                .thenReturn(function2RandomAndUpperCase);
        when(mockedLambdaConnectorInstance.listEventSourceMappings(eq(FUNCTION_3)))
                .thenReturn(function3AllUpperCase);
        when(mockedLambdaConnectorInstance.listEventSourceMappings(eq(FUNCTION_4)))
                .thenReturn(function4EmptyMappings);

        final var actualResult = testObject.execute(config);

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedResult);
    }

    private static FunctionConfiguration createFunctionConfiguration(final String functionName) {
        return FunctionConfiguration.builder().functionName(functionName).build();
    }

    private static List<EventSourceMappingConfiguration> createEventSourceMappingConfiguration(
            final List<String> states) {
        final List<EventSourceMappingConfiguration> eventSourceMappingConfigurations = new ArrayList<>();
        for (String state : states) {
            eventSourceMappingConfigurations.add(
                    EventSourceMappingConfiguration.builder().state(state).build());
        }
        return eventSourceMappingConfigurations;
    }
}
