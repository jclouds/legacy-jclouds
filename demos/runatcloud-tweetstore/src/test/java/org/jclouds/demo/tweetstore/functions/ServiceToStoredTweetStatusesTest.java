/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.BlobStoreContextFactory;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.demo.tweetstore.domain.StoredTweetStatus;
import org.jclouds.demo.tweetstore.reference.TweetStoreConstants;
import org.testng.annotations.Test;
import org.testng.collections.Maps;

import com.google.common.collect.Iterables;

/**
 * Tests behavior of {@code ServiceToStoredTweetStatuses}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ServiceToStoredTweetStatusesTest {

   Map<String, BlobStoreContext> createServices(String container) throws InterruptedException,
            ExecutionException {
      Map<String, BlobStoreContext> services = Maps.newHashMap();
      for (String name : new String[] { "1", "2" }) {
         BlobStoreContext context = new BlobStoreContextFactory().createContext("transient",
                  "dummy", "dummy");
         context.getAsyncBlobStore().createContainerInLocation(null, container).get();
         Blob blob = context.getAsyncBlobStore().blobBuilder("1").build();
         blob.getMetadata().getUserMetadata().put(TweetStoreConstants.SENDER_NAME, "frank");
         blob.setPayload("I love beans!");
         context.getAsyncBlobStore().putBlob(container, blob).get();
         services.put(name, context);
      }
      return services;
   }

   public void testStoreTweets() throws IOException, InterruptedException, ExecutionException {
      String container = "container";
      Map<String, BlobStoreContext> contexts = createServices(container);

      ServiceToStoredTweetStatuses function = new ServiceToStoredTweetStatuses(contexts, container);

      assertEquals(Iterables.getLast(function.apply("1")), new StoredTweetStatus("1", "localhost",
               container, "1", "frank", "I love beans!", null));

      assertEquals(Iterables.getLast(function.apply("2")), new StoredTweetStatus("2", "localhost",
               container, "1", "frank", "I love beans!", null));

   }
}
