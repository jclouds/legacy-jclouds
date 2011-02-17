/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.util;

import static com.google.common.base.Predicates.instanceOf;
import static com.google.common.base.Throwables.getCausalChain;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Iterables.find;

import java.util.NoSuchElementException;

import org.jclouds.http.HttpResponseException;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.InsufficientResourcesException;
import org.jclouds.rest.ResourceNotFoundException;

import com.google.common.base.Throwables;
import com.google.inject.ProvisionException;
import com.google.inject.spi.Message;

/**
 * General utilities used in jclouds code.
 * 
 * @author Adrian Cole
 */
public class Throwables2 {

   @SuppressWarnings("unchecked")
   public static <T extends Throwable> T getFirstThrowableOfType(Throwable from, Class<T> clazz) {
      if (from instanceof ProvisionException)
         return getFirstThrowableOfType(ProvisionException.class.cast(from), clazz);
      try {
         return (T) find(getCausalChain(from), instanceOf(clazz));
      } catch (NoSuchElementException e) {
         return null;
      }
   }

   public static <T extends Throwable> T getFirstThrowableOfType(ProvisionException e, Class<T> clazz) {
      for (Message message : e.getErrorMessages()) {
         if (message.getCause() != null) {
            T cause = getFirstThrowableOfType(message.getCause(), clazz);
            if (cause instanceof ProvisionException)
               return getFirstThrowableOfType(ProvisionException.class.cast(cause), clazz);
            return cause;
         }
      }
      return null;
   }

   public static <T> T propagateOrNull(Exception from) {
      propagate(from);
      assert false : "exception should have propogated";
      return null;
   }

   // Note this needs to be kept up-to-date with all top-level exceptions jclouds works against
   @SuppressWarnings( { "unchecked", "rawtypes" })
   public static Exception returnFirstExceptionIfInListOrThrowStandardExceptionOrCause(Class[] exceptionTypes,
            Exception exception) throws Exception {
      for (Class type : exceptionTypes) {
         Throwable throwable = getFirstThrowableOfType(exception, type);
         if (throwable != null) {
            return (Exception) throwable;
         }
      }
      for (Class<Exception> propagatableExceptionType : new Class[] { IllegalStateException.class,
               UnsupportedOperationException.class, IllegalArgumentException.class, AuthorizationException.class,
               ResourceNotFoundException.class, InsufficientResourcesException.class, HttpResponseException.class }) {
         Throwable throwable = getFirstThrowableOfType(exception, propagatableExceptionType);
         if (throwable != null) {
            throw (Exception) throwable;
         }
      }
      Throwables.throwCause(exception, true);
      return exception;
   }

   public static <T> T propagateAuthorizationOrOriginalException(Exception e) {
      AuthorizationException aex = getFirstThrowableOfType(e, AuthorizationException.class);
      if (aex != null)
         throw aex;
      propagate(e);
      assert false : "exception should have propogated " + e;
      return null;
   }

}
