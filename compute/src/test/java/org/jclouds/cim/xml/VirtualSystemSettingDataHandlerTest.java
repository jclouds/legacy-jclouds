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
package org.jclouds.cim.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.cim.ResourceAllocationSettingData;
import org.jclouds.cim.VirtualSystemSettingData;
import org.jclouds.cim.ResourceAllocationSettingData.ResourceType;
import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseSax.Factory;
import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.ovf.OperatingSystemSection;
import org.jclouds.ovf.VirtualHardwareSection;
import org.jclouds.ovf.VirtualSystem;
import org.jclouds.ovf.internal.BaseVirtualSystem;
import org.jclouds.ovf.xml.VirtualSystemHandler;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code VirtualSystemSettingDataHandler}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "VirtualSystemSettingDataHandlerTest")
public class VirtualSystemSettingDataHandlerTest extends BaseHandlerTest {

   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream("/virtualsystem-hosting.xml");

      VirtualSystemSettingData result = factory.create(injector.getInstance(VirtualSystemSettingDataHandler.class))
               .parse(is);

      VirtualSystemSettingData expects = VirtualSystemSettingData.builder().instanceID("0").elementName(
               "Virtual Hardware Family").virtualSystemIdentifier("SimpleVM").virtualSystemType("vmx-04").build();
      assertEquals(result, expects);

   }

   public void testVCloud1_0() {
      InputStream is = getClass().getResourceAsStream("/virtualsystem.xml");
      Injector injector = Guice.createInjector(new SaxParserModule());
      Factory factory = injector.getInstance(ParseSax.Factory.class);
      VirtualSystem result = factory.create(injector.getInstance(VirtualSystemHandler.class)).parse(is);
      checkVirtualSystem(result);
   }

   @Test(enabled = false)
   public static <T extends BaseVirtualSystem<T>> void checkVirtualSystem(BaseVirtualSystem<T> result) {
      assertEquals(result.getId(), "Ubuntu1004");
      assertEquals(result.getName(), "Ubuntu1004");
      assertEquals(result.getInfo(), "A virtual machine:");
      checkHardware(Iterables.get(result.getVirtualHardwareSections(), 0));
      checkOs(result.getOperatingSystemSection());
   }

   @Test(enabled = false)
   public static void checkHardware(VirtualHardwareSection result) {
      assertEquals(result.getSystem(), VirtualSystemSettingData.builder().instanceID("0").elementName(
               "Virtual Hardware Family").virtualSystemIdentifier("Ubuntu1004").virtualSystemType("vmx-07").build());
      assertEquals(result.getInfo(), "Virtual hardware requirements");

      assertEquals(Iterables.get(result.getItems(), 0).toString(), ResourceAllocationSettingData
               .builder().instanceID("1").elementName("Network adapter 0").description("PCNet32 ethernet adapter")
               .resourceType(ResourceType.ETHERNET_ADAPTER).resourceSubType("PCNet32").address("00:50:56:8c:00:13")
               .automaticAllocation(true).connection("vAppNet-vApp Internal").addressOnParent("0").build().toString());

      assertEquals(Iterables.get(result.getItems(), 1).toString(), ResourceAllocationSettingData
               .builder().instanceID("2").elementName("SCSI Controller 0").description("SCSI Controller").resourceType(
                        ResourceType.PARALLEL_SCSI_HBA).resourceSubType("lsilogic").address("0").build().toString());

      assertEquals(Iterables.get(result.getItems(), 2).toString(), ResourceAllocationSettingData
               .builder().instanceID("2000").elementName("Hard disk 1").description("Hard disk").resourceType(
                        ResourceType.DISK_DRIVE).addressOnParent("0").parent("2").build().toString());

      assertEquals(Iterables.get(result.getItems(), 3).toString(), ResourceAllocationSettingData
               .builder().instanceID("3").elementName("IDE Controller 0").description("IDE Controller").resourceType(
                        ResourceType.IDE_CONTROLLER).address("0").build().toString());

      assertEquals(Iterables.get(result.getItems(), 4).toString(), ResourceAllocationSettingData
               .builder().instanceID("3002").elementName("CD/DVD Drive 1").description("CD/DVD Drive").resourceType(
                        ResourceType.CD_DRIVE).addressOnParent("0").parent("3").automaticAllocation(false).build()
               .toString());

      assertEquals(Iterables.get(result.getItems(), 5).toString(), ResourceAllocationSettingData
               .builder().instanceID("8000").elementName("Floppy Drive 1").description("Floppy Drive").resourceType(
                        ResourceType.FLOPPY_DRIVE).addressOnParent("0").automaticAllocation(false).build().toString());

      assertEquals(Iterables.get(result.getItems(), 6).toString(), ResourceAllocationSettingData
               .builder().instanceID("4").elementName("1 virtual CPU(s)").description("Number of Virtual CPUs")
               .resourceType(ResourceType.PROCESSOR).virtualQuantity(1l).allocationUnits("hertz * 10^6")
               .reservation(0l).weight(0).build().toString());

      assertEquals(Iterables.get(result.getItems(), 7).toString(), ResourceAllocationSettingData
               .builder().instanceID("5").elementName("512 MB of memory").description("Memory Size").resourceType(
                        ResourceType.MEMORY).virtualQuantity(512l).allocationUnits("byte * 2^20").reservation(0l)
               .weight(0).build().toString());
   }

   @Test(enabled = false)
   public static void checkOs(OperatingSystemSection result) {
      assertEquals(result.getDescription(), "Ubuntu Linux (64-bit)");
      assertEquals(result.getId(), Integer.valueOf(94));
      assertEquals(result.getInfo(), "Specifies the operating system installed");
   }
}
