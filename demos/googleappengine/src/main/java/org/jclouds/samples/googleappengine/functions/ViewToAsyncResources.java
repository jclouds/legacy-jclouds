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
package org.jclouds.samples.googleappengine.functions;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.View;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.domain.ResourceMetadata;

import com.google.common.base.Function;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ViewToAsyncResources implements Function<View, ListenableFuture<? extends Iterable<? extends ResourceMetadata<?>>>> {
   private final BlobStoreContextToAsyncResources blobStoreContextToAsyncResources;
   private final ComputeServiceContextToAsyncResources computeServiceContextToAsyncResources;

   @Inject
   public ViewToAsyncResources(BlobStoreContextToAsyncResources blobStoreContextToAsyncResources,
         ComputeServiceContextToAsyncResources computeServiceContextToAsyncResources) {
      this.blobStoreContextToAsyncResources = blobStoreContextToAsyncResources;
      this.computeServiceContextToAsyncResources = computeServiceContextToAsyncResources;
   }


   @Override
   public ListenableFuture<? extends Iterable<? extends ResourceMetadata<?>>> apply(View input) {
      if (input instanceof BlobStoreContext) {
         return blobStoreContextToAsyncResources.apply(BlobStoreContext.class.cast(input));
      } else if (input instanceof ComputeServiceContext) {
         return computeServiceContextToAsyncResources.apply(ComputeServiceContext.class.cast(input));
      }
      throw new UnsupportedOperationException("unknown view type: " + input);
   }

}
