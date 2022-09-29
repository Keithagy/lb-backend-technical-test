package com.backendtestka.helpers;


/**
 * This interface is implemented by each class with the JPA @Entity annotation.
 * It contains only one method, withoutUUID(), which is a method that strips the UUID from the object to be JSON-serialized.
 */
public interface ConstructableWithoutIdentifier<T> {
    T withoutUUID();
}
