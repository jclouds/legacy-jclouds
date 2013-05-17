/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.cloudstack.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.util.concurrent.Futures.immediateFuture;
import static org.jclouds.Fallbacks.valOnNotFoundOr404;
import static org.jclouds.util.Throwables2.getFirstThrowableOfType;

import org.jclouds.Fallback;

import com.google.common.util.concurrent.ListenableFuture;

public final class CloudStackFallbacks {
   private CloudStackFallbacks() {
   }

   /**
    * CloudStack is currently sending 431 errors with the text "Unable to find account owner for ip ". In this case, we
    * have to ignore as there's no means for us to avoid the problem, or action to take.
    */
   public static final class VoidOnNotFoundOr404OrUnableToFindAccountOwner implements Fallback<Void> {
      @Override
      public ListenableFuture<Void> create(Throwable t) throws Exception {
         return immediateFuture(createOrPropagate(t));
      }

      @Override
      public Void createOrPropagate(Throwable t) throws Exception {
         IllegalStateException e = getFirstThrowableOfType(checkNotNull(t, "throwable"), IllegalStateException.class);
         if (e != null && e.getMessage().indexOf("Unable to find account owner for") != -1) {
            return null;
         } else {
            return valOnNotFoundOr404(null, t);
         }
      }
   }
}
