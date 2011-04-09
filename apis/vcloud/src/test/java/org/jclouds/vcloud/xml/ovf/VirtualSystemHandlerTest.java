/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.vcloud.xml.ovf;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseSax.Factory;
import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.vcloud.domain.ovf.OperatingSystemSection;
import org.jclouds.vcloud.domain.ovf.ResourceAllocation;
import org.jclouds.vcloud.domain.ovf.ResourceType;
import org.jclouds.vcloud.domain.ovf.System;
import org.jclouds.vcloud.domain.ovf.VCloudHardDisk;
import org.jclouds.vcloud.domain.ovf.VCloudNetworkAdapter;
import org.jclouds.vcloud.domain.ovf.VirtualHardwareSection;
import org.jclouds.vcloud.domain.ovf.VirtualSystem;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code VirtualSystemHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class VirtualSystemHandlerTest {

   public void testVCloud1_0() {
      InputStream is = getClass().getResourceAsStream("/virtualsystem.xml");
      Injector injector = Guice.createInjector(new SaxParserModule());
      Factory factory = injector.getInstance(ParseSax.Factory.class);
      VirtualSystem result = factory.create(injector.getInstance(VirtualSystemHandler.class)).parse(is);
      checkVirtualSystem(result);
   }

   static void checkVirtualSystem(VirtualSystem result) {
      assertEquals(result.getId(), "Ubuntu1004");
      assertEquals(result.getName(), "Ubuntu1004");
      assertEquals(result.getInfo(), "A virtual machine:");
      checkHardware(Iterables.get(result.getHardware(), 0));
      checkOs(result.getOperatingSystem());
   }

   @Test(enabled = false)
   public static void checkHardware(VirtualHardwareSection result) {
      assertEquals(result.getSystem(),
               new System(0, "Virtual Hardware Family", "Ubuntu1004", ImmutableSet.of("vmx-07")));
      assertEquals(result.getInfo(), "Virtual hardware requirements");

      assertEquals(Iterables.get(result.getResourceAllocations(), 0), new VCloudNetworkAdapter(1, "Network adapter 0",
               "PCNet32 ethernet adapter", ResourceType.ETHERNET_ADAPTER, "PCNet32", null, "00:50:56:8c:00:13", 0,
               null, false, 1, null, "192.168.2.100", true, "POOL"));

      assertEquals(Iterables.get(result.getResourceAllocations(), 1), new ResourceAllocation(2, "SCSI Controller 0",
               "SCSI Controller", ResourceType.SCSI_CONTROLLER, "lsilogic", null, "0", null, null, null, 1, null));

      assertEquals(Iterables.get(result.getResourceAllocations(), 2), new VCloudHardDisk(2000, "Hard disk 1",
               "Hard disk", ResourceType.DISK_DRIVE, null, null, null, 0, 2, null, 1, null, 30720, 6, "lsilogic"));

      assertEquals(Iterables.get(result.getResourceAllocations(), 3), new ResourceAllocation(3, "IDE Controller 0",
               "IDE Controller", ResourceType.IDE_CONTROLLER, null, null, "0", null, null, null, 1, null));

      assertEquals(Iterables.get(result.getResourceAllocations(), 4), new ResourceAllocation(3002, "CD/DVD Drive 1",
               "CD/DVD Drive", ResourceType.CD_DRIVE, null, null, null, 0, 3, null, 1, null));

      assertEquals(Iterables.get(result.getResourceAllocations(), 5), new ResourceAllocation(8000, "Floppy Drive 1",
               "Floppy Drive", ResourceType.FLOPPY_DRIVE, null, null, null, 0, null, null, 1, null));

      assertEquals(Iterables.get(result.getResourceAllocations(), 6), new ResourceAllocation(4, "1 virtual CPU(s)",
               "Number of Virtual CPUs", ResourceType.PROCESSOR, null, null, null, null, null, null, 1, "hertz * 10^6"));

      assertEquals(Iterables.get(result.getResourceAllocations(), 7), new ResourceAllocation(5, "512 MB of memory",
               "Memory Size", ResourceType.MEMORY, null, null, null, null, null, null, 512, "byte * 2^20"));
   }

   @Test(enabled = false)
   public static void checkOs(OperatingSystemSection result) {
      assertEquals(result.getDescription(), "Ubuntu Linux (64-bit)");
      assertEquals(result.getId(), new Integer(94));
      assertEquals(result.getInfo(), "Specifies the operating system installed");
   }
}
