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
package org.jclouds.softlayer;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.jclouds.http.HttpRequest;
import org.jclouds.softlayer.features.BaseSoftLayerApiTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code SoftLayerApi}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "SoftLayerApiTest")
public class SoftLayerApiTest extends BaseSoftLayerApiTest<SoftLayerApi> {

   private SoftLayerApi syncClient;

   public void testSync() throws SecurityException, NoSuchMethodException, InterruptedException, ExecutionException {
      assert syncClient.getVirtualGuestApi() != null;
      assert syncClient.getDatacenterApi() != null;
      assert syncClient.getProductPackageApi() != null;
   }

   @BeforeClass
   @Override
   protected void setupFactory() throws IOException {
      super.setupFactory();
      syncClient = injector.getInstance(SoftLayerApi.class);
   }

   @Override
   protected void checkFilters(HttpRequest request) {

   }
}
