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
package org.jclouds.cloudstack.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.util.concurrent.Futures.immediateFuture;
import static org.jclouds.util.Throwables2.getFirstThrowableOfType;

import org.jclouds.Fallbacks;

import com.google.common.util.concurrent.FutureFallback;
import com.google.common.util.concurrent.ListenableFuture;

public final class CloudStackFallbacks {
   private CloudStackFallbacks() {
   }

   /**
    * CloudStack is currently sending 431 errors with the text "Unable to find account owner for ip ". In this case, we
    * have to ignore as there's no means for us to avoid the problem, or action to take.
    */
   public static final class VoidOnNotFoundOr404OrUnableToFindAccountOwner implements FutureFallback<Void> {
      @Override
      public ListenableFuture<Void> create(final Throwable t) {
         IllegalStateException e = getFirstThrowableOfType(checkNotNull(t, "throwable"), IllegalStateException.class);
         if (e != null && e.getMessage().indexOf("Unable to find account owner for") != -1) {
            return immediateFuture(null);
         } else {
            return Fallbacks.valOnNotFoundOr404(null, t);
         }
      }
   }
}
