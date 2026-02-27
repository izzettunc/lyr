package com.example.rule.lambda;

import static com.example.TestUtil.DISABLED;
import static com.example.TestUtil.DISABLING;
import static com.example.TestUtil.ENABLED;
import static com.example.TestUtil.ENABLING;
import static com.example.TestUtil.FUNCTION_1;
import static com.example.TestUtil.FUNCTION_2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import com.example.rule.lambda.config.ValidateLambdaFunctionTriggerStateRuleConfig;
import com.example.rule.outcome.ValidationOutcome;
import com.example.services.lambda.LambdaConnector;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import software.amazon.awssdk.services.lambda.model.EventSourceMappingConfiguration;
import software.amazon.awssdk.services.lambda.model.GetFunctionResponse;
import software.amazon.awssdk.services.lambda.model.ListEventSourceMappingsResponse;

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
        final var listOfLambdaFunctionTriggerState = List.of(
                new ValidateLambdaFunctionTriggerStateRuleConfig.LambdaFunctionTriggerState(FUNCTION_1, true),
                new ValidateLambdaFunctionTriggerStateRuleConfig.LambdaFunctionTriggerState(FUNCTION_2, false));

        final var config = ValidateLambdaFunctionTriggerStateRuleConfig.builder()
                .functionTriggerStates(listOfLambdaFunctionTriggerState)
                .build();

        final var expectedResult = ImmutableList.of(
                ValidationOutcome.invalid(LambdaReason.FUNCTION_NOT_FOUND),
                ValidationOutcome.invalid(LambdaReason.FUNCTION_NOT_FOUND));

        // When
        when(mockedLambdaConnectorInstance.getLambdaFunction(anyString())).thenReturn(Optional.empty());
        final var actualResult = testObject.execute(config);

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedResult);
    }

    @Test
    void
            testThatValidateLambdaFunctionTriggerStateRuleReturnsCorrectOutcomeBasedOnWantedStateWhenNoEventSourceMappingFound() {
        // Given
        final var listOfLambdaFunctionTriggerState = List.of(
                new ValidateLambdaFunctionTriggerStateRuleConfig.LambdaFunctionTriggerState(FUNCTION_1, true),
                new ValidateLambdaFunctionTriggerStateRuleConfig.LambdaFunctionTriggerState(FUNCTION_2, false));

        final var config = ValidateLambdaFunctionTriggerStateRuleConfig.builder()
                .functionTriggerStates(listOfLambdaFunctionTriggerState)
                .build();

        final var optLambdaFunction = Optional.of(GetFunctionResponse.builder().build());

        final var expectedResult = ImmutableList.of(
                ValidationOutcome.valid(LambdaReason.NO_EVENT_SOURCE_MAPPINGS),
                ValidationOutcome.invalid(LambdaReason.NO_EVENT_SOURCE_MAPPINGS));

        // When
        when(mockedLambdaConnectorInstance.getLambdaFunction(anyString())).thenReturn(optLambdaFunction);
        when(mockedLambdaConnectorInstance.listEventSourceMappings(anyString())).thenReturn(Optional.empty());

        final var actualResult = testObject.execute(config);

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedResult);
    }

    @Test
    void
            testThatValidateLambdaFunctionTriggerStateRuleReturnsCorrectOutcomeBasedOnWantedStateWhenAllTriggersAreEnabled() {
        // Given
        final var listOfLambdaFunctionTriggerState = List.of(
                new ValidateLambdaFunctionTriggerStateRuleConfig.LambdaFunctionTriggerState(FUNCTION_1, true),
                new ValidateLambdaFunctionTriggerStateRuleConfig.LambdaFunctionTriggerState(FUNCTION_2, false));

        final var config = ValidateLambdaFunctionTriggerStateRuleConfig.builder()
                .functionTriggerStates(listOfLambdaFunctionTriggerState)
                .build();

        final var optLambdaFunction = Optional.of(GetFunctionResponse.builder().build());
        final var optEventMappings = Optional.of(ListEventSourceMappingsResponse.builder()
                .eventSourceMappings(
                        EventSourceMappingConfiguration.builder().state(ENABLED).build(),
                        EventSourceMappingConfiguration.builder().state(ENABLED).build(),
                        EventSourceMappingConfiguration.builder().state(ENABLED).build())
                .build());

        final var expectedResult = ImmutableList.of(
                ValidationOutcome.valid(LambdaReason.ALL_EVENT_MAPPINGS_ARE_ENABLED),
                ValidationOutcome.invalid(LambdaReason.ALL_EVENT_MAPPINGS_ARE_ENABLED));

        // When
        when(mockedLambdaConnectorInstance.getLambdaFunction(anyString())).thenReturn(optLambdaFunction);
        when(mockedLambdaConnectorInstance.listEventSourceMappings(anyString())).thenReturn(optEventMappings);

        final var actualResult = testObject.execute(config);

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedResult);
    }

    @Test
    void
            testThatValidateLambdaFunctionTriggerStateRuleReturnsCorrectOutcomeBasedOnWantedStateWhenAllTriggersAreDisabled() {
        // Given
        final var listOfLambdaFunctionTriggerState = List.of(
                new ValidateLambdaFunctionTriggerStateRuleConfig.LambdaFunctionTriggerState(FUNCTION_1, true),
                new ValidateLambdaFunctionTriggerStateRuleConfig.LambdaFunctionTriggerState(FUNCTION_2, false));

        final var config = ValidateLambdaFunctionTriggerStateRuleConfig.builder()
                .functionTriggerStates(listOfLambdaFunctionTriggerState)
                .build();

        final var optLambdaFunction = Optional.of(GetFunctionResponse.builder().build());
        final var optEventMappings = Optional.of(ListEventSourceMappingsResponse.builder()
                .eventSourceMappings(
                        EventSourceMappingConfiguration.builder()
                                .state(DISABLED)
                                .build(),
                        EventSourceMappingConfiguration.builder()
                                .state(ENABLING)
                                .build(),
                        EventSourceMappingConfiguration.builder()
                                .state(DISABLING)
                                .build())
                .build());

        final var expectedResult = ImmutableList.of(
                ValidationOutcome.valid(LambdaReason.ALL_EVENT_MAPPINGS_ARE_DISABLED),
                ValidationOutcome.invalid(LambdaReason.ALL_EVENT_MAPPINGS_ARE_DISABLED));

        // When
        when(mockedLambdaConnectorInstance.getLambdaFunction(anyString())).thenReturn(optLambdaFunction);
        when(mockedLambdaConnectorInstance.listEventSourceMappings(anyString())).thenReturn(optEventMappings);

        final var actualResult = testObject.execute(config);

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedResult);
    }

    @Test
    void
            testThatValidateLambdaFunctionTriggerStateRuleReturnsCorrectOutcomeBasedOnWantedStateWhenSomeTriggersAreEnabledOrDisabled() {
        // Given
        final var listOfLambdaFunctionTriggerState = List.of(
                new ValidateLambdaFunctionTriggerStateRuleConfig.LambdaFunctionTriggerState(FUNCTION_1, true),
                new ValidateLambdaFunctionTriggerStateRuleConfig.LambdaFunctionTriggerState(FUNCTION_2, false));

        final var config = ValidateLambdaFunctionTriggerStateRuleConfig.builder()
                .functionTriggerStates(listOfLambdaFunctionTriggerState)
                .build();

        final var optLambdaFunction = Optional.of(GetFunctionResponse.builder().build());
        final var optEventMappings = Optional.of(ListEventSourceMappingsResponse.builder()
                .eventSourceMappings(
                        EventSourceMappingConfiguration.builder().state(ENABLED).build(),
                        EventSourceMappingConfiguration.builder()
                                .state(DISABLED)
                                .build(),
                        EventSourceMappingConfiguration.builder()
                                .state(ENABLING)
                                .build(),
                        EventSourceMappingConfiguration.builder()
                                .state(DISABLING)
                                .build())
                .build());

        final var expectedResult = ImmutableList.of(
                ValidationOutcome.invalid(LambdaReason.ALL_EVENT_MAPPINGS_ARE_NOT_DISABLED),
                ValidationOutcome.invalid(LambdaReason.ALL_EVENT_MAPPINGS_ARE_NOT_ENABLED));

        // When
        when(mockedLambdaConnectorInstance.getLambdaFunction(anyString())).thenReturn(optLambdaFunction);
        when(mockedLambdaConnectorInstance.listEventSourceMappings(anyString())).thenReturn(optEventMappings);

        final var actualResult = testObject.execute(config);

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedResult);
    }
}
