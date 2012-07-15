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
package org.jclouds.savvis.vpdc.features;

import static org.jclouds.savvis.vpdc.options.GetVMOptions.Builder.withPowerState;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.jclouds.savvis.vpdc.domain.Org;
import org.jclouds.savvis.vpdc.domain.Resource;
import org.jclouds.savvis.vpdc.domain.Task;
import org.jclouds.savvis.vpdc.domain.VDC;
import org.jclouds.savvis.vpdc.domain.VM;
import org.jclouds.savvis.vpdc.reference.VCloudMediaType;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

@Test(groups = "live")
public class ServiceManagementApiLiveTest extends BaseVPDCApiLiveTest {

   private ServiceManagementApi api;

   @Override
   @BeforeGroups(groups = { "live" })
   public void setupContext() {
      super.setupContext();
      api = restContext.getApi().getServiceManagementApi();
   }
   
   // test for a single vm, as savvis response times are very slow. So if there are multiple vpdc's with numerous vm's,
   // test execution will invariably take a long time
   public void testLifeCycle() throws InterruptedException, ExecutionException, TimeoutException, IOException {
      for (Resource org1 : restContext.getApi().listOrgs()) {
         Org org = restContext.getApi().getBrowsingApi().getOrg(org1.getId());
         VDC_LOOP : for (Resource vdc : org.getVDCs()) {
            VDC VDC = restContext.getApi().getBrowsingApi().getVDCInOrg(org.getId(), vdc.getId());
            for (Resource vmHandle : Iterables.filter(VDC.getResourceEntities(), new Predicate<Resource>() {

               @Override
               public boolean apply(Resource arg0) {
                  return VCloudMediaType.VAPP_XML.equals(arg0.getType());
               }

            })) {
            	
               Task powerOffTask = api.powerOffVM(vmHandle.getHref());
               assert taskTester.apply(powerOffTask.getId());

               VM vm = restContext.getApi().getBrowsingApi().getVM(vmHandle.getHref(), withPowerState());
               assertEquals(vm.getStatus(), VM.Status.OFF);
               
               Task powerOnTask = api.powerOnVM(vmHandle.getHref());
               assert taskTester.apply(powerOnTask.getId());

               vm = restContext.getApi().getBrowsingApi().getVM(vmHandle.getHref(), withPowerState());

               assertEquals(vm.getStatus(), VM.Status.ON);
               
               break VDC_LOOP;
            }
         }
      }
   }

}
