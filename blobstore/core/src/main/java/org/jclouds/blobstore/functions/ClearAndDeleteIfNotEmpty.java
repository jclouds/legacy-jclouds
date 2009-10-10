/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.blobstore.functions;

import java.lang.reflect.Constructor;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ContainerMetadata;
import org.jclouds.blobstore.internal.BlobRuntimeException;
import org.jclouds.blobstore.reference.BlobStoreConstants;
import org.jclouds.blobstore.strategy.ClearContainerStrategy;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponseException;
import org.jclouds.rest.InvocationContext;
import org.jclouds.util.Utils;

import com.google.common.base.Function;

public class ClearAndDeleteIfNotEmpty<C extends ContainerMetadata, M extends BlobMetadata, B extends Blob<M>>
         implements Function<Exception, Void>, InvocationContext {
   static final Void v;
   static {
      Constructor<Void> cv;
      try {
         cv = Void.class.getDeclaredConstructor();
         cv.setAccessible(true);
         v = cv.newInstance();
      } catch (Exception e) {
         throw new Error("Error setting up class", e);
      }
   }
   /**
    * maximum duration of an blob Request
    */
   @Inject(optional = true)
   @Named(BlobStoreConstants.PROPERTY_BLOBSTORE_TIMEOUT)
   protected long requestTimeoutMilliseconds = 30000;
   private Object[] args;
   private HttpRequest request;

   private final ClearContainerStrategy<C, M, B> clear;
   private final BlobStore<C, M, B> connection;

   @Inject
   protected
   ClearAndDeleteIfNotEmpty(ClearContainerStrategy<C, M, B> clear, BlobStore<C, M, B> connection) {
      this.clear = clear;
      this.connection = connection;
   }

   public Void apply(Exception from) {
      if (from instanceof HttpResponseException) {
         HttpResponseException responseException = (HttpResponseException) from;
         if (responseException.getResponse().getStatusCode() == 404) {
            return v;
         } else if (responseException.getResponse().getStatusCode() == 409) {
            clear.execute(connection, args[0].toString());
            try {
               connection.deleteContainer(args[0].toString()).get(requestTimeoutMilliseconds,
                        TimeUnit.MILLISECONDS);
               return v;
            } catch (Exception e) {
               Utils.<BlobRuntimeException> rethrowIfRuntimeOrSameType(e);
               throw new BlobRuntimeException("Error deleting container: " + args[0].toString(), e);
            }
         }
      }
      return null;
   }

   public Object[] getArgs() {
      return args;
   }

   public HttpRequest getRequest() {
      return request;
   }

   public void setContext(HttpRequest request, Object[] args) {
      this.request = request;
      this.args = args;
   }

}
