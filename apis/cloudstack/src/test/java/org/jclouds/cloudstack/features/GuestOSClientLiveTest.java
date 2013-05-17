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
package org.jclouds.cloudstack.features;

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jclouds.cloudstack.domain.OSType;
import org.jclouds.cloudstack.internal.BaseCloudStackClientLiveTest;
import org.jclouds.cloudstack.options.ListOSTypesOptions;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code GuestOSClientLiveTest}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "GuestOSClientLiveTest")
public class GuestOSClientLiveTest extends BaseCloudStackClientLiveTest {

   public void testListOSTypes() throws Exception {
      Set<OSType> response = client.getGuestOSClient().listOSTypes();
      assert null != response;
      assertTrue(response.size() >= 0);
      for (OSType type : response) {
         OSType newDetails = getOnlyElement(client.getGuestOSClient().listOSTypes(
               ListOSTypesOptions.Builder.id(type.getId())));
         assertEquals(type.getId(), newDetails.getId());
         checkOSType(type);
      }
   }

   public void testListOSCategories() throws Exception {
      Map<String, String> response = client.getGuestOSClient().listOSCategories();
      assert null != response;
      assertTrue(response.size() >= 0);
      for (Entry<String, String> category : response.entrySet()) {
         checkOSCategory(category);
      }
   }

   protected void checkOSCategory(Entry<String, String> category) {
      assertEquals(category, client.getGuestOSClient().getOSCategory(category.getKey()));
      assert category.getKey() != null : category;
      assert category.getValue() != null : category;
   }

   protected void checkOSType(OSType type) {
      assertEquals(type.getId(), client.getGuestOSClient().getOSType(type.getId()).getId());
      assert type.getId() != null : type;
      assert type.getOSCategoryId() != null : type;
      assert type.getDescription() != null : type;

   }

}
