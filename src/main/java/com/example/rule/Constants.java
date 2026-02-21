package com.example.rule;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {

    public static final String SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT =
            "scan.glue.session.activeWithLongIdleTimeout";

    public static final String SCAN_DYNAMODB_TABLE_IDLE = "scan.dynamodb.table.idle";

    public static final String SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY =
            "scan.lambda.function.withUnboundedConcurrency";

    public static final String VALIDATE_LAMBDA_FUNCTION_EXISTS = "validate.lambda.function.exists";
    public static final String VALIDATE_LAMBDA_FUNCTION_CONCURRENCY = "validate.lambda.function.concurrency";
    public static final String VALIDATE_LAMBDA_FUNCTION_TRIGGER_STATE = "validate.lambda.function.trigger.state";

    public static final String VALIDATE_SSM_PARAMETER_EXISTS = "validate.ssm.parameter.exists";
    public static final String VALIDATE_SSM_PARAMETER_VALUE = "validate.ssm.parameter.value";
}
