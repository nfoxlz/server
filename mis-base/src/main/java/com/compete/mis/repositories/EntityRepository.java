package com.compete.mis.repositories;

public interface EntityRepository {
    <T> T get(final long id, Class<T> elementType);

    <T> T get(final String code, Class<T> elementType);

    int delete(final long id);

    int delete(final String code);
}
