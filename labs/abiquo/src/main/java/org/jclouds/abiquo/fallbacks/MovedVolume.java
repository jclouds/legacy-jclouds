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

package org.jclouds.abiquo.fallbacks;

import static com.google.common.base.Throwables.getCausalChain;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Iterables.find;
import static com.google.common.util.concurrent.Futures.immediateFuture;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.Response.Status;

import org.jclouds.Fallback;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.http.functions.ParseXMLWithJAXB;
import org.jclouds.xml.XMLParser;

import com.abiquo.server.core.infrastructure.storage.MovedVolumeDto;
import com.abiquo.server.core.infrastructure.storage.VolumeManagementDto;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Predicate;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.TypeLiteral;

/**
 * Return false on service error exceptions.
 * 
 * @author Ignasi Barrera
 */
@Singleton
public class MovedVolume implements Fallback<VolumeManagementDto> {

   @Singleton
   @VisibleForTesting
   static class ReturnMoveVolumeReference extends ParseXMLWithJAXB<MovedVolumeDto> {
      @Inject
      public ReturnMoveVolumeReference(final XMLParser xml, final TypeLiteral<MovedVolumeDto> type) {
         super(xml, type);
      }

   }

   private ParseXMLWithJAXB<MovedVolumeDto> parser;

   @Inject
   public MovedVolume(final ReturnMoveVolumeReference parser) {
      this.parser = parser;
   }

   @Override
   public ListenableFuture<VolumeManagementDto> create(Throwable from) throws Exception {
      return immediateFuture(createOrPropagate(from));
   }

   @Override
   public VolumeManagementDto createOrPropagate(Throwable from) throws Exception {
      Throwable exception = find(getCausalChain(from), isMovedException(from), null);

      if (exception != null) {
         HttpResponseException responseException = (HttpResponseException) exception;
         HttpResponse response = responseException.getResponse();

         return parser.apply(response).getVolume();
      }

      throw propagate(from);
   }

   private static Predicate<Throwable> isMovedException(final Throwable exception) {
      return new Predicate<Throwable>() {
         @Override
         public boolean apply(final Throwable input) {
            if (input instanceof HttpResponseException) {
               HttpResponse response = ((HttpResponseException) input).getResponse();
               return response != null && response.getStatusCode() == Status.MOVED_PERMANENTLY.getStatusCode();
            }

            return false;
         }
      };
   }
}
