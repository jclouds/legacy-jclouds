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
package org.jclouds.blobstore;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.util.concurrent.Futures.immediateFuture;
import static org.jclouds.http.HttpUtils.contains404;

import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.blobstore.KeyNotFoundException;

import com.google.common.util.concurrent.FutureFallback;
import com.google.common.util.concurrent.ListenableFuture;

public final class BlobStoreFallbacks {
   private BlobStoreFallbacks() {
   }
   
   public static final class ThrowContainerNotFoundOn404 implements FutureFallback<Object> {

      @Override
      public ListenableFuture<Object> create(Throwable t) {
         if (contains404(checkNotNull(t, "throwable")))
            throw new ContainerNotFoundException(t);
         throw propagate(t);
      }

   }

   public static final class ThrowKeyNotFoundOn404 implements FutureFallback<Object> {

      @Override
      public ListenableFuture<Object> create(Throwable t) {
         if (contains404(checkNotNull(t, "throwable")))
            throw new KeyNotFoundException(t);
         throw propagate(t);
      }

   }

   public static final class FalseOnContainerNotFound implements FutureFallback<Boolean> {

      @Override
      public ListenableFuture<Boolean> create(Throwable t) {
         if (checkNotNull(t, "throwable") instanceof ContainerNotFoundException) {
            return immediateFuture(false);
         }
         throw propagate(t);
      }
   }

   public static final class FalseOnKeyNotFound implements FutureFallback<Boolean> {

      @Override
      public ListenableFuture<Boolean> create(Throwable t) {
         if (checkNotNull(t, "throwable") instanceof KeyNotFoundException) {
            return immediateFuture(false);
         }
         throw propagate(t);
      }
   }

   public static final class NullOnContainerNotFound implements FutureFallback<Object> {

      @Override
      public ListenableFuture<Object> create(Throwable t) {
         if (checkNotNull(t, "throwable") instanceof ContainerNotFoundException) {
            return immediateFuture(null);
         }
         throw propagate(t);
      }
   }

   public static final class NullOnKeyNotFound implements FutureFallback<Object> {

      @Override
      public ListenableFuture<Object> create(Throwable t) {
         if (checkNotNull(t, "throwable") instanceof KeyNotFoundException) {
            return immediateFuture(null);
         }
         throw propagate(t);
      }
   }
}
