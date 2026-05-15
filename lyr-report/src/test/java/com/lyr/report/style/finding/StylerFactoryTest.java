package com.lyr.report.style.finding;

import static org.assertj.core.api.Assertions.assertThat;

import com.lyr.report.style.finding.dynamodb.ScanDynamodbTableIdleRuleFindingStyler;
import com.lyr.report.style.finding.glue.ScanGlueSessionActiveWithLongIdleTimeoutRuleFindingStyler;
import com.lyr.report.style.finding.lambda.ScanLambdaFunctionWithUnboundedConcurrencyRuleFindingStyler;
import com.lyr.util.RuleDefinition;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class StylerFactoryTest {

    @ParameterizedTest
    @MethodSource("validRuleDefinitionsAndExpectedStylerClasses")
    void testThatCorrectStylerCreatedSuccessfullyForGivenRuleDefinition(
            final RuleDefinition ruleDefinition, final Class<?> expectedStylerClass) {
        // Given ruleDefinition and expectedStylerClass

        // When
        final var actualStyler = StylerFactory.getStylerFor(ruleDefinition);

        // Then
        assertThat(actualStyler).isInstanceOf(expectedStylerClass);
    }

    public static Stream<Arguments> validRuleDefinitionsAndExpectedStylerClasses() {
        return Stream.of(
                Arguments.of(RuleDefinition.SCAN_DYNAMODB_TABLE_IDLE, ScanDynamodbTableIdleRuleFindingStyler.class),
                Arguments.of(
                        RuleDefinition.SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY,
                        ScanLambdaFunctionWithUnboundedConcurrencyRuleFindingStyler.class),
                Arguments.of(
                        RuleDefinition.SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT,
                        ScanGlueSessionActiveWithLongIdleTimeoutRuleFindingStyler.class));
    }
}
