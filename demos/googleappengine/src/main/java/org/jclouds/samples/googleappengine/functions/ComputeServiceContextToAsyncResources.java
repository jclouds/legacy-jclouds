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

import java.util.concurrent.Callable;

import javax.annotation.Resource;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.domain.ResourceMetadata;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;

/**
 * ComputeService doesn't currently have an Async counterpart
 * 
 * @author Adrian Cole
 */
@Singleton
public class ComputeServiceContextToAsyncResources implements
      Function<ComputeServiceContext, ListenableFuture<? extends Iterable<? extends ResourceMetadata<?>>>> {

   @Resource
   protected Logger logger = Logger.NULL;

   private final ListeningExecutorService currentRequestExecutorService;

   @Inject
   public ComputeServiceContextToAsyncResources(ListeningExecutorService currentRequestExecutorService) {
      this.currentRequestExecutorService = currentRequestExecutorService;
   }

   public ListenableFuture<? extends Iterable<? extends ResourceMetadata<?>>> apply(final ComputeServiceContext in) {
      return currentRequestExecutorService.submit(new Callable<Iterable<? extends ResourceMetadata<?>>>() {

         @Override
         public Iterable<? extends ResourceMetadata<?>> call() throws Exception {
            logger.info("listing nodes on %s: ", in.unwrap().getId());
            return in.getComputeService().listNodes();
         }

         @Override
         public String toString() {
            return in.toString();
         }
      });
   }
}
