package com.example.rule.lambda.config;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ScanLambdaFunctionWithUnboundedConcurrencyRuleConfigTest {

    @Test
    void testThatScanLambdaFunctionWithUnboundedConcurrencyRuleConfigIsParsedToNull() {
        // Given
        Object configList = List.of(15);
        Object configMap = Map.of("abc", 15, "def", 30);

        // When
        var actualRuleConfigList = ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig.parse(configList);
        var actualRuleConfigMap = ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig.parse(configMap);
        var actualRuleConfigNull = ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig.parse(null);

        // Then
        assertThat(actualRuleConfigList).isNull();
        assertThat(actualRuleConfigMap).isNull();
        assertThat(actualRuleConfigNull).isNull();
    }
}