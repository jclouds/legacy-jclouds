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
package org.jclouds.blobstore.functions;

import static org.jclouds.util.Utils.propagateOrNull;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.internal.BlobRuntimeException;
import org.jclouds.blobstore.strategy.ClearContainerStrategy;
import org.jclouds.http.HttpResponseException;
import org.jclouds.rest.InvocationContext;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.inject.Inject;

public class ClearAndDeleteIfNotEmpty implements Function<Exception, Void>, InvocationContext {

   private final ClearContainerStrategy clear;
   private final BlobStore connection;

   private GeneratedHttpRequest<?> request;

   @Inject
   protected ClearAndDeleteIfNotEmpty(ClearContainerStrategy clear, BlobStore connection) {
      this.clear = clear;
      this.connection = connection;
   }

   public Void apply(Exception from) {
      if (from instanceof HttpResponseException) {
         HttpResponseException responseException = (HttpResponseException) from;
         if (responseException.getResponse().getStatusCode() == 404) {
            return null;
         } else if (responseException.getResponse().getStatusCode() == 409) {
            clear.execute(request.getArgs()[0].toString());
            try {
               connection.deleteContainer(request.getArgs()[0].toString());
               return null;
            } catch (Exception e) {
               Throwables.propagateIfPossible(e, BlobRuntimeException.class);
               throw new BlobRuntimeException("Error deleting container: "
                        + request.getArgs()[0].toString(), e);
            }
         }
      }
      return Void.class.cast(propagateOrNull(from));
   }

   public void setContext(GeneratedHttpRequest<?> request) {
      this.request = request;
   }

}
