/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.demo.tweetstore.functions;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.jclouds.blobstore.BlobMap;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.integration.StubBlobStoreContextBuilder;
import org.jclouds.demo.tweetstore.domain.StoredTweetStatus;
import org.jclouds.demo.tweetstore.reference.TweetStoreConstants;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code KeyToStoredTweetStatus}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "tweetstore.KeyToStoredTweetStatusTest")
public class KeyToStoredTweetStatusTest {

   BlobMap createMap() throws InterruptedException, ExecutionException {
      BlobStoreContext<BlobStore> context = new StubBlobStoreContextBuilder().buildContext();
      context.getBlobStore().createContainer("test1").get();
      return context.createBlobMap("test1");
   }

   public void testStoreTweets() throws IOException, InterruptedException, ExecutionException {
      BlobMap map = createMap();
      Blob blob = map.newBlob();
      blob.getMetadata().setName("1");
      blob.getMetadata().getUserMetadata().put(TweetStoreConstants.SENDER_NAME, "frank");
      blob.setData("I love beans!");
      map.put("1", blob);
      String host = "localhost";
      String service = "stub";
      String container = "tweetstore";

      KeyToStoredTweetStatus function = new KeyToStoredTweetStatus(map, service, host, container);
      StoredTweetStatus result = function.apply("1");

      StoredTweetStatus expected = new StoredTweetStatus(service, host, container, "1", "frank",
               "I love beans!", null);

      assertEquals(result, expected);

   }
}
