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
package org.jclouds.openstack.nova.v1_1;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.jclouds.http.HttpRequest;
import org.jclouds.openstack.nova.v1_1.features.BaseNovaAsyncClientTest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code NovaAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "NovaAsyncClientTest")
public class NovaAsyncClientTest extends BaseNovaAsyncClientTest<NovaAsyncClient> {

   private NovaAsyncClient asyncClient;
   private NovaClient syncClient;

   public void testSync() throws SecurityException, NoSuchMethodException, InterruptedException, ExecutionException {
      assert syncClient.getServerClient() != null;
   }

   public void testAsync() throws SecurityException, NoSuchMethodException, InterruptedException, ExecutionException {
      assert asyncClient.getServerClient() != null;
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<NovaAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<NovaAsyncClient>>() {
      };
   }

   @BeforeClass
   @Override
   protected void setupFactory() throws IOException {
      super.setupFactory();
      asyncClient = injector.getInstance(NovaAsyncClient.class);
      syncClient = injector.getInstance(NovaClient.class);
   }

   @Override
   protected void checkFilters(HttpRequest request) {

   }
}
