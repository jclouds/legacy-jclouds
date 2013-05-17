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

import static org.testng.Assert.assertEquals;

import org.jclouds.cloudstack.domain.Account;
import org.jclouds.cloudstack.domain.User;
import org.jclouds.cloudstack.internal.BaseCloudStackClientLiveTest;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code AccountClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "AccountClientLiveTest")
public class AccountClientLiveTest extends BaseCloudStackClientLiveTest {

   @Test
   public void testListAccounts() throws Exception {
      for (Account securityAccount : client.getAccountClient().listAccounts())
         checkAccount(securityAccount);
   }

   protected void checkAccount(Account account) {
      assert account.getId() != null : account;
      assertEquals(account.toString(), client.getAccountClient().getAccount(account.getId()).toString());
      assert account.getName() != null : account;
      assert account.getType() != null && account.getType() != Account.Type.UNRECOGNIZED : account;
      assert account.getDomain() != null : account;
      assert account.getDomainId() != null : account;
      assert account.getUsers() != null : account;
      for (User user : account.getUsers()) {
         assert user.getName() != null : user;
         assert user.getAccountType().equals(account.getType()) : user;
         assert user.getDomain().equals(account.getDomain()) : user;
         assert user.getDomainId().equals(account.getDomainId()) : user;
         assert user.getCreated() != null : user;
         assert user.getEmail() != null : user;
         assert user.getLastName() != null : user;
         assert user.getFirstName() != null : user;
         assert user.getId() != null : user;
         assert user.getState() != null : user;
      }
      assert account.getIPsAvailable() == null || account.getIPsAvailable() >= 0 : account;
      assert account.getIPLimit() == null || account.getIPLimit() >= 0 : account;
      assert account.getIPs() >= 0 : account;
      assert account.getReceivedBytes() >= 0 : account;
      assert account.getSentBytes() >= 0 : account;
      assert account.getSnapshotsAvailable() == null || account.getSnapshotsAvailable() >= 0 : account;
      assert account.getSnapshotLimit() == null || account.getSnapshotLimit() >= 0 : account;
      assert account.getSnapshots() >= 0 : account;
      assert account.getState() != null && account.getState() != Account.State.UNRECOGNIZED : account;
      assert account.getTemplatesAvailable() == null || account.getTemplatesAvailable() >= 0 : account;
      assert account.getTemplateLimit() == null || account.getTemplateLimit() >= 0 : account;
      assert account.getTemplates() >= 0 : account;
      assert account.getVMsAvailable() == null || account.getVMsAvailable() >= 0 : account;
      assert account.getVMLimit() == null || account.getVMLimit() >= 0 : account;
      assert account.getVMsRunning() >= 0 : account;
      assert account.getVMsStopped() >= 0 : account;
      assert account.getVMs() >= 0 : account;
      assert account.getVolumesAvailable() == null || account.getVolumesAvailable() >= 0 : account;
      assert account.getVolumeLimit() == null || account.getVolumeLimit() >= 0 : account;
      assert account.getVolumes() >= 0 : account;
   }

}
