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

import static org.jclouds.util.Utils.toStringAndClose;

import javax.annotation.Resource;

import org.jclouds.blobstore.BlobMap;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.demo.tweetstore.domain.StoredTweetStatus;
import org.jclouds.demo.tweetstore.reference.TweetStoreConstants;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;

/**
 * 
 * @author Adrian Cole
 */
public class KeyToStoredTweetStatus implements Function<String, StoredTweetStatus> {
   private final String host;
   private final BlobMap map;
   private final String service;
   private final String container;

   @Resource
   protected Logger logger = Logger.NULL;

   KeyToStoredTweetStatus(BlobMap map, String service, String host, String container) {
      this.host = host;
      this.map = map;
      this.service = service;
      this.container = container;
   }

   public StoredTweetStatus apply(String id) {
      String status;
      String from;
      String tweet;
      try {
         long start = System.currentTimeMillis();
         Blob blob = map.get(id);
         status = ((System.currentTimeMillis() - start) + "ms");
         from = blob.getMetadata().getUserMetadata().get(TweetStoreConstants.SENDER_NAME);
         tweet = toStringAndClose(blob.getPayload().getInput());
      } catch (Exception e) {
         logger.error(e, "Error listing container %s//%s/$s", service, container, id);
         status = (e.getMessage());
         tweet = "";
         from = "";
      }
      return new StoredTweetStatus(service, host, container, id, from, tweet, status);
   }
}
