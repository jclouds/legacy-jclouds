/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.predicates;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Predicate;

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
        validate(t);
        return true; // by contract, if no exception thrown
    }

    /**
     * Validates the parameter
     * @param t parameter to be validated
     * @throws IllegalArgumentException if validation failed
     */
    public abstract void validate(@Nullable T t) throws IllegalArgumentException;
}
