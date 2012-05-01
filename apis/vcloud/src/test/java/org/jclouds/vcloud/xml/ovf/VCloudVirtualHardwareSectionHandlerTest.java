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
package org.jclouds.vcloud.xml.ovf;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;
import java.net.UnknownHostException;

import org.jclouds.cim.ResourceAllocationSettingData;
import org.jclouds.cim.ResourceAllocationSettingData.ResourceType;
import org.jclouds.cim.VirtualSystemSettingData;
import org.jclouds.cim.xml.ResourceAllocationSettingDataHandler;
import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.vcloud.domain.internal.ReferenceTypeImpl;
import org.jclouds.vcloud.domain.ovf.EditableResourceAllocationSettingData;
import org.jclouds.vcloud.domain.ovf.VCloudHardDisk;
import org.jclouds.vcloud.domain.ovf.VCloudNetworkAdapter;
import org.jclouds.vcloud.domain.ovf.VCloudVirtualHardwareSection;
import org.jclouds.vcloud.xml.VCloudVirtualHardwareHandler;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.inject.Guice;

/**
 * Tests behavior of {@code VCloudVirtualHardwareSectionHandler}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "VCloudVirtualHardwareSectionHandlerTest")
public class VCloudVirtualHardwareSectionHandlerTest extends BaseHandlerTest {
   @BeforeTest
   @Override
   protected void setUpInjector() {
      injector = Guice.createInjector(new SaxParserModule() {
         public void configure() {
            super.configure();
            bind(ResourceAllocationSettingDataHandler.class).to(VCloudResourceAllocationSettingDataHandler.class);
         }
      });
      factory = injector.getInstance(ParseSax.Factory.class);
      assert factory != null;
   }

   public void testDefault() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/virtualhardwaresection.xml");

      VCloudVirtualHardwareSection result = factory.create(injector.getInstance(VCloudVirtualHardwareHandler.class))
               .parse(is);

      checkHardware(result);

   }

   @Test(enabled = false)
   public static void checkHardware(VCloudVirtualHardwareSection result) {
      VirtualSystemSettingData system = VirtualSystemSettingData.builder().instanceID("0").elementName(
               "Virtual Hardware Family").virtualSystemIdentifier("RHEL5").virtualSystemType("vmx-07").build();

      assertEquals(result.getHref(), URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/vApp/vm-2087535248/virtualHardwareSection/"));
      assertEquals(result.getType(), "application/vnd.vmware.vcloud.virtualHardwareSection+xml");
      assertEquals(result.getSystem().toString(), system.toString());
      assertEquals(result.getInfo(), "Virtual hardware requirements");

      assertEquals(Iterables.get(result.getItems(), 0).toString(), VCloudNetworkAdapter.builder()
               .instanceID("1").elementName("Network adapter 0").description("PCNet32 ethernet adapter").resourceType(
                        ResourceType.ETHERNET_ADAPTER).resourceSubType("PCNet32").automaticAllocation(true).connection(
                        "internet01").address("00:50:56:01:01:f2").addressOnParent("0").ipAddress("174.47.101.164")
               .primaryNetworkConnection(true).ipAddressingMode("POOL").build().toString());

      assertEquals(Iterables.get(result.getItems(), 1).toString(), ResourceAllocationSettingData
               .builder().instanceID("2").elementName("SCSI Controller 0").description("SCSI Controller").resourceType(
                        ResourceType.PARALLEL_SCSI_HBA).resourceSubType("lsilogic").address("0").build().toString());

      assertEquals(Iterables.get(result.getItems(), 2).toString(), VCloudHardDisk.builder().instanceID(
               "2000").elementName("Hard disk 1").description("Hard disk").resourceType(ResourceType.DISK_DRIVE)
               .addressOnParent("0").parent("2").capacity(30720).busType(6).busSubType("lsilogic").build().toString());

      assertEquals(Iterables.get(result.getItems(), 3).toString(), ResourceAllocationSettingData
               .builder().instanceID("3").elementName("IDE Controller 0").description("IDE Controller").resourceType(
                        ResourceType.IDE_CONTROLLER).address("0").build().toString());

      assertEquals(Iterables.get(result.getItems(), 4).toString(), ResourceAllocationSettingData
               .builder().instanceID("3002").elementName("CD/DVD Drive 1").description("CD/DVD Drive").resourceType(
                        ResourceType.CD_DRIVE).addressOnParent("0").automaticAllocation(false).parent("3").build()
               .toString());

      assertEquals(Iterables.get(result.getItems(), 5).toString(), ResourceAllocationSettingData
               .builder().instanceID("8000").elementName("Floppy Drive 1").description("Floppy Drive").resourceType(
                        ResourceType.FLOPPY_DRIVE).addressOnParent("0").automaticAllocation(false).build().toString());

      assertEquals(
               Iterables.get(result.getItems(), 6).toString(),
               EditableResourceAllocationSettingData
                        .builder()
                        .instanceID("4")
                        .elementName("1 virtual CPU(s)")
                        .description("Number of Virtual CPUs")
                        .resourceType(ResourceType.PROCESSOR)
                        .virtualQuantity(1l)
                        .allocationUnits("hertz * 10^6")
                        .reservation(0l)
                        .weight(0)
                        .edit(
                                 new ReferenceTypeImpl(
                                          null,
                                          "application/vnd.vmware.vcloud.rasdItem+xml",
                                          URI
                                                   .create("https://vcenterprise.bluelock.com/api/v1.0/vApp/vm-2087535248/virtualHardwareSection/cpu")))
                        .build().toString());

      assertEquals(
               Iterables.get(result.getItems(), 7).toString(),
               EditableResourceAllocationSettingData
                        .builder()
                        .instanceID("5")
                        .elementName("384 MB of memory")
                        .description("Memory Size")
                        .resourceType(ResourceType.MEMORY)
                        .virtualQuantity(384l)
                        .allocationUnits("byte * 2^20")
                        .reservation(0l)
                        .weight(0)
                        .edit(
                                 new ReferenceTypeImpl(
                                          null,
                                          "application/vnd.vmware.vcloud.rasdItem+xml",
                                          URI
                                                   .create("https://vcenterprise.bluelock.com/api/v1.0/vApp/vm-2087535248/virtualHardwareSection/memory")))
                        .build().toString());
   }
}
