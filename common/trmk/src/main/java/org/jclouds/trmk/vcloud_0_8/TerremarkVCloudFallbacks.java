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
package org.jclouds.trmk.vcloud_0_8;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.util.concurrent.Futures.immediateFuture;

import java.util.regex.Pattern;

import org.jclouds.Fallback;
import org.jclouds.http.HttpResponseException;
import org.jclouds.rest.AuthorizationException;

import com.google.common.util.concurrent.ListenableFuture;

public final class TerremarkVCloudFallbacks {
   private TerremarkVCloudFallbacks() {
   }

   /**
    * There's no current way to determine if an IP is the default outbound one. In this case, we may get errors on
    * deleting an IP, which are ok.
    * 
    * @author Adrian Cole
    */
   public static final class VoidOnDeleteDefaultIp implements Fallback<Void> {
      public static final Pattern MESSAGE_PATTERN = Pattern
            .compile(".*Cannot release this Public IP as it is default oubound IP.*");

      @Override
      public ListenableFuture<Void> create(Throwable t) throws Exception {
         return immediateFuture(createOrPropagate(t));
      }

      @Override
      public Void createOrPropagate(Throwable t) throws Exception {
         if (checkNotNull(t, "throwable") instanceof HttpResponseException) {
            HttpResponseException hre = HttpResponseException.class.cast(t);
            if (hre.getResponse().getStatusCode() == 503 || hre.getResponse().getStatusCode() == 401
                  || MESSAGE_PATTERN.matcher(hre.getMessage()).matches())
               return null;
         } else if (t instanceof AuthorizationException) {
            return null;
         }
         throw propagate(t);
      }
   }

}
