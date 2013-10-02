/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.rest.annotationparsing;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.jclouds.providers.AnonymousProviderMetadata.forClientMappedToAsyncClientOnEndpoint;

import java.io.Closeable;
import java.io.IOException;

import org.jclouds.ContextBuilder;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.providers.ProviderMetadata;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Module;

/**
 * ensures that jclouds can be operated w/o reference to a context as the Api
 * itself is closeable.
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ClosableApiTest")
public class ClosableApiTest {

   static interface DelegatingApi extends Closeable {
   }

   static interface DelegatingAsyncApi extends Closeable {
   }

   ProviderMetadata provider = forClientMappedToAsyncClientOnEndpoint(DelegatingApi.class, DelegatingAsyncApi.class,
         "http://mock");

   public void testApiClosesExecutorServiceOnClose() throws IOException {
      ListeningExecutorService executor = createMock(ListeningExecutorService.class);

      expect(executor.shutdownNow()).andReturn(ImmutableList.<Runnable> of()).atLeastOnce();

      replay(executor);

      DelegatingApi api = ContextBuilder.newBuilder(provider)
                                        .modules(ImmutableSet.<Module> builder()
                                                             .add(new ExecutorServiceModule(executor, executor))
                                                             .build())
                                        .buildApi(DelegatingApi.class);
      api.close();
      verify(executor);
   }
}
