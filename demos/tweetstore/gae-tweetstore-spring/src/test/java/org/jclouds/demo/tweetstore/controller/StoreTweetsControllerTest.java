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
package org.jclouds.demo.tweetstore.controller;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.jclouds.util.Strings2.toStringAndClose;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

import org.jclouds.blobstore.BlobMap;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.BlobStoreContextFactory;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.demo.tweetstore.reference.TweetStoreConstants;
import org.testng.annotations.Test;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.User;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * Tests behavior of {@code StoreTweetsController}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class StoreTweetsControllerTest {

   Twitter createTwitter() {
      return createMock(Twitter.class);
   }

   Map<String, BlobStoreContext> createBlobStores() throws InterruptedException, ExecutionException {
      Map<String, BlobStoreContext> contexts = ImmutableMap.<String, BlobStoreContext> of("test1",
               new BlobStoreContextFactory().createContext("transient", "dummy", "dummy"), "test2",
               new BlobStoreContextFactory().createContext("transient", "dummy", "dummy"));
      for (BlobStoreContext blobstore : contexts.values()) {
         blobstore.getAsyncBlobStore().createContainerInLocation(null, "favo").get();
      }
      return contexts;
   }

   public void testStoreTweets() throws IOException, InterruptedException, ExecutionException {
      Map<String, BlobStoreContext> stores = createBlobStores();
      StoreTweetsController function = new StoreTweetsController(stores, "favo", createTwitter());

      User frank = createMock(User.class);
      expect(frank.getScreenName()).andReturn("frank").atLeastOnce();

      Status frankStatus = createMock(Status.class);
      expect(frankStatus.getId()).andReturn(1l).atLeastOnce();
      expect(frankStatus.getUser()).andReturn(frank).atLeastOnce();
      expect(frankStatus.getText()).andReturn("I love beans!").atLeastOnce();
      
      User jimmy = createMock(User.class);
      expect(jimmy.getScreenName()).andReturn("jimmy").atLeastOnce();

      Status jimmyStatus = createMock(Status.class);
      expect(jimmyStatus.getId()).andReturn(2l).atLeastOnce();
      expect(jimmyStatus.getUser()).andReturn(jimmy).atLeastOnce();
      expect(jimmyStatus.getText()).andReturn("cloud is king").atLeastOnce();

      replay(frank);
      replay(frankStatus);
      replay(jimmy);
      replay(jimmyStatus);

      function.addMyTweets("test1", ImmutableList.of(frankStatus, jimmyStatus));
      function.addMyTweets("test2", ImmutableList.of(frankStatus, jimmyStatus));

      verify(frank);
      verify(frankStatus);
      verify(jimmy);
      verify(jimmyStatus);

      for (Entry<String, BlobStoreContext> entry : stores.entrySet()) {
         BlobMap map = entry.getValue().createBlobMap("favo");
         Blob frankBlob = map.get("1");
         assertEquals(frankBlob.getMetadata().getName(), "1");
         assertEquals(frankBlob.getMetadata().getUserMetadata().get(TweetStoreConstants.SENDER_NAME), "frank");
         assertEquals(frankBlob.getMetadata().getContentMetadata().getContentType(), "text/plain");
         assertEquals(toStringAndClose(frankBlob.getPayload().getInput()), "I love beans!");

         Blob jimmyBlob = map.get("2");
         assertEquals(jimmyBlob.getMetadata().getName(), "2");
         assertEquals(jimmyBlob.getMetadata().getUserMetadata().get(TweetStoreConstants.SENDER_NAME), "jimmy");
         assertEquals(jimmyBlob.getMetadata().getContentMetadata().getContentType(), "text/plain");
         assertEquals(toStringAndClose(jimmyBlob.getPayload().getInput()), "cloud is king");
      }

   }
}
