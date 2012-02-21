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

import static org.easymock.EasyMock.*;

import java.net.URI;
import java.util.Map;

import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.BlobStoreContextFactory;
import org.jclouds.demo.paas.RunnableHttpRequest;
import org.jclouds.demo.paas.RunnableHttpRequest.Factory;
import org.jclouds.demo.paas.service.taskqueue.TaskQueue;
import org.jclouds.http.HttpRequest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;

/**
 * Tests behavior of {@code EnqueueStoresController}
 * 
 * @author Andrew Phillips
 */
@Test(groups = "unit")
public class EnqueueStoresControllerTest {

    Map<String, BlobStoreContext> createBlobStores() {
        Map<String, BlobStoreContext> contexts = ImmutableMap.of(
                "test1", new BlobStoreContextFactory().createContext("transient", "dummy", "dummy"), 
                "test2", new BlobStoreContextFactory().createContext("transient", "dummy", "dummy"));
        return contexts;
    }

    public void testEnqueueStores() {
        Map<String, BlobStoreContext> stores = createBlobStores();
        TaskQueue taskQueue = createMock(TaskQueue.class);
        Factory httpRequestFactory = createMock(Factory.class);
        EnqueueStoresController function = new EnqueueStoresController(stores, 
                taskQueue, "http://localhost:8080");

        expect(taskQueue.getHttpRequestFactory()).andStubReturn(httpRequestFactory);

        HttpRequest storeInTest1Request = HttpRequest.builder().endpoint(
                URI.create("http://localhost:8080/store/do"))
        .headers(ImmutableMultimap.of("context", "test1")).method("GET").build();
        RunnableHttpRequest storeInTest1Task = null;
        expect(httpRequestFactory.create(eq(storeInTest1Request))).andReturn(storeInTest1Task);

        HttpRequest storeInTest2Request = HttpRequest.builder().endpoint(
                URI.create("http://localhost:8080/store/do"))
        .headers(ImmutableMultimap.of("context", "test2")).method("GET").build();        
        RunnableHttpRequest storeInTest2Task = null;
        expect(httpRequestFactory.create(eq(storeInTest2Request))).andReturn(storeInTest2Task);

        taskQueue.add(storeInTest1Task);
        expectLastCall();
        taskQueue.add(storeInTest2Task);
        expectLastCall();
        replay(httpRequestFactory, taskQueue);

        function.enqueueStoreTweetTasks();

        verify(taskQueue);
    }
}