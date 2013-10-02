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
package org.jclouds.apis;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.util.NoSuchElementException;

import org.testng.annotations.Test;

/**
 * The ApisTest tests the org.jclouds.apis.Apis class.
 * 
 * @author Jeremy Whitlock <jwhitlock@apache.org>
 */
@Test(groups = "unit", testName = "ApisTest")
public class ApisTest {

   private final JcloudsTestBlobStoreApiMetadata testBlobstoreApi = new JcloudsTestBlobStoreApiMetadata();
   private final JcloudsTestComputeApiMetadata testComputeApi = new JcloudsTestComputeApiMetadata();
   private final JcloudsTestYetAnotherComputeApiMetadata testYetAnotherComputeApi = new JcloudsTestYetAnotherComputeApiMetadata();

   @Test
   public void testWithId() {

      ApiMetadata apiMetadata;
      try {
         apiMetadata = Apis.withId("fake-id");
         fail("Looking for a api with an id that doesn't exist should " + "throw an exception.");
      } catch (NoSuchElementException nsee) {
         ; // Expected
      }

      apiMetadata = Apis.withId(testBlobstoreApi.getId());

      assertEquals(testBlobstoreApi, apiMetadata);
   }

   @Test
   public void testTransformableTo() {
      Iterable<ApiMetadata> apisMetadata = Apis.viewableAs(Storage.class);

      for (ApiMetadata apiMetadata : apisMetadata) {
         assertEquals(testBlobstoreApi, apiMetadata);
      }

      apisMetadata = Apis.viewableAs(Compute.class);

      for (ApiMetadata apiMetadata : apisMetadata) {
         if (apiMetadata.getName().equals(testComputeApi.getName())) {
            assertEquals(testComputeApi, apiMetadata);
         } else {
            assertEquals(testYetAnotherComputeApi, apiMetadata);
         }
      }

      apisMetadata = Apis.viewableAs(Balancer.class);

      assertEquals(false, apisMetadata.iterator().hasNext());
   }

   @Test
   public void testAll() {
      Iterable<ApiMetadata> apisMetadata = Apis.all();

      for (ApiMetadata apiMetadata : apisMetadata) {
         if (apiMetadata.getName().equals(testBlobstoreApi.getName())) {
            assertEquals(testBlobstoreApi, apiMetadata);
         } else if (apiMetadata.getName().equals(testComputeApi.getName())) {
            assertEquals(testComputeApi, apiMetadata);
         } else {
            assertEquals(testYetAnotherComputeApi, apiMetadata);
         }
      }
   }

}
