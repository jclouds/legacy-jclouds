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
import static com.google.common.base.Preconditions.checkArgument;

import java.util.NoSuchElementException;

import org.jclouds.blobstore.config.TransientBlobStore;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.rest.internal.ContextBuilder;

/**
 * @author Adrian Cole
 */
public abstract class BlobStoreContextBuilder<S, A, C extends BlobStoreContext<S, A>, M extends BlobStoreApiMetadata<S, A, C, M>>
      extends ContextBuilder<S, A, C, M> {
   
   /**
    * looks up a provider or api with the given id
    * 
    * @param providerOrApi
    *           id of the provider or api
    * @return means to build a context to that provider
    * @throws NoSuchElementException
    *            if the id was not configured.
    * @throws IllegalArgumentException
    *            if the api or provider isn't assignable from BlobStoreContext
    */
   public static BlobStoreContextBuilder<?, ?, ?, ?> newBuilder(String providerOrApi) throws NoSuchElementException {
      ContextBuilder<?, ?, ?, ?> builder = ContextBuilder.newBuilder(providerOrApi);
      checkArgument(builder instanceof BlobStoreContextBuilder,
            "type of providerOrApi[%s] is not BlobStoreContextBuilder: %s", providerOrApi, builder);
      return BlobStoreContextBuilder.class.cast(builder);
   }
   
   public static ContextBuilder<TransientBlobStore, AsyncBlobStore, BlobStoreContext<TransientBlobStore, AsyncBlobStore>, TransientApiMetadata> forTests() {
      return ContextBuilder.newBuilder(new TransientApiMetadata());
   }

   public BlobStoreContextBuilder(ProviderMetadata<S, A, C, M> providerMetadata) {
      super(providerMetadata);
   }

   public BlobStoreContextBuilder(M apiMetadata) {
      super(apiMetadata);
   }
}