package com.lyr.services.util;

import com.google.common.annotations.VisibleForTesting;
import com.lyr.util.CollectionUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ArnUtil {

    public static final String COLON_SYMBOL = ":";
    public static final String ARN_PREFIX = "arn";
    public static final String SLASH_SYMBOL = "/";
    public static final String WILDCARD_SYMBOL = "*";

    private static final int ARN_PREFIX_INDEX = 0;
    private static final int PARTITION_INDEX = 1;
    private static final int SERVICE_INDEX = 2;
    private static final int REGION_INDEX = 3;
    private static final int ACCOUNT_ID_INDEX = 4;
    private static final int RESOURCE_INDEX = 5;

    private static final int RESOURCE_TYPE_INDEX = 0;
    private static final int RESOURCE_ID_INDEX = 1;

    private static final int NUMBER_OF_ARN_PIECES = 6;
    private static final String INVALID_ARGUMENT_ARGUMENT_SHOULD_FOLLOW_ARN_STRUCTURE =
            "Invalid argument. Argument should follow ARN structure";

    public static boolean isMatching(final String patternArn, final String resourceArn) {
        final var patternArnParts = parseArn(patternArn).toParts();
        final var resourceArnParts = parseArn(resourceArn).toParts();

        return IntStream.range(0, patternArnParts.size())
                .mapToObj(index -> isPartMatching(patternArnParts.get(index), resourceArnParts.get(index)))
                .reduce(Boolean::logicalAnd)
                .orElse(false);
    }

    private static boolean isPartMatching(final String patternPart, final String resourcePart) {
        if (null == patternPart && null == resourcePart) {
            return true;
        }

        if (null == patternPart || null == resourcePart) {
            return false;
        }

        if (patternPart.equals(WILDCARD_SYMBOL)) {
            return true;
        }

        if (patternPart.endsWith(WILDCARD_SYMBOL)) {
            final var patternWithoutWildcard = patternPart.substring(0, patternPart.length() - 1);
            return resourcePart.startsWith(patternWithoutWildcard);
        }

        return patternPart.equals(resourcePart);
    }

    public static Arn parseArn(final String resourceArn) {
        if (null == resourceArn) {
            throw new IllegalArgumentException(INVALID_ARGUMENT_ARGUMENT_SHOULD_FOLLOW_ARN_STRUCTURE);
        }

        final var parts = resourceArn.split(COLON_SYMBOL, NUMBER_OF_ARN_PIECES);

        if (parts.length != NUMBER_OF_ARN_PIECES || !ARN_PREFIX.equalsIgnoreCase(parts[ARN_PREFIX_INDEX])) {
            throw new IllegalArgumentException(INVALID_ARGUMENT_ARGUMENT_SHOULD_FOLLOW_ARN_STRUCTURE);
        }

        return new Arn(
                parts[PARTITION_INDEX],
                parts[SERVICE_INDEX],
                parts[REGION_INDEX],
                parts[ACCOUNT_ID_INDEX],
                parseArnResourceSegment(parts[RESOURCE_INDEX]));
    }

    @VisibleForTesting
    static Resource parseArnResourceSegment(final String resourceSegment) {
        if (resourceSegment.contains(SLASH_SYMBOL)) {
            final var parts = resourceSegment.split(SLASH_SYMBOL);
            return new Resource(parts[RESOURCE_TYPE_INDEX], parts[RESOURCE_ID_INDEX]);
        } else if (resourceSegment.contains(COLON_SYMBOL)) {
            final var parts = resourceSegment.split(COLON_SYMBOL);
            return new Resource(parts[RESOURCE_TYPE_INDEX], parts[RESOURCE_ID_INDEX]);
        } else {
            return new Resource(null, resourceSegment);
        }
    }

    @Builder
    public record Arn(String partition, String service, String region, String accountId, Resource resource) {

        public List<String> toParts() {
            return CollectionUtil.combineAsStream(List.of(partition, service, region, accountId), resource.toParts())
                    .toList();
        }

        @Override
        public String toString() {
            final var parts = new ArrayList<>(
                    toParts().stream().filter(part -> !(null == part)).toList());
            parts.addFirst(ARN_PREFIX);
            return String.join(COLON_SYMBOL, parts);
        }
    }

    @Builder
    public record Resource(String resourceType, String resourceId) {

        public List<String> toParts() {
            return Arrays.asList(resourceType, resourceId);
        }
    }
}
