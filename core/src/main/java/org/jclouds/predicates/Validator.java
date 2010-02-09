package org.jclouds.predicates;

import com.google.common.base.Predicate;

import javax.annotation.Nullable;

/**
 * Abstract class that creates a bridge between {@link com.google.common.base.Predicate}
 * and {@link org.jclouds.rest.annotations.ParamValidators}s.
 *
 * @param <T> Type of object to be validated. For generic
 * validation (where object's class is determined in {@link #validate(Object)},
 * use {@link Object}.
 *
 * @see com.google.common.base.Predicate
 *
 * @author Oleksiy Yarmula
 */
public abstract class Validator<T> implements Predicate<T> {

    @Override
    public boolean apply(@Nullable T t) {
        try {
            validate(t);
            return true; // by contract
        } catch(IllegalArgumentException iae) {
            return false; // by contract
        }
    }

    /**
     * Validates the parameter
     * @param t parameter to be validated
     * @throws IllegalArgumentException if validation failed
     */
    public abstract void validate(@Nullable T t) throws IllegalArgumentException;
}
