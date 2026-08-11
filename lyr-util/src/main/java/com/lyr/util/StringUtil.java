package com.lyr.util;

import java.util.Collection;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StringUtil {
    private static final String QUOTE_SYMBOL = "\"";
    private static final String LIST_OPENING_SYMBOL = "[";
    private static final String LIST_CLOSING_SYMBOL = "]";
    private static final String COMMA_SPACE_SYMBOL = ", ";
    private static final String SET_OPENING_SYMBOL = "{";
    private static final String SET_CLOSING_SYMBOL = "}";

    public static String collectionToString(final Collection<?> collection) {
        if (collection instanceof Set) {
            return collectionToString(collection, SET_OPENING_SYMBOL, SET_CLOSING_SYMBOL);
        }

        return collectionToString(collection, LIST_OPENING_SYMBOL, LIST_CLOSING_SYMBOL);
    }

    private static String collectionToString(
            final Collection<?> collection,
            final String collectionOpeningSymbol,
            final String collectionClosingSymbol) {
        return collection.stream()
                .map(StringUtil::objectToTypeAwareString)
                .collect(Collectors.joining(COMMA_SPACE_SYMBOL, collectionOpeningSymbol, collectionClosingSymbol));
    }

    public static String objectToTypeAwareString(final Object object) {
        return object instanceof String ? QUOTE_SYMBOL + object + QUOTE_SYMBOL : String.valueOf(object);
    }

    public static Collection<String> toLower(final Collection<String> collection) {
        return collection.stream()
                .map(string -> string.toLowerCase(Locale.ROOT))
                .collect(Collectors.toList());
    }

    public static boolean containsIgnoreCase(final Collection<String> source, final String value) {
        final var lowerCaseCollection = toLower(source);
        return lowerCaseCollection.contains(value.toLowerCase(Locale.ROOT));
    }

    public static boolean containsAllIgnoreCase(final Collection<String> source, final Collection<String> values) {
        final var lowerCaseSource = toLower(source);
        final var lowerCaseValues = toLower(values);
        return lowerCaseSource.containsAll(lowerCaseValues);
    }
}
