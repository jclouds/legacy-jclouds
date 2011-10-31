/**
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
package org.jclouds.cloudstack;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.jclouds.cloudstack.features.BaseCloudStackAsyncClientTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.TypeLiteral;

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
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<CloudStackAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<CloudStackAsyncClient>>() {
      };
   }

   @BeforeClass
   @Override
   protected void setupFactory() throws IOException {
      super.setupFactory();
      asyncClient = injector.getInstance(CloudStackAsyncClient.class);
      syncClient = injector.getInstance(CloudStackClient.class);
   }

   @Override
   protected void checkFilters(HttpRequest request) {

   }
}
