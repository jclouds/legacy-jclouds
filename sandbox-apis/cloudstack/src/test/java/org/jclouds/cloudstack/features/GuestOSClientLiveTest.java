/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.cloudstack.features;

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.cloudstack.domain.OSType;
import org.jclouds.cloudstack.options.ListOSTypesOptions;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code GuestOSClientLiveTest}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "GuestOSClientLiveTest")
public class GuestOSClientLiveTest extends BaseCloudStackClientLiveTest {

   public void testListOSTypes() throws Exception {
      Set<OSType> response = client.getGuestOSClient().listOSTypes();
      assert null != response;
      assertTrue(response.size() >= 0);
      for (OSType type : response) {
         OSType newDetails = getOnlyElement(client.getGuestOSClient().listOSTypes(
               ListOSTypesOptions.Builder.id(type.getId())));
         assertEquals(type.getId(), newDetails.getId());
         checkIP(type);
      }
   }

   protected void checkIP(OSType type) {
      assertEquals(type.getId(), client.getGuestOSClient().getOSType(type.getId()).getId());
      assert type.getId() > 0 : type;
      assert type.getOSCategoryId() > 0 : type;
      assert type.getDescription() != null : type;

   }

}
