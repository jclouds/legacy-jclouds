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

package org.jclouds.vsphere.functions;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Map;

import org.jclouds.compute.config.BaseComputeServiceContextModule;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.json.Json;
import org.jclouds.json.config.GsonModule;
import org.jclouds.vsphere.config.VSphereComputeServiceContextModule;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.vmware.vim25.VirtualMachineConfigInfo;
import com.vmware.vim25.VirtualMachinePowerState;
import com.vmware.vim25.VirtualMachineRuntimeInfo;
import com.vmware.vim25.mo.VirtualMachine;

@Test(groups = "unit", testName = "VirtualMachineToImageTest")
public class VirtualMachineToImageTest {

   Map<OsFamily, Map<String, String>> map = new BaseComputeServiceContextModule() {
   }.provideOsVersionMap(new ComputeServiceConstants.ReferenceData(), Guice.createInjector(new GsonModule())
            .getInstance(Json.class));

   @Test
   public void testConvert() throws Exception {

      VirtualMachine vm = createNiceMock(VirtualMachine.class);      
      VirtualMachineConfigInfo configInfo = createNiceMock(VirtualMachineConfigInfo.class);
      VirtualMachineRuntimeInfo virtualMachineRuntimeInfo = createNiceMock(VirtualMachineRuntimeInfo.class);
      String description = "Ubuntu 12.04";
      expect(vm.getName()).andReturn(description).anyTimes();
      expect(vm.getConfig()).andReturn(configInfo).anyTimes();
      expect(configInfo.getAnnotation()).andReturn(description).anyTimes();
      expect(vm.getRuntime()).andReturn(virtualMachineRuntimeInfo).anyTimes();
      expect(virtualMachineRuntimeInfo.getPowerState()).andReturn(VirtualMachinePowerState.poweredOff).anyTimes();
      
      replay(vm, configInfo, virtualMachineRuntimeInfo);

      VirtualMachineToImage fn = new VirtualMachineToImage(VSphereComputeServiceContextModule.toPortableImageStatus, map);

      Image image = fn.apply(vm);

      assertTrue(image.getOperatingSystem().is64Bit());
      assertEquals(image.getOperatingSystem().getFamily(), OsFamily.UBUNTU);
      assertEquals(image.getOperatingSystem().getVersion(), "12.04");
      assertEquals(image.getId(), description);
      assertEquals(image.getStatus(), Image.Status.AVAILABLE);

   }
   
}
