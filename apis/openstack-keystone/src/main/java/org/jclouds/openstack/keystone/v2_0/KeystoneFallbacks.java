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
package org.jclouds.openstack.keystone.v2_0;

import static com.google.common.util.concurrent.Futures.immediateFuture;
import static org.jclouds.Fallbacks.valOnNotFoundOr404;

import org.jclouds.Fallback;
import org.jclouds.openstack.keystone.v2_0.domain.PaginatedCollection;
import org.jclouds.openstack.v2_0.domain.Link;

import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.ListenableFuture;

public final class KeystoneFallbacks {
   private KeystoneFallbacks() {
   }

   public static final class EmptyPaginatedCollectionOnNotFoundOr404 implements Fallback<PaginatedCollection<Object>> {
      private static final PaginatedCollection<Object> EMPTY = new PaginatedCollection<Object>(
            ImmutableSet.<Object> of(), ImmutableSet.<Link> of()) {
      };

      @Override
      public ListenableFuture<PaginatedCollection<Object>> create(Throwable t) throws Exception {
         return immediateFuture(createOrPropagate(t));
      }

      @Override
      public PaginatedCollection<Object> createOrPropagate(Throwable t) throws Exception {
         return valOnNotFoundOr404(EMPTY, t);
      }
   }

}
