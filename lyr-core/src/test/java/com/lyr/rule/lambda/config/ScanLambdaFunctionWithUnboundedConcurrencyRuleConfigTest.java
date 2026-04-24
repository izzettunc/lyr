package com.lyr.rule.lambda.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ScanLambdaFunctionWithUnboundedConcurrencyRuleConfigTest {

    @Test
    void testThatScanLambdaFunctionWithUnboundedConcurrencyRuleConfigIsParsedToNull() {
        // Given
        final Object configList = List.of(15);
        final Object configMap = Map.of("abc", 15, "def", 30);

        // When
        final var actualRuleConfigList = ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig.parse(configList);
        final var actualRuleConfigMap = ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig.parse(configMap);
        final var actualRuleConfigNull = ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig.parse(null);

        // Then
        assertThat(actualRuleConfigList).isNull();
        assertThat(actualRuleConfigMap).isNull();
        assertThat(actualRuleConfigNull).isNull();
    }
}
