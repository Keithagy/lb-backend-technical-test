package com.backendtestka.helpers;

import org.springframework.lang.Nullable;

/**
 * Every non-GET HTTP method returns this class.
 * Encapsulates API call success status with error messsage and additional data as required.
 * E.g. with successful POST/PUT/DELETE calls, data will contain the properties of the created/mutated/deleted entity.
 */
public class Confirmation<T> {
    final public boolean success;
    final public String message;
    @Nullable
    final public T data;

    public Confirmation(boolean success, String message, @Nullable T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static <T> Confirmation<T> successful(String message, @Nullable T data) {
        return new Confirmation<>(true, message, data);
    }

    public static <T> Confirmation<T> failure(String message, @Nullable T data) {
        return new Confirmation<>(false, message, data);
    }

}
