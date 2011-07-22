/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.terremark.ecloud.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.jclouds.terremark.ecloud.BaseTerremarkECloudClientLiveTest;
import org.jclouds.terremark.ecloud.domain.TerremarkECloudOrg;
import org.jclouds.vcloud.domain.ReferenceType;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", enabled = true, singleThreaded = true, testName = "TagOperationsClientLiveTest")
public class TagOperationsClientLiveTest extends BaseTerremarkECloudClientLiveTest {
   @Test
   public void testListTagsInOrg() throws Exception {
      for (ReferenceType response : getApi().listOrgs().values()) {
         TerremarkECloudOrg org = getApi().getOrg(response.getHref());
         assertNotNull(response);
         assertNotNull(response.getName());
         assertNotNull(response.getHref());
         assertEquals(getApi().getTagOperationsClient().getTagNameToUsageCountInOrg(org.getHref()), getApi()
               .getTagOperationsClient().getTagNameToUsageCount(org.getTags().getHref()));
      }
   }
}