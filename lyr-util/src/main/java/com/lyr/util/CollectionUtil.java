package com.lyr.util;

import java.util.Collection;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CollectionUtil {

    @SafeVarargs
    public static <T> Stream<T> combineAsStream(final Collection<T>... collections) {
        return Stream.of(collections).flatMap(Collection::stream);
    }
}
