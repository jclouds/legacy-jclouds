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
package org.jclouds.jenkins.v1;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.util.concurrent.Futures.immediateFuture;
import static org.jclouds.http.HttpUtils.returnValueOnCodeOrNull;

import com.google.common.base.Predicates;
import com.google.common.util.concurrent.FutureFallback;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * 
 * @author Adrian Cole
 */
public final class JenkinsFallbacks {
   private JenkinsFallbacks() {
   }

   public static final class VoidOn302Or404 implements FutureFallback<Void> {
      @Override
      public ListenableFuture<Void> create(final Throwable t) {
         Boolean returnVal = returnValueOnCodeOrNull(checkNotNull(t, "throwable"), true,
               Predicates.<Integer> or(equalTo(302), equalTo(404)));
         if (returnVal != null && returnVal)
            return immediateFuture(null);
         throw propagate(t);
      }

   }
}
