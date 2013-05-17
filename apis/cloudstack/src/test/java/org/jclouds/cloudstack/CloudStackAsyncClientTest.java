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
package org.jclouds.cloudstack;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.jclouds.cloudstack.internal.BaseCloudStackAsyncClientTest;
import org.jclouds.http.HttpRequest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code CloudStackAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "CloudStackAsyncClientTest")
public class CloudStackAsyncClientTest extends BaseCloudStackAsyncClientTest<CloudStackAsyncClient> {

   private CloudStackAsyncClient asyncClient;
   private CloudStackClient syncClient;
   private CloudStackDomainAsyncClient domainAsyncClient;
   private CloudStackDomainClient domainSyncClient;
   private CloudStackGlobalAsyncClient globalAsyncClient;
   private CloudStackGlobalClient globalSyncClient;

   public void testSync() throws SecurityException, NoSuchMethodException, InterruptedException, ExecutionException {
      assert syncClient.getZoneClient() != null;
      assert syncClient.getTemplateClient() != null;
      assert syncClient.getOfferingClient() != null;
      assert syncClient.getNetworkClient() != null;
      assert syncClient.getVirtualMachineClient() != null;
      assert syncClient.getSecurityGroupClient() != null;
      assert syncClient.getAsyncJobClient() != null;
      assert syncClient.getAddressClient() != null;
      assert syncClient.getNATClient() != null;
      assert syncClient.getFirewallClient() != null;
      assert syncClient.getLoadBalancerClient() != null;
      assert syncClient.getGuestOSClient() != null;
      assert syncClient.getHypervisorClient() != null;
      assert syncClient.getConfigurationClient() != null;
      assert syncClient.getAccountClient() != null;
      assert syncClient.getSSHKeyPairClient() != null;
      assert syncClient.getVMGroupClient() != null;
      assert syncClient.getEventClient() != null;
      assert syncClient.getLimitClient() != null;
      assert syncClient.getISOClient() != null;
      assert syncClient.getVolumeClient() != null;
      assert syncClient.getSnapshotClient() != null;

      assert domainSyncClient.getLimitClient() != null;
      assert domainSyncClient.getAccountClient() != null;

      assert globalSyncClient.getAccountClient() != null;
   }

   public void testAsync() throws SecurityException, NoSuchMethodException, InterruptedException, ExecutionException {
      assert asyncClient.getZoneClient() != null;
      assert asyncClient.getTemplateClient() != null;
      assert asyncClient.getOfferingClient() != null;
      assert asyncClient.getNetworkClient() != null;
      assert asyncClient.getVirtualMachineClient() != null;
      assert asyncClient.getSecurityGroupClient() != null;
      assert asyncClient.getAsyncJobClient() != null;
      assert asyncClient.getAddressClient() != null;
      assert asyncClient.getNATClient() != null;
      assert asyncClient.getFirewallClient() != null;
      assert asyncClient.getLoadBalancerClient() != null;
      assert asyncClient.getGuestOSClient() != null;
      assert asyncClient.getHypervisorClient() != null;
      assert asyncClient.getConfigurationClient() != null;
      assert asyncClient.getAccountClient() != null;
      assert asyncClient.getSSHKeyPairClient() != null;
      assert asyncClient.getVMGroupClient() != null;
      assert asyncClient.getEventClient() != null;
      assert asyncClient.getLimitClient() != null;
      assert asyncClient.getISOClient() != null;
      assert asyncClient.getVolumeClient() != null;
      assert asyncClient.getSnapshotClient() != null;

      assert domainAsyncClient.getLimitClient() != null;
      assert domainAsyncClient.getAccountClient() != null;

      assert globalAsyncClient.getAccountClient() != null;
   }

   @BeforeClass
   @Override
   protected void setupFactory() throws IOException {
      super.setupFactory();
      asyncClient = injector.getInstance(CloudStackAsyncClient.class);
      syncClient = injector.getInstance(CloudStackClient.class);
      domainAsyncClient = injector.getInstance(CloudStackDomainAsyncClient.class);
      domainSyncClient = injector.getInstance(CloudStackDomainClient.class);
      globalAsyncClient = injector.getInstance(CloudStackGlobalAsyncClient.class);
      globalSyncClient = injector.getInstance(CloudStackGlobalClient.class);
   }

   @Override
   protected void checkFilters(HttpRequest request) {

   }
}
