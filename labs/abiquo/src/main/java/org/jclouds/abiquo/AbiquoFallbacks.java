/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo;

import static com.google.common.base.Throwables.getCausalChain;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Iterables.find;
import static com.google.common.util.concurrent.Futures.immediateFuture;

import javax.ws.rs.core.Response.Status;

import org.jclouds.Fallback;
import org.jclouds.abiquo.domain.exception.AbiquoException;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.rest.ResourceNotFoundException;

import com.google.common.base.Predicate;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * fallbacks common to abiquo
 * 
 * @author Ignasi Barrera
 */
public final class AbiquoFallbacks {
   private AbiquoFallbacks() {
   }
   
   /**
    * Return an Abiquo Exception on not found errors.
    */
   public static final class PropagateAbiquoExceptionOnNotFoundOr4xx implements Fallback<Object> {
      @Override
      public ListenableFuture<Object> create(Throwable from) throws Exception {
         return immediateFuture(createOrPropagate(from));
      }

      @Override
      public Object createOrPropagate(Throwable from) throws Exception {
         Throwable exception = find(getCausalChain(from), isNotFoundAndHasAbiquoException(from), null);
         throw propagate(exception == null ? from : exception.getCause());
      }
   }

   /**
    * Return <code>null</code> on 303 response codes when requesting a task.
    */
   public static final class NullOn303 implements Fallback<Object> {
      @Override
      public ListenableFuture<Object> create(Throwable from) throws Exception {
         return immediateFuture(createOrPropagate(from));
      }

      @Override
      public Object createOrPropagate(Throwable from) throws Exception {
         Throwable exception = find(getCausalChain(from), hasResponse(from), null);

         if (exception != null) {
            HttpResponseException responseException = (HttpResponseException) exception;
            HttpResponse response = responseException.getResponse();

            if (response != null && response.getStatusCode() == Status.SEE_OTHER.getStatusCode()) {
               return null;
            }
         }

         throw propagate(from);
      }

   }

   /**
    * Return false on service error exceptions.
    */
   public static final class FalseOn5xx implements Fallback<Boolean> {
      @Override
      public ListenableFuture<Boolean> create(Throwable from) throws Exception {
         return immediateFuture(createOrPropagate(from));
      }

      @Override
      public Boolean createOrPropagate(Throwable from) throws Exception {
         Throwable exception = find(getCausalChain(from), hasResponse(from), null);

         if (exception != null) {
            HttpResponseException responseException = (HttpResponseException) exception;
            HttpResponse response = responseException.getResponse();

            if (response != null && response.getStatusCode() >= 500 && response.getStatusCode() < 600) {
               return false;
            }
         }

         throw propagate(from);
      }

   }

   /**
    * Return false on service error exceptions.
    */
   public static final class FalseIfNotAvailable implements Fallback<Boolean> {
      @Override
      public ListenableFuture<Boolean> create(Throwable from) throws Exception {
         return immediateFuture(createOrPropagate(from));
      }

      @Override
      public Boolean createOrPropagate(Throwable from) throws Exception {
         Throwable exception = find(getCausalChain(from), isNotAvailableException(from), null);

         if (exception != null) {
            if (exception instanceof HttpResponseException) {
               HttpResponseException responseException = (HttpResponseException) exception;
               HttpResponse response = responseException.getResponse();

               if (response != null && response.getStatusCode() >= 500 && response.getStatusCode() < 600) {
                  return false;
               }
            } else {
               // Will enter here when exception is a ResourceNotFoundException
               return false;
            }
         }

         throw propagate(from);
      }

   }

   private static Predicate<Throwable> isNotFoundAndHasAbiquoException(final Throwable exception) {
      return new Predicate<Throwable>() {
         @Override
         public boolean apply(final Throwable input) {
            return input instanceof ResourceNotFoundException && input.getCause() instanceof AbiquoException;
         }
      };
   }

   private static Predicate<Throwable> isNotAvailableException(final Throwable exception) {
      return new Predicate<Throwable>() {
         @Override
         public boolean apply(final Throwable input) {
            boolean notAvailable = input instanceof HttpResponseException
                  && ((HttpResponseException) input).getResponse() != null;

            notAvailable |= input instanceof ResourceNotFoundException;

            return notAvailable;
         }
      };
   }

   private static Predicate<Throwable> hasResponse(final Throwable exception) {
      return new Predicate<Throwable>() {
         @Override
         public boolean apply(final Throwable input) {
            return input instanceof HttpResponseException && ((HttpResponseException) input).getResponse() != null;
         }
      };
   }
}