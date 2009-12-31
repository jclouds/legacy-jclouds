/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.samples.googleappengine.functions;

import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.blobstore.domain.ResourceMetadata;
import org.jclouds.blobstore.domain.ResourceType;
import org.jclouds.logging.Logger;
import org.jclouds.samples.googleappengine.domain.ContainerResult;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

@Singleton
public class BlobStoreContextToContainerResult implements Function<String, ContainerResult> {
   private final class BuildContainerResult implements Function<ResourceMetadata, ContainerResult> {
      private final String host;
      private final BlobStoreContext<?, ?> context;
      private final String contextName;

      private BuildContainerResult(String host, BlobStoreContext<?, ?> context, String contextName) {
         this.host = host;
         this.context = context;
         this.contextName = contextName;
      }

      public ContainerResult apply(ResourceMetadata from) {
         String status;
         try {
            try {
               long start = System.currentTimeMillis();
               context.getBlobStore().containerExists(from.getName());
               status = ((System.currentTimeMillis() - start) + "ms");
            } catch (ContainerNotFoundException ex) {
               status = ("not found");
            }
         } catch (Exception e) {
            logger.error(e, "Error listing container %s//%s", contextName, from);
            status = (e.getMessage());
         }
         return new ContainerResult(contextName, host, from.getName(), status);
      }
   }

   @Inject
   private Map<String, BlobStoreContext<?, ?>> contexts;

   @Resource
   protected Logger logger = Logger.NULL;

   public ContainerResult apply(final String contextName) {
      final BlobStoreContext<?, ?> context = contexts.get(contextName);
      final String host = context.getEndPoint().getHost();
      try {
         ResourceMetadata md = Iterables.getLast(Sets.newTreeSet(Iterables.filter(context
                  .getBlobStore().list(), new Predicate<ResourceMetadata>() {

            public boolean apply(ResourceMetadata input) {
               return input.getType() == ResourceType.CONTAINER;
            }

         })));
         return new BuildContainerResult(host, context, contextName).apply(md);
      } catch (Exception e) {
         ContainerResult result = new ContainerResult(contextName, host, null, e.getMessage());
         logger.error(e, "Error listing service %s", contextName);
         return result;
      }

   }
}