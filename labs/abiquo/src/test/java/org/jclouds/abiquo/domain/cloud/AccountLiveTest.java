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

package org.jclouds.abiquo.domain.cloud;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.jclouds.abiquo.domain.enterprise.Enterprise;
import org.jclouds.abiquo.internal.BaseAbiquoLiveApiTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Live integration tests
 * 
 * @author Susana Acedo
 */
@Test(groups = "live", testName = "AccountLiveTest")
public class AccountLiveTest extends BaseAbiquoLiveApiTest {

   private Enterprise enterprise;

   @BeforeClass(groups = { "integration", "live" })
   @Override
   public void setupContext() {
      super.setupContext();
      enterprise = view.getAdministrationService().getCurrentEnterprise();
   }

   public void testGetCurrentUser() {
      assertEquals(view.getAdministrationService().getCurrentUser().getNick(), view.getApiContext().getIdentity());
   }

   public void testAllowedDatacenters() {
      assertTrue(enterprise.listAllowedDatacenters().size() > 0);
   }

   public void testAvailableTemplates() {
      assertTrue(enterprise.listTemplates().size() > 0);
   }

}
