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

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.Map;

import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.BlobStoreContextBuilder;
import org.jclouds.blobstore.TransientApiMetadata;
import org.testng.annotations.Test;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.common.collect.ImmutableMap;

/**
 * Tests behavior of {@link EnqueueStoresController}
 * 
 * @author Andrew Phillips
 */
@Test(groups = "unit")
public class EnqueueStoresControllerTest {

    Map<String, BlobStoreContext<?, ?>> createBlobStores() {
        TransientApiMetadata transientApiMetadata = TransientApiMetadata.builder().build();
        Map<String, BlobStoreContext<?, ?>> contexts = ImmutableMap.<String, BlobStoreContext<?, ?>>of(
                "test1", BlobStoreContextBuilder.newBuilder(transientApiMetadata).build(), 
                "test2", BlobStoreContextBuilder.newBuilder(transientApiMetadata).build());
        return contexts;
    }

    public void testEnqueueStores() {
        Map<String, BlobStoreContext<?, ?>> stores = createBlobStores();
        Queue taskQueue = createMock(Queue.class);
        EnqueueStoresController function = new EnqueueStoresController(stores, taskQueue);

        expect(taskQueue.add(withUrl("/store/do").header("context", "test1").method(Method.GET))).andReturn(null);
        expect(taskQueue.add(withUrl("/store/do").header("context", "test2").method(Method.GET))).andReturn(null);
        replay(taskQueue);

        function.enqueueStoreTweetTasks();

        verify(taskQueue);
    }
}
