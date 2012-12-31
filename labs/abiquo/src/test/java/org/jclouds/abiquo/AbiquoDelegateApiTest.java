/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo;

import static org.testng.Assert.assertNotNull;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.jclouds.abiquo.features.BaseAbiquoAsyncApiTest;
import org.jclouds.http.HttpRequest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Tests asynchronous and synchronous API delegates.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "unit", testName = "AbiquoDelegateApiTest")
public class AbiquoDelegateApiTest extends BaseAbiquoAsyncApiTest<AbiquoAsyncApi> {
   private AbiquoAsyncApi asyncApi;

   private AbiquoApi syncApi;

   @BeforeClass
   @Override
   protected void setupFactory() throws IOException {
      super.setupFactory();
      asyncApi = injector.getInstance(AbiquoAsyncApi.class);
      syncApi = injector.getInstance(AbiquoApi.class);
   }

   public void testSync() throws SecurityException, NoSuchMethodException, InterruptedException, ExecutionException {
      assertNotNull(syncApi.getAdminApi());
      assertNotNull(syncApi.getConfigApi());
      assertNotNull(syncApi.getInfrastructureApi());
      assertNotNull(syncApi.getEnterpriseApi());
      assertNotNull(syncApi.getCloudApi());
      assertNotNull(syncApi.getVirtualMachineTemplateApi());
      assertNotNull(syncApi.getTaskApi());
      assertNotNull(syncApi.getPricingApi());
   }

   public void testAsync() throws SecurityException, NoSuchMethodException, InterruptedException, ExecutionException {
      assertNotNull(asyncApi.getAdminApi());
      assertNotNull(asyncApi.getConfigApi());
      assertNotNull(asyncApi.getInfrastructureApi());
      assertNotNull(asyncApi.getEnterpriseApi());
      assertNotNull(asyncApi.getCloudApi());
      assertNotNull(asyncApi.getVirtualMachineTemplateApi());
      assertNotNull(asyncApi.getTaskApi());
      assertNotNull(asyncApi.getPricingApi());
   }

   @Override
   protected void checkFilters(final HttpRequest request) {

   }
}
