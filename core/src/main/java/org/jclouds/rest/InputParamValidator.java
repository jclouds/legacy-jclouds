/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.rest;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.jclouds.predicates.Validator;
import org.jclouds.rest.annotations.ParamValidators;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.Injector;

/**
 * Validates method parameters.
 *
 * Checks the {@link ParamValidators} annotation for validators. There can be method-level
 * validators that apply to all parameters, and parameter-level validators. When validation on at
 * least one parameter doesn't pass, throws {@link IllegalStateException}.
 *
 * @author Oleksiy Yarmula
 */
public class InputParamValidator {

    private final Injector injector;

    @Inject
    public InputParamValidator(Injector injector) {
        this.injector = injector;
    }

    public InputParamValidator() {
        injector = null;
    }

    /**
     * Validates that method parameters are correct, according to {@link ParamValidators}.
     *
     * @param method
     *           method with optionally set {@link ParamValidators}
     * @param args
     *           method arguments with optionally set {@link ParamValidators}
     * @see ParamValidators
     * @see Validator
     *
     * @throws IllegalStateException
     *            if validation failed
     */
    public void validateMethodParametersOrThrow(Method method, Object... args) {

        try {
            performMethodValidation(method, args);
            performParameterValidation(method.getParameterAnnotations(), args);
        } catch(IllegalArgumentException e) {
            String argsString = Iterables.toString(Arrays.asList(args));
            throw new IllegalArgumentException(String.format("Validation on '%s#%s' didn't pass for arguments: " +
                    "%s. %n Reason: %s.", method.getDeclaringClass().getName(), method.getName(), argsString,
                    e.getMessage()));
        }
    }

    /**
     * Returns if all the method parameters passed all of the method-level
     * validators or throws an {@link IllegalArgumentException}.
     * @param method
     *           method with optionally set {@link ParamValidators}. This can not be null.
     * @param args
     *           method's parameters
     */
    private void performMethodValidation(Method method, Object... args) {
        ParamValidators paramValidatorsAnnotation = checkNotNull(method).getAnnotation(
                ParamValidators.class);
        if (paramValidatorsAnnotation == null)
            return; // by contract

        List<Validator<?>> methodValidators = getValidatorsFromAnnotation(paramValidatorsAnnotation);

        runPredicatesAgainstArgs(methodValidators, args);
    }

    /**
     * Returns if all the method parameters passed all of their corresponding
     * validators or throws an {@link IllegalArgumentException}.
     *
     * @param annotations
     *           annotations for method's arguments
     * @param args
     *           arguments that correspond to the array of annotations
     */
    private void performParameterValidation(Annotation[][] annotations, Object... args) {
        for (int currentParameterIndex = 0; currentParameterIndex < annotations.length; currentParameterIndex++) {
            ParamValidators annotation = findParamValidatorsAnnotationOrReturnNull(annotations[currentParameterIndex]);
            if (annotation == null)
                continue;
            List<Validator<?>> parameterValidators = getValidatorsFromAnnotation(annotation);
            runPredicatesAgainstArgs(parameterValidators,
                    args[currentParameterIndex]);
        }
    }

    private List<Validator<?>> getValidatorsFromAnnotation(ParamValidators paramValidatorsAnnotation) {
        List<Validator<?>> validators = Lists.newArrayList();
        for (Class<? extends Validator<?>> validator : paramValidatorsAnnotation.value()) {
            validators.add(checkNotNull(injector.getInstance(validator)));
        }
        return validators;
    }

    @SuppressWarnings("unchecked")
    private void runPredicatesAgainstArgs(List<Validator<?>> predicates, Object... args) {
        for (@SuppressWarnings("rawtypes") Validator validator : predicates) {
            Iterables.all(Arrays.asList(args), validator);
        }
    }

    private ParamValidators findParamValidatorsAnnotationOrReturnNull(
            Annotation[] parameterAnnotations) {
        for (Annotation annotation : parameterAnnotations) {
            if (annotation instanceof ParamValidators)
                return (ParamValidators) annotation;
        }
        return null;
    }

}
