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

package org.jclouds.abiquo.functions;

import javax.inject.Singleton;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.rest.ResourceNotFoundException;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;

/**
 * Return false on service error exceptions.
 * 
 * @author Ignasi Barrera
 */
@Singleton
public class ReturnFalseIfNotAvailable implements Function<Exception, Object> {
   @Override
   public Object apply(final Exception from) {
      Throwable exception = Iterables.find(Throwables.getCausalChain(from), isNotAvailableException(from), null);

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

      throw Throwables.propagate(from);
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
}
