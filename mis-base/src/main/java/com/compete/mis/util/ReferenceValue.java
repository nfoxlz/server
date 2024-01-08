package com.compete.mis.util;

public final class ReferenceValue<T> {

    private T data;

    public T get() {
        return data;
    }

    public void set(T data) {
        this.data = data;
    }
}
