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
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.StorageType;
import org.jclouds.logging.Logger;
import org.jclouds.samples.googleappengine.domain.StatusResult;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

@Singleton
public class BlobStoreContextToStatusResult implements Function<String, StatusResult> {
   private final class BuildContainerResult implements Function<StorageMetadata, StatusResult> {
      private final String host;
      private final BlobStoreContext context;
      private final String contextName;

      private BuildContainerResult(String host, BlobStoreContext context, String contextName) {
         this.host = host;
         this.context = context;
         this.contextName = contextName;
      }

      public StatusResult apply(StorageMetadata from) {
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
         return new StatusResult(contextName, host, from.getName(), status);
      }
   }

   @Inject
   private Map<String, BlobStoreContext> contexts;

   @Resource
   protected Logger logger = Logger.NULL;

   public StatusResult apply(final String contextName) {
      final BlobStoreContext context = contexts.get(contextName);
      final String host = context.getProviderSpecificContext().getEndPoint().getHost();
      try {
         StorageMetadata md = Iterables.getLast(Sets.newTreeSet(Iterables.filter(context
                  .getBlobStore().list(), new Predicate<StorageMetadata>() {

            public boolean apply(StorageMetadata input) {
               return input.getType() == StorageType.CONTAINER;
            }

         })));
         return new BuildContainerResult(host, context, contextName).apply(md);
      } catch (Exception e) {
         StatusResult result = new StatusResult(contextName, host, null, e.getMessage());
         logger.error(e, "Error listing service %s", contextName);
         return result;
      }

   }
}