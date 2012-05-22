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
package org.jclouds.demo.tweetstore.controller;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.TransientApiMetadata;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.demo.tweetstore.reference.TweetStoreConstants;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

/**
 * Tests behavior of {@code AddTweetsController}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ClearTweetsControllerTest {

    Map<String, BlobStoreContext> createBlobStores(String container) throws InterruptedException, ExecutionException {
        TransientApiMetadata transientApiMetadata = TransientApiMetadata.builder().build();
        Map<String, BlobStoreContext> contexts = ImmutableMap.<String, BlobStoreContext>of(
                        "test1", ContextBuilder.newBuilder(transientApiMetadata).build(BlobStoreContext.class),
                        "test2", ContextBuilder.newBuilder(transientApiMetadata).build(BlobStoreContext.class));
        for (BlobStoreContext blobstore : contexts.values()) {
            blobstore.getBlobStore().createContainerInLocation(null, container);
            Blob blob = blobstore.getAsyncBlobStore().blobBuilder("1").build();
            blob.getMetadata().getUserMetadata().put(TweetStoreConstants.SENDER_NAME, "frank");
            blob.setPayload("I love beans!");
            blobstore.getBlobStore().putBlob(container, blob);
        }
        return contexts;
    }

    public void testClearTweets() throws IOException, InterruptedException, ExecutionException {
        String container = ClearTweetsControllerTest.class.getName() + "#container";
        Map<String, BlobStoreContext> contexts = createBlobStores(container);

        ClearTweetsController controller = new ClearTweetsController(contexts,
                container);
        controller.clearContainer("test1");
        controller.clearContainer("test2");

        for (BlobStoreContext context : contexts.values()) {
            assertEquals(context.getBlobStore().countBlobs(container), 0, context.toString());
        }
    }
}
