package com.example.rule.lambda;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalMatchers.or;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import com.example.rule.lambda.config.ValidateLambdaFunctionExistsRuleConfig;
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
import software.amazon.awssdk.services.lambda.model.GetFunctionResponse;

class ValidateLambdaFunctionExistsRuleImplTest {

    static MockedStatic<LambdaConnector> mockedLambdaConnector = Mockito.mockStatic(LambdaConnector.class);
    static LambdaConnector mockedLambdaConnectorInstance = Mockito.mock(LambdaConnector.class);
    static ValidateLambdaFunctionExistsRuleImpl testObject;

    @BeforeEach
    public void beforeEach() {
        mockedLambdaConnector.when(LambdaConnector::create).thenReturn(mockedLambdaConnectorInstance);
        testObject = new ValidateLambdaFunctionExistsRuleImpl();
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
    void testThatValidateLambdaFunctionExistsRuleExecutesSuccessfully() {
        // Given
        var config = ValidateLambdaFunctionExistsRuleConfig.builder()
                .functionNames(List.of("function1", "function2"))
                .build();

        var optLambdaFunction = Optional.of(GetFunctionResponse.builder().build());

        var expectedResult = ImmutableList.of(ValidationOutcome.valid(), ValidationOutcome.valid());

        // When
        when(mockedLambdaConnectorInstance.getLambdaFunction(anyString())).thenReturn(optLambdaFunction);
        var actualResult = testObject.execute(config);

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedResult);
    }

    @Test
    void testThatValidateLambdaFunctionExistsRuleReturnsInvalidWhenFunctionNotFoundWithCorrectReason() {
        // Given
        var config = ValidateLambdaFunctionExistsRuleConfig.builder()
                .functionNames(List.of("function1", "function2", "function3", "function4"))
                .build();

        var optLambdaFunction = Optional.of(GetFunctionResponse.builder().build());

        var expectedResult = ImmutableList.of(
                ValidationOutcome.valid(),
                ValidationOutcome.invalid(LambdaReason.FUNCTION_NOT_FOUND),
                ValidationOutcome.invalid(LambdaReason.FUNCTION_NOT_FOUND),
                ValidationOutcome.valid());

        // When
        when(mockedLambdaConnectorInstance.getLambdaFunction(or(eq("function1"), eq("function4"))))
                .thenReturn(optLambdaFunction);
        when(mockedLambdaConnectorInstance.getLambdaFunction(or(eq("function2"), eq("function3"))))
                .thenReturn(Optional.empty());

        var actualResult = testObject.execute(config);

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedResult);
    }
}
