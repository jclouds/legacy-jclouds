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
package org.jclouds.glesys;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.jclouds.glesys.features.BaseGleSYSAsyncClientTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code GleSYSAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "GleSYSAsyncClientTest")
public class GleSYSAsyncClientTest extends BaseGleSYSAsyncClientTest<GleSYSAsyncClient> {

   private GleSYSAsyncClient asyncClient;
   private GleSYSClient syncClient;

   public void testSync() throws SecurityException, NoSuchMethodException, InterruptedException, ExecutionException {
      assert syncClient.getServerClient() != null;
      assert syncClient.getIpClient() != null;
   }

   public void testAsync() throws SecurityException, NoSuchMethodException, InterruptedException, ExecutionException {
      assert asyncClient.getServerClient() != null;
      assert asyncClient.getIpClient() != null;
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<GleSYSAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<GleSYSAsyncClient>>() {
      };
   }

   @BeforeClass
   @Override
   protected void setupFactory() throws IOException {
      super.setupFactory();
      asyncClient = injector.getInstance(GleSYSAsyncClient.class);
      syncClient = injector.getInstance(GleSYSClient.class);
   }

   @Override
   protected void checkFilters(HttpRequest request) {

   }
}
