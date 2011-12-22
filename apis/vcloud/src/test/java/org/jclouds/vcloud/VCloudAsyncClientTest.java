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
package org.jclouds.vcloud;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.vcloud.internal.BaseVCloudAsyncClientTest;
import org.jclouds.vcloud.utils.TestUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code VCloudAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "VCloudAsyncClientTest")
public class VCloudAsyncClientTest extends BaseVCloudAsyncClientTest<VCloudAsyncClient> {

   @Override
   protected TypeLiteral<RestAnnotationProcessor<VCloudAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<VCloudAsyncClient>>() {
      };
   }

   private VCloudAsyncClient asyncClient;
   private VCloudClient syncClient;

   public void testSync() throws SecurityException, NoSuchMethodException, InterruptedException, ExecutionException {
      assert syncClient.getVAppClient() != null;
      assert syncClient.getCatalogClient() != null;
      assert syncClient.getVmClient() != null;
      assert syncClient.getVAppTemplateClient() != null;
      assert syncClient.getTaskClient() != null;
      assert syncClient.getVDCClient() != null;
      assert syncClient.getNetworkClient() != null;
      assert syncClient.getOrgClient() != null;
   }

   public void testAsync() throws SecurityException, NoSuchMethodException, InterruptedException, ExecutionException {
      assert asyncClient.getVAppClient() != null;
      assert asyncClient.getCatalogClient() != null;
      assert asyncClient.getVmClient() != null;
      assert asyncClient.getVAppTemplateClient() != null;
      assert asyncClient.getTaskClient() != null;
      assert asyncClient.getVDCClient() != null;
      assert asyncClient.getNetworkClient() != null;
      assert asyncClient.getOrgClient() != null;
   }

   @BeforeClass
   @Override
   protected void setupFactory() throws IOException {
      super.setupFactory();
      asyncClient = injector.getInstance(VCloudAsyncClient.class);
      syncClient = injector.getInstance(VCloudClient.class);
   }

   @DataProvider
   public Object[][] ignoreOnWindows() {
      return (TestUtils.isWindowsOs() ? TestUtils.NO_INVOCATIONS 
                                      : TestUtils.SINGLE_NO_ARG_INVOCATION);
   }
   
}
