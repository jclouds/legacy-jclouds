/*
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
package org.jclouds.opsource.servers.features;

import org.jclouds.opsource.servers.domain.Account;
import org.jclouds.opsource.servers.domain.DataCentersList;
import org.jclouds.opsource.servers.internal.BaseOpSourceServersClientLiveTest;
import org.testng.annotations.Test;

/**
 * Tests live behavior of {@link AccountClient}.
 * 
 * @author Adrian Cole
 */
@Test(groups = { "live" }, singleThreaded = true, testName = "AccountClientLiveTest")
public class AccountClientLiveTest extends BaseOpSourceServersClientLiveTest {

   public void testGetMyAccount() {
      Account account = restContext.getApi().getAccountClient().getMyAccount();
      assert account.getOrgId() != null;
   }
   
   public void testGetDataCenterWithLimits() {
	  Account account = restContext.getApi().getAccountClient().getMyAccount();
	  assert account.getOrgId() != null;
      DataCentersList dataCentersList = restContext.getApi().getAccountClient().getDataCentersWithLimits(account.getOrgId());
      assert dataCentersList != null;
   }

}
