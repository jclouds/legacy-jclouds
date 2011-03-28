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

package org.jclouds.savvis.vpdc.features;

import static org.jclouds.savvis.vpdc.options.GetVMOptions.Builder.withPowerState;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.jclouds.savvis.vpdc.domain.Org;
import org.jclouds.savvis.vpdc.domain.Resource;
import org.jclouds.savvis.vpdc.domain.VDC;
import org.jclouds.savvis.vpdc.domain.VM;
import org.jclouds.savvis.vpdc.reference.VCloudMediaType;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

@Test(groups = "live")
public class ServiceManagementClientLiveTest extends BaseVPDCClientLiveTest {

   private ServiceManagementClient client;

   @Override
   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      super.setupClient();
      client = restContext.getApi().getServiceManagementClient();
   }

   public void testLifeCycle() throws InterruptedException, ExecutionException, TimeoutException, IOException {
      for (Resource org1 : restContext.getApi().listOrgs()) {
         Org org = restContext.getApi().getBrowsingClient().getOrg(org1.getId());
         for (Resource vdc : org.getVDCs()) {
            VDC VDC = restContext.getApi().getBrowsingClient().getVDCInOrg(org.getId(), vdc.getId());
            for (Resource vmHandle : Iterables.filter(VDC.getResourceEntities(), new Predicate<Resource>() {

               @Override
               public boolean apply(Resource arg0) {
                  return VCloudMediaType.VAPP_XML.equals(arg0.getType());
               }

            })) {

               assert taskTester.apply(client.powerOffVM(vmHandle.getHref()).getId());

               VM vm = restContext.getApi().getBrowsingClient().getVM(vmHandle.getHref(), withPowerState());
               assertEquals(vm.getStatus(), VM.Status.OFF);

               assert taskTester.apply(client.powerOnVM(vmHandle.getHref()).getId());

               vm = restContext.getApi().getBrowsingClient().getVM(vmHandle.getHref(), withPowerState());

               assertEquals(vm.getStatus(), VM.Status.ON);

            }
         }
      }
   }

}