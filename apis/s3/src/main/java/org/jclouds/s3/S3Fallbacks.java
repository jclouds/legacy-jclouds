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
package org.jclouds.s3;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.util.concurrent.Futures.immediateFuture;
import static org.jclouds.http.HttpUtils.returnValueOnCodeOrNull;
import static org.jclouds.util.Throwables2.getFirstThrowableOfType;

import org.jclouds.blobstore.ContainerNotFoundException;

import com.google.common.util.concurrent.FutureFallback;
import com.google.common.util.concurrent.ListenableFuture;

public final class S3Fallbacks {
   private S3Fallbacks() {
   }

   public static final class TrueOn404OrNotFoundFalseOnIllegalState implements FutureFallback<Boolean> {
      @Override
      public ListenableFuture<Boolean> create(final Throwable t) {
         if (getFirstThrowableOfType(checkNotNull(t, "throwable"), IllegalStateException.class) != null)
            return immediateFuture(false);
         if (getFirstThrowableOfType(t, ContainerNotFoundException.class) != null)
            return immediateFuture(true);
         if (returnValueOnCodeOrNull(t, true, equalTo(404)) != null)
            return immediateFuture(true);
         throw propagate(t);
      }
   }
}