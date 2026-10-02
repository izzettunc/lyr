package com.lyr.services.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ArnUtilTest {

    @Test
    void testThatAResourcePartOfArnStringParsedCorrectlyToResourceRecord() {
        // Given
        final var resourcePattern = "*";
        final var simpleResource = "abc";
        final var resourceWithSlash = "table/abc";
        final var resourceWithColon = "dummy:abc";

        final var expectedResourceForResourcePattern =
                ArnUtil.Resource.builder().resourceId("*").build();

        final var expectedResourceForSimpleResource =
                ArnUtil.Resource.builder().resourceId("abc").build();

        final var expectedResourceForResourceWithSlash = ArnUtil.Resource.builder()
                .resourceType("table")
                .resourceId("abc")
                .build();

        final var expectedResourceForResourceWithColon = ArnUtil.Resource.builder()
                .resourceType("dummy")
                .resourceId("abc")
                .build();

        // When

        final var actualPatternAsArn = ArnUtil.parseArnResourceSegment(resourcePattern);
        final var actualArnWithSimpleResourceAsArn = ArnUtil.parseArnResourceSegment(simpleResource);
        final var actualArnWithResourceTypeUsingSlashAsArn = ArnUtil.parseArnResourceSegment(resourceWithSlash);
        final var actualArnWithResourceTypeUsingColonAsArn = ArnUtil.parseArnResourceSegment(resourceWithColon);

        // Then

        assertThat(actualPatternAsArn).isEqualTo(expectedResourceForResourcePattern);
        assertThat(actualArnWithSimpleResourceAsArn).isEqualTo(expectedResourceForSimpleResource);
        assertThat(actualArnWithResourceTypeUsingColonAsArn).isEqualTo(expectedResourceForResourceWithColon);
        assertThat(actualArnWithResourceTypeUsingSlashAsArn).isEqualTo(expectedResourceForResourceWithSlash);
    }

    @Test
    void testThatAnArnStringParsedCorrectlyToArnRecord() {
        // Given
        final var patternArn = "arn:*:*:*:*:*";
        final var arnWithSimpleResource = "arn:123:dynamodb:eu-west-1:456:abc";

        final var expectedPatternAsArn = ArnUtil.Arn.builder()
                .partition("*")
                .service("*")
                .region("*")
                .accountId("*")
                .resource(ArnUtil.Resource.builder().resourceId("*").build())
                .build();

        final var expectedArnWithSimpleResourceAsArn = ArnUtil.Arn.builder()
                .partition("123")
                .service("dynamodb")
                .region("eu-west-1")
                .accountId("456")
                .resource(ArnUtil.Resource.builder().resourceId("abc").build())
                .build();

        // When

        final var actualPatternAsArn = ArnUtil.parseArn(patternArn);
        final var actualArnWithSimpleResourceAsArn = ArnUtil.parseArn(arnWithSimpleResource);

        // Then

        assertThat(actualPatternAsArn).isEqualTo(expectedPatternAsArn);
        assertThat(actualArnWithSimpleResourceAsArn).isEqualTo(expectedArnWithSimpleResourceAsArn);
    }

    @Test
    void testThatParseArnThrowsExceptionGivenInvalidArn() {
        // Given
        final var shortArn = "arn:*:*:*";
        final var completeButBadArn = "brn:*:*:*:*:*";
        final var emptyString = "";
        final String nullString = null;

        // When & Then
        assertThatThrownBy(() -> ArnUtil.parseArn(shortArn))
                .hasMessage("Invalid argument. Argument should follow ARN structure")
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> ArnUtil.parseArn(emptyString))
                .hasMessage("Invalid argument. Argument should follow ARN structure")
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> ArnUtil.parseArn(nullString))
                .hasMessage("Invalid argument. Argument should follow ARN structure")
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> ArnUtil.parseArn(completeButBadArn))
                .hasMessage("Invalid argument. Argument should follow ARN structure")
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @MethodSource("validPatternAndArnsWithExpectedMatchResults")
    void testThatIsMatchingWorksSuccessfully(
            final String patternArn, final String resourceArn, final boolean expectedResult) {
        // Given patternArn, resourceArn, expectedResult
        // When
        final var actualResult = ArnUtil.isMatching(patternArn, resourceArn);
        // Then
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    public static Stream<Arguments> validPatternAndArnsWithExpectedMatchResults() {
        List<String> values = List.of("partition", "service", "region", "account", "resource");

        final var allDefinedResource = "arn:partition:service:region:account:resource";

        final var allGenericSuccessCases =
                Sets.cartesianProduct(values.stream().map(v -> Set.of("*", v)).toList()).stream()
                        .map(parts -> "arn:" + String.join(":", parts))
                        .map(pattern -> Arguments.of(pattern, allDefinedResource, true));

        final var genericFailureCases = Stream.of(
                Arguments.of("arn:123:*:*:*:*", allDefinedResource, false),
                Arguments.of("arn:*:dynamodb:*:*:*", allDefinedResource, false),
                Arguments.of("arn:*:*:eu-west-1:*:*", allDefinedResource, false),
                Arguments.of("arn:*:*:*:456:*", allDefinedResource, false),
                Arguments.of("arn:*:*:*:*:abc", allDefinedResource, false));

        final var edgeCases = Stream.of(
                Arguments.of("arn:part*:*:*:*:*", allDefinedResource, true),
                Arguments.of("arn:*:ser*:*:*:*", allDefinedResource, true),
                Arguments.of("arn:*:*:reg*:*:*", allDefinedResource, true),
                Arguments.of("arn:*:*:*:acc*:*", allDefinedResource, true),
                Arguments.of("arn:*:*:*:*:res*", allDefinedResource, true),
                Arguments.of("ARN:PARTITION:SERVICE:REGION:ACCOUNT:RESOURCE", allDefinedResource, false),
                Arguments.of("arn:partition:service:region:account:resourceType/resource", allDefinedResource, false),
                Arguments.of("arn:partition:service:region:account:resourceType:resource", allDefinedResource, false),
                Arguments.of(
                        "ARN:PARTITION:SERVICE:REGION:ACCOUNT:RESOURCE",
                        "ARN:PARTITION:SERVICE:REGION:ACCOUNT:RESOURCE",
                        true),
                Arguments.of(
                        "arn:partition:service:region:account:resourceType/resource",
                        "arn:partition:service:region:account:resourceType/resource",
                        true),
                Arguments.of(
                        "arn:partition:service:region:account:resourceType:resource",
                        "arn:partition:service:region:account:resourceType:resource",
                        true),
                Arguments.of(
                        "arn:partition:service:region:account:resourceType/*",
                        "arn:partition:service:region:account:resourceType/resource",
                        true),
                Arguments.of(
                        "arn:partition:service:region:account:resourceType:*",
                        "arn:partition:service:region:account:resourceType:resource",
                        true),
                Arguments.of(allDefinedResource, "arn:partition:service:region:account:resourceType/resource", false),
                Arguments.of(allDefinedResource, "arn:partition:service:region:account:resourceType:resource", false));

        return Stream.of(allGenericSuccessCases, genericFailureCases, edgeCases).flatMap(Function.identity());
    }
}
