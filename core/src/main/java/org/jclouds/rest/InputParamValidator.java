/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
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

      if (!passesMethodValidation(method, args)
               || !passesParameterValidation(method.getParameterAnnotations(), args)) {

         String argsString = Iterables.toString(Arrays.asList(args));
         throw new IllegalArgumentException(String.format(
                  "Validation on '%s#%s' didn't pass for arguments: " + "%s", method
                           .getDeclaringClass().getName(), method.getName(), argsString));
      }
   }

   /**
    * Returns true if all the method parameters passed all of the method-level validators.
    * 
    * @param method
    *           method with optionally set {@link ParamValidators}. This can not be null.
    * @param args
    *           method's parameters
    * @return true if all the method's parameters pass all method-level validators
    */
   private boolean passesMethodValidation(Method method, Object... args) {
      ParamValidators paramValidatorsAnnotation = checkNotNull(method).getAnnotation(
               ParamValidators.class);
      if (paramValidatorsAnnotation == null)
         return true; // by contract

      List<Validator<?>> methodValidators = getValidatorsFromAnnotation(paramValidatorsAnnotation);

      return runPredicatesAgainstArgs(methodValidators, args);
   }

   /**
    * Returns true if all the method parameters passed all of their corresponding validators.
    * 
    * @param annotations
    *           annotations for method's arguments
    * @param args
    *           arguments that correspond to the array of annotations
    * @return true if all the method parameters passed all of their corresponding validators.
    */
   private boolean passesParameterValidation(Annotation[][] annotations, Object... args) {
      boolean allPreducatesTrue = true;
      for (int currentParameterIndex = 0; currentParameterIndex < annotations.length; currentParameterIndex++) {
         ParamValidators annotation = findParamValidatorsAnnotationOrReturnNull(annotations[currentParameterIndex]);
         if (annotation == null)
            continue;
         List<Validator<?>> parameterValidators = getValidatorsFromAnnotation(annotation);
         allPreducatesTrue &= runPredicatesAgainstArgs(parameterValidators,
                  args[currentParameterIndex]);
      }
      return allPreducatesTrue;
   }

   private List<Validator<?>> getValidatorsFromAnnotation(ParamValidators paramValidatorsAnnotation) {
      List<Validator<?>> validators = Lists.newArrayList();
      for (Class<? extends Validator<?>> validator : paramValidatorsAnnotation.value()) {
         validators.add(checkNotNull(injector.getInstance(validator)));
      }
      return validators;
   }

   @SuppressWarnings("unchecked")
   private boolean runPredicatesAgainstArgs(List<Validator<?>> predicates, Object... args) {
      boolean allPredicatesTrue = true;
      for (Validator validator : predicates) {
         allPredicatesTrue &= Iterables.all(Arrays.asList(args), validator);
      }
      return allPredicatesTrue;
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
