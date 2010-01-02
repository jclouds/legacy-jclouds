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
package org.jclouds.demo.tweetstore.functions;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.blobstore.BlobMap;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.demo.tweetstore.domain.StoredTweetStatus;
import org.jclouds.demo.tweetstore.reference.TweetStoreConstants;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

@Singleton
public class ServiceToStoredTweetStatuses implements Function<String, Iterable<StoredTweetStatus>> {

   private final Map<String, BlobStoreContext<?,?>> contexts;
   private final String container;

   @Inject
   public ServiceToStoredTweetStatuses(Map<String, BlobStoreContext<?,?>> contexts,
            @Named(TweetStoreConstants.PROPERTY_TWEETSTORE_CONTAINER) String container) {
      this.contexts = contexts;
      this.container = container;
   }

   @Resource
   protected Logger logger = Logger.NULL;

   public Iterable<StoredTweetStatus> apply(String service) {
      BlobStoreContext<?, ?> context = contexts.get(service);
      String host = context.getEndPoint().getHost();
      try {
         BlobMap blobMap = context.createBlobMap(container);
         Set<String> blobs = blobMap.keySet();
         return Iterables.transform(blobs, new KeyToStoredTweetStatus(blobMap, service, host,
                  container));
      } catch (Exception e) {
         StoredTweetStatus result = new StoredTweetStatus(service, host, container, null, null,
                  null, e.getMessage());
         logger.error(e, "Error listing service %s", service);
         return Collections.singletonList(result);
      }

   }
}