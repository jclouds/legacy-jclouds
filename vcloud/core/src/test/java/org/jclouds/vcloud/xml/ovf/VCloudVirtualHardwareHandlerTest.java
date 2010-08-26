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

package org.jclouds.vcloud.xml.ovf;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;
import java.net.UnknownHostException;

import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.vcloud.domain.internal.ReferenceTypeImpl;
import org.jclouds.vcloud.domain.ovf.EditableResourceAllocation;
import org.jclouds.vcloud.domain.ovf.ResourceAllocation;
import org.jclouds.vcloud.domain.ovf.ResourceType;
import org.jclouds.vcloud.domain.ovf.System;
import org.jclouds.vcloud.domain.ovf.VCloudHardDisk;
import org.jclouds.vcloud.domain.ovf.VCloudNetworkAdapter;
import org.jclouds.vcloud.domain.ovf.VCloudVirtualHardware;
import org.jclouds.vcloud.xml.VCloudVirtualHardwareHandler;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Tests behavior of {@code VCloudVirtualHardwareHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "vcloud.VCloudVirtualHardwareHandlerTest")
public class VCloudVirtualHardwareHandlerTest extends BaseHandlerTest {

   public void testDefault() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/virtualhardwaresection.xml");

      VCloudVirtualHardware result = factory.create(injector.getInstance(VCloudVirtualHardwareHandler.class)).parse(is);

      checkHardware(result);

   }

   @Test(enabled = false)
   public static void checkHardware(VCloudVirtualHardware result) {
      System system = new System(0, "Virtual Hardware Family", "RHEL5", ImmutableSet.of("vmx-07"));

      assertEquals(result.getHref(), URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/vApp/vm-2087535248/virtualHardwareSection/"));
      assertEquals(result.getType(), "application/vnd.vmware.vcloud.virtualHardwareSection+xml");
      assertEquals(result.getSystem(), system);
      assertEquals(result.getInfo(), "Virtual hardware requirements");

      assertEquals(Iterables.get(result.getResourceAllocations(), 0), new VCloudNetworkAdapter(1, "Network adapter 0",
               "PCNet32 ethernet adapter", ResourceType.ETHERNET_ADAPTER, "PCNet32", null, "00:50:56:01:01:f2", 0,
               null, false, 1, null, "174.47.101.164", true, "POOL"));

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

      assertEquals(
               Iterables.get(result.getResourceAllocations(), 6),
               new EditableResourceAllocation(
                        4,
                        "1 virtual CPU(s)",
                        "Number of Virtual CPUs",
                        ResourceType.PROCESSOR,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        1,
                        "hertz * 10^6",
                        new ReferenceTypeImpl(
                                 null,
                                 "application/vnd.vmware.vcloud.rasdItem+xml",
                                 URI
                                          .create("https://vcenterprise.bluelock.com/api/v1.0/vApp/vm-2087535248/virtualHardwareSection/cpu"))));

      assertEquals(
               Iterables.get(result.getResourceAllocations(), 7),
               new EditableResourceAllocation(
                        5,
                        "384 MB of memory",
                        "Memory Size",
                        ResourceType.MEMORY,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        384,
                        "byte * 2^20",
                        new ReferenceTypeImpl(
                                 null,
                                 "application/vnd.vmware.vcloud.rasdItem+xml",
                                 URI
                                          .create("https://vcenterprise.bluelock.com/api/v1.0/vApp/vm-2087535248/virtualHardwareSection/memory"))));
   }
}
