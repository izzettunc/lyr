package com.lyr.util;

import com.lyr.util.exception.rule.UnknownRuleException;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public enum RuleDefinition {
    SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT("scan.glue.session.activeWithLongIdleTimeout", "1"),

    SCAN_DYNAMODB_TABLE_IDLE("scan.dynamodb.table.idle", "2"),

    SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY("scan.lambda.function.withUnboundedConcurrency", "3");

    private static final Map<String, RuleDefinition> NAME_TO_DEFINITION_MAP = new HashMap<>();
    private static final Map<String, RuleDefinition> CODE_TO_DEFINITION_MAP = new HashMap<>();

    static {
        for (final RuleDefinition definition : values()) {
            NAME_TO_DEFINITION_MAP.put(definition.ruleName, definition);
            CODE_TO_DEFINITION_MAP.put(definition.ruleCode, definition);
        }
    }

    private final String ruleName;
    private final String ruleCode;

    RuleDefinition(final String name, final String code) {
        this.ruleName = name;
        this.ruleCode = code;
    }

    public static RuleDefinition definitionByName(final String name) {
        if (!NAME_TO_DEFINITION_MAP.containsKey(name)) {
            throw new UnknownRuleException("Given rule is not defined. Rule name: " + name);
        }

        return NAME_TO_DEFINITION_MAP.get(name);
    }

    public static RuleDefinition definitionByCode(final String code) {
        if (!CODE_TO_DEFINITION_MAP.containsKey(code)) {
            throw new UnknownRuleException("Given rule is not defined. Rule code: " + code);
        }

        return CODE_TO_DEFINITION_MAP.get(code);
    }
}
