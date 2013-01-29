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
package org.jclouds.rds;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.util.concurrent.Futures.immediateFuture;
import static org.jclouds.Fallbacks.valOnNotFoundOr404;

import org.jclouds.Fallback;
import org.jclouds.aws.AWSResponseException;

import com.google.common.util.concurrent.ListenableFuture;

public final class RDSFallbacks {
   private RDSFallbacks() {
   }

   public static final class NullOnStateDeletingNotFoundOr404 implements Fallback<Object> {
      @Override
      public ListenableFuture<Object> create(Throwable t) throws Exception {
         return immediateFuture(createOrPropagate(t));
      }

      @Override
      public Object createOrPropagate(Throwable t) throws Exception {
         if (checkNotNull(t, "throwable") instanceof AWSResponseException) {
            AWSResponseException e = AWSResponseException.class.cast(t);
            if ("InvalidDBInstanceState".equals(e.getError().getCode())
                  && e.getError().getMessage().contains("has state: deleting"))
               return null;
         }
         return valOnNotFoundOr404(null, t);
      }
   }
}