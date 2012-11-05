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

package org.jclouds.virtualbox.compute;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_IMAGE_PREFIX;

import java.util.List;
import java.util.Map;

import org.easymock.EasyMock;
import org.jclouds.compute.config.BaseComputeServiceContextModule;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.json.Json;
import org.jclouds.json.config.GsonModule;
import org.jclouds.virtualbox.config.VirtualBoxComputeServiceContextModule;
import org.jclouds.virtualbox.functions.IMachineToImage;
import org.testng.annotations.Test;
import org.virtualbox_4_1.IGuestOSType;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.IVirtualBox;
import org.virtualbox_4_1.VirtualBoxManager;

import com.google.common.base.Function;
import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;
import com.google.inject.Guice;

@Test(groups = "unit")
public class VirtualBoxComputeServiceAdapterTest {

   Map<OsFamily, Map<String, String>> osMap = new BaseComputeServiceContextModule() {
   }.provideOsVersionMap(new ComputeServiceConstants.ReferenceData(), Guice.createInjector(new GsonModule())
         .getInstance(Json.class));

   @Test
   public void testListImages() throws Exception {

      VirtualBoxManager manager = createNiceMock(VirtualBoxManager.class);
      IVirtualBox vBox = createNiceMock(IVirtualBox.class);
      IGuestOSType osType = createNiceMock(IGuestOSType.class);

      List<IMachine> machines = Lists.newArrayList();
      IMachine imageMachine = createNiceMock(IMachine.class);
      IMachine clonedMachine = createNiceMock(IMachine.class);
      machines.add(imageMachine);
      machines.add(clonedMachine);

      expect(clonedMachine.getName()).andReturn("My Linux Node");
      expect(clonedMachine.getDescription()).andReturn("My Linux Node");
      expect(imageMachine.getName()).andReturn(VIRTUALBOX_IMAGE_PREFIX + "ubuntu-10.04");
      expect(imageMachine.getDescription()).andReturn(VIRTUALBOX_IMAGE_PREFIX + "ubuntu-10.04");

      expect(manager.getVBox()).andReturn(vBox).anyTimes();
      expect(vBox.getMachines()).andReturn(machines).anyTimes();
      expect(vBox.getGuestOSType(EasyMock.<String> anyObject())).andReturn(osType).anyTimes();
      expect(osType.getDescription()).andReturn("Ubuntu 10.04").anyTimes();
      expect(osType.getIs64Bit()).andReturn(true).anyTimes();

      replay(manager, vBox, clonedMachine, imageMachine, osType);

      Function<IMachine, Image> iMachineToImage = new IMachineToImage(
               VirtualBoxComputeServiceContextModule.toPortableImageStatus, Suppliers.ofInstance(manager), osMap);
//      VirtualBoxComputeServiceAdapter adapter = new VirtualBoxComputeServiceAdapter(Suppliers.ofInstance(manager), iMachineToImage, new ImageFromYamlString(), new Supplier<String>() {
//
//			@Override
//         public String get() {
//	         return "images:\n" + 
//	         		"    - id: myTestId\n" + 
//	         		"      name: ubuntu-11.04-server-i386\n" + 
//	         		"      description: ubuntu 11.04 server (i386)\n" + 
//	         		"      os_arch: x86\n" + 
//	         		"      os_family: ubuntu\n" + 
//	         		"      os_description: ubuntu\n" + 
//	         		"      os_version: 11.04\n" + 
//	         		"      iso: http://releases.ubuntu.com/11.04/ubuntu-11.04-server-i386.iso";
//         }
//		});
//
//      Iterator<Image> iterator = adapter.listImages().iterator();
//      Image image = Iterators.getOnlyElement(iterator);
//      assertEquals(image.getDescription(), "ubuntu 11.04 server (i386)");

   }
}
