package com.example.rule.lambda;

import com.example.rule.lambda.config.ValidateLambdaFunctionTriggerStateRuleConfig;
import com.example.rule.outcome.ValidationOutcome;
import com.example.services.lambda.LambdaConnector;
import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import software.amazon.awssdk.services.lambda.model.EventSourceMappingConfiguration;
import software.amazon.awssdk.services.lambda.model.GetFunctionResponse;
import software.amazon.awssdk.services.lambda.model.ListEventSourceMappingsResponse;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

class ValidateLambdaFunctionTriggerStateRuleImplTest {

    static MockedStatic<LambdaConnector> mockedLambdaConnector = Mockito.mockStatic(LambdaConnector.class);
    static LambdaConnector mockedLambdaConnectorInstance = Mockito.mock(LambdaConnector.class);
    static ValidateLambdaFunctionTriggerStateRuleImpl testObject;

    @BeforeEach
    public void beforeEach() {
        mockedLambdaConnector.when(LambdaConnector::create).thenReturn(mockedLambdaConnectorInstance);
        testObject = new ValidateLambdaFunctionTriggerStateRuleImpl();
    }

    @AfterEach
    public void afterEach() {
        mockedLambdaConnector.reset();
        reset(mockedLambdaConnectorInstance);
    }

    @AfterAll
    public static void afterAll() {
        mockedLambdaConnector.closeOnDemand();
    }

    @Test
    void testThatValidateLambdaFunctionTriggerStateRuleReturnsInvalidWhenFunctionDoesntExists() {
        // Given
        var listOfLambdaFunctionTriggerState = List.of(
                new ValidateLambdaFunctionTriggerStateRuleConfig.LambdaFunctionTriggerState("lambda1", true),
                new ValidateLambdaFunctionTriggerStateRuleConfig.LambdaFunctionTriggerState("lambda2", false)
        );

        var config = ValidateLambdaFunctionTriggerStateRuleConfig
                .builder()
                .functionTriggerStates(listOfLambdaFunctionTriggerState)
                .build();

        var expectedResult = ImmutableList.of(
                ValidationOutcome.invalid(LambdaReason.FUNCTION_NOT_FOUND),
                ValidationOutcome.invalid(LambdaReason.FUNCTION_NOT_FOUND)
        );

        // When
        when(mockedLambdaConnectorInstance.getLambdaFunction(anyString())).thenReturn(Optional.empty());
        var actualResult = testObject.execute(config);

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedResult);
    }

    @Test
    void testThatValidateLambdaFunctionTriggerStateRuleReturnsCorrectOutcomeBasedOnWantedStateWhenNoEventSourceMappingFound() {
        // Given
        var listOfLambdaFunctionTriggerState = List.of(
                new ValidateLambdaFunctionTriggerStateRuleConfig.LambdaFunctionTriggerState("lambda1", true),
                new ValidateLambdaFunctionTriggerStateRuleConfig.LambdaFunctionTriggerState("lambda2", false)
        );

        var config = ValidateLambdaFunctionTriggerStateRuleConfig
                .builder()
                .functionTriggerStates(listOfLambdaFunctionTriggerState)
                .build();

        var optLambdaFunction = Optional.of(GetFunctionResponse.builder().build());

        var expectedResult = ImmutableList.of(
                ValidationOutcome.valid(LambdaReason.NO_EVENT_SOURCE_MAPPINGS),
                ValidationOutcome.invalid(LambdaReason.NO_EVENT_SOURCE_MAPPINGS)
        );

        // When
        when(mockedLambdaConnectorInstance.getLambdaFunction(anyString())).thenReturn(optLambdaFunction);
        when(mockedLambdaConnectorInstance.listEventSourceMappings(anyString())).thenReturn(Optional.empty());

        var actualResult = testObject.execute(config);

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedResult);
    }

    @Test
    void testThatValidateLambdaFunctionTriggerStateRuleReturnsCorrectOutcomeBasedOnWantedStateWhenAllTriggersAreEnabled() {
        // Given
        var listOfLambdaFunctionTriggerState = List.of(
                new ValidateLambdaFunctionTriggerStateRuleConfig.LambdaFunctionTriggerState("lambda1", true),
                new ValidateLambdaFunctionTriggerStateRuleConfig.LambdaFunctionTriggerState("lambda2", false)
        );

        var config = ValidateLambdaFunctionTriggerStateRuleConfig
                .builder()
                .functionTriggerStates(listOfLambdaFunctionTriggerState)
                .build();

        var optLambdaFunction = Optional.of(GetFunctionResponse.builder().build());
        var optEventMappings = Optional.of(ListEventSourceMappingsResponse.builder()
                .eventSourceMappings(
                        EventSourceMappingConfiguration.builder().state("enabled").build(),
                        EventSourceMappingConfiguration.builder().state("enabled").build(),
                        EventSourceMappingConfiguration.builder().state("enabled").build())
                .build());

        var expectedResult = ImmutableList.of(
                ValidationOutcome.valid(LambdaReason.ALL_EVENT_MAPPINGS_ARE_ENABLED),
                ValidationOutcome.invalid(LambdaReason.ALL_EVENT_MAPPINGS_ARE_ENABLED)
        );

        // When
        when(mockedLambdaConnectorInstance.getLambdaFunction(anyString())).thenReturn(optLambdaFunction);
        when(mockedLambdaConnectorInstance.listEventSourceMappings(anyString())).thenReturn(optEventMappings);

        var actualResult = testObject.execute(config);

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedResult);
    }

    @Test
    void testThatValidateLambdaFunctionTriggerStateRuleReturnsCorrectOutcomeBasedOnWantedStateWhenAllTriggersAreDisabled() {
        // Given
        var listOfLambdaFunctionTriggerState = List.of(
                new ValidateLambdaFunctionTriggerStateRuleConfig.LambdaFunctionTriggerState("lambda1", true),
                new ValidateLambdaFunctionTriggerStateRuleConfig.LambdaFunctionTriggerState("lambda2", false)
        );

        var config = ValidateLambdaFunctionTriggerStateRuleConfig
                .builder()
                .functionTriggerStates(listOfLambdaFunctionTriggerState)
                .build();

        var optLambdaFunction = Optional.of(GetFunctionResponse.builder().build());
        var optEventMappings = Optional.of(ListEventSourceMappingsResponse.builder()
                .eventSourceMappings(
                        EventSourceMappingConfiguration.builder().state("disabled").build(),
                        EventSourceMappingConfiguration.builder().state("enabling").build(),
                        EventSourceMappingConfiguration.builder().state("disabling").build())
                .build());

        var expectedResult = ImmutableList.of(
                ValidationOutcome.valid(LambdaReason.ALL_EVENT_MAPPINGS_ARE_DISABLED),
                ValidationOutcome.invalid(LambdaReason.ALL_EVENT_MAPPINGS_ARE_DISABLED)
        );

        // When
        when(mockedLambdaConnectorInstance.getLambdaFunction(anyString())).thenReturn(optLambdaFunction);
        when(mockedLambdaConnectorInstance.listEventSourceMappings(anyString())).thenReturn(optEventMappings);

        var actualResult = testObject.execute(config);

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedResult);
    }

    @Test
    void testThatValidateLambdaFunctionTriggerStateRuleReturnsCorrectOutcomeBasedOnWantedStateWhenSomeTriggersAreEnabledOrDisabled() {
        // Given
        var listOfLambdaFunctionTriggerState = List.of(
                new ValidateLambdaFunctionTriggerStateRuleConfig.LambdaFunctionTriggerState("lambda1", true),
                new ValidateLambdaFunctionTriggerStateRuleConfig.LambdaFunctionTriggerState("lambda2", false)
        );

        var config = ValidateLambdaFunctionTriggerStateRuleConfig
                .builder()
                .functionTriggerStates(listOfLambdaFunctionTriggerState)
                .build();

        var optLambdaFunction = Optional.of(GetFunctionResponse.builder().build());
        var optEventMappings = Optional.of(ListEventSourceMappingsResponse.builder()
                .eventSourceMappings(
                        EventSourceMappingConfiguration.builder().state("enabled").build(),
                        EventSourceMappingConfiguration.builder().state("disabled").build(),
                        EventSourceMappingConfiguration.builder().state("enabling").build(),
                        EventSourceMappingConfiguration.builder().state("disabling").build())
                .build());

        var expectedResult = ImmutableList.of(
                ValidationOutcome.invalid(LambdaReason.ALL_EVENT_MAPPINGS_ARE_NOT_DISABLED),
                ValidationOutcome.invalid(LambdaReason.ALL_EVENT_MAPPINGS_ARE_NOT_ENABLED)
        );

        // When
        when(mockedLambdaConnectorInstance.getLambdaFunction(anyString())).thenReturn(optLambdaFunction);
        when(mockedLambdaConnectorInstance.listEventSourceMappings(anyString())).thenReturn(optEventMappings);

        var actualResult = testObject.execute(config);

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedResult);
    }
}