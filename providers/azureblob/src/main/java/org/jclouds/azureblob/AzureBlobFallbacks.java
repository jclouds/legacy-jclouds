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
package org.jclouds.azureblob;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.util.concurrent.Futures.immediateFuture;

import org.jclouds.azure.storage.AzureStorageResponseException;

import com.google.common.util.concurrent.FutureFallback;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * 
 * @author Adrian Cole
 */
public final class AzureBlobFallbacks {
   private AzureBlobFallbacks() {
   }

   public static final class FalseIfContainerAlreadyExists implements FutureFallback<Boolean> {

      @Override
      public ListenableFuture<Boolean> create(Throwable t) {
         if (checkNotNull(t, "throwable") instanceof AzureStorageResponseException) {
            AzureStorageResponseException responseException = AzureStorageResponseException.class.cast(t);
            if ("ContainerAlreadyExists".equals(responseException.getError().getCode())) {
               return immediateFuture(false);
            }
         }
         throw propagate(t);
      }
   }
}
