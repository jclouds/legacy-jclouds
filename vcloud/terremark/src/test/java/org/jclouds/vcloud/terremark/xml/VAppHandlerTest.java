/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.vcloud.terremark.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.List;

import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.vcloud.VCloudMediaType;
import org.jclouds.vcloud.domain.ResourceAllocation;
import org.jclouds.vcloud.domain.ResourceType;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.VAppStatus;
import org.jclouds.vcloud.domain.VirtualSystem;
import org.jclouds.vcloud.domain.internal.NamedResourceImpl;
import org.jclouds.vcloud.endpoints.internal.VAppRoot;
import org.jclouds.vcloud.xml.VAppHandler;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Provides;
import com.google.inject.internal.Iterables;

/**
 * Tests behavior of {@code VAppHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "vcloud.VAppHandlerTest")
public class VAppHandlerTest extends BaseHandlerTest {

   @BeforeTest
   @Override
   protected void setUpInjector() {
      injector = Guice.createInjector(new SaxParserModule(), new AbstractModule() {

         @Override
         protected void configure() {
         }

         @SuppressWarnings("unused")
         @Provides
         @VAppRoot
         String provide() {
            return "https://services.vcloudexpress.terremark.com/api/v0.8/vapp";
         }

      });
      factory = injector.getInstance(ParseSax.Factory.class);
   }

   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream("/terremark/launched_vapp.xml");

      VApp result = (VApp) factory.create(injector.getInstance(VAppHandler.class)).parse(is);
      assertEquals(result.getId(), 13775 + "");

      assertEquals(result.getName(), "adriantest");
      assertEquals(result.getStatus(), VAppStatus.UNRESOLVED);

      assertEquals(result.getSize().longValue(), 4l);

      assertEquals(result.getLocation(), URI.create("https://services.vcloudexpress.terremark.com/api/v0.8/vapp/13775"));
      assertEquals(result.getVDC(), new NamedResourceImpl("32", null, "application/vnd.vmware.vcloud.vdc+xml", URI
               .create("https://services.vcloudexpress.terremark.com/api/v0.8/vdc/32")));

   }

   public void testGetVApp() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/terremark/get_vapp.xml");

      VApp result = (VApp) factory.create(injector.getInstance(VAppHandler.class)).parse(is);
      assertEquals(result.getId(), 16238 + "");

      assertEquals(result.getName(), "centos-53");
      assertEquals(result.getStatus(), VAppStatus.OFF);

      assertEquals(result.getSize().longValue(), 10485760);
      assertEquals(result.getOperatingSystemDescription(), "Red Hat Enterprise Linux 5 (64-bit)");

      assertEquals(result.getLocation(), URI.create("https://services.vcloudexpress.terremark.com/api/v0.8/vapp/16238"));
      assertEquals(result.getVDC(), new NamedResourceImpl("32", null, VCloudMediaType.VDC_XML, URI
               .create("https://services.vcloudexpress.terremark.com/api/v0.8/vdc/32")));

      assertEquals(result.getSystem(), new VirtualSystem(0, "Virtual Hardware Family", "centos-53", "vmx-07"));
      assertEquals(result.getNetworkToAddresses().get("Internal"), ImmutableList.<String> of("10.114.34.132"));

      ResourceAllocation cpu = new ResourceAllocation(1, "1 virtual CPU(s)", "Number of Virtual CPUs",
               ResourceType.PROCESSOR, null, null, null, null, null, null, 1, "hertz * 10^6");

      ResourceAllocation controller = new ResourceAllocation(3, "SCSI Controller 0", "SCSI Controller",
               ResourceType.SCSI_CONTROLLER, "lsilogic", null, 0, null, null, null, 1, null);
      ResourceAllocation memory = new ResourceAllocation(2, "512MB of memory", "Memory Size", ResourceType.MEMORY,
               null, null, null, null, null, null, 512, "byte * 2^20");
      ResourceAllocation disk = new ResourceAllocation(9, "Hard Disk 1", null, ResourceType.DISK_DRIVE, null,
               "10485760", null, 0, 3, null, 10485760, "byte * 2^20");
      assertEquals(result.getResourceAllocations(), ImmutableSet.of(cpu, memory, controller, disk));
      assertEquals(Iterables.getOnlyElement(result.getResourceAllocationByType().get(ResourceType.PROCESSOR))
               .getVirtualQuantity(), 1);
      assertEquals(Iterables.getOnlyElement(result.getResourceAllocationByType().get(ResourceType.SCSI_CONTROLLER))
               .getVirtualQuantity(), 1);
      assertEquals(Iterables.getOnlyElement(result.getResourceAllocationByType().get(ResourceType.MEMORY))
               .getVirtualQuantity(), 512);
      assertEquals(Iterables.getOnlyElement(result.getResourceAllocationByType().get(ResourceType.DISK_DRIVE))
               .getVirtualQuantity(), 10485760);
      assertEquals(result.getSize().longValue(), Iterables.getOnlyElement(
               result.getResourceAllocationByType().get(ResourceType.DISK_DRIVE)).getVirtualQuantity());
   }

   public void testGetVApp2disks() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/terremark/get_vapp2disks.xml");

      VApp vApp = factory.create(injector.getInstance(VAppHandler.class)).parse(is);
      assertEquals(vApp.getId(), 15639 + "");

      assertEquals(vApp.getName(), "eduardo");
      assertEquals(vApp.getStatus(), VAppStatus.OFF);

      assertEquals(vApp.getSize().longValue(), 30408704);
      assertEquals(vApp.getOperatingSystemDescription(), "Ubuntu Linux (32-bit)");

      assertEquals(vApp.getLocation(), URI.create("https://services.vcloudexpress.terremark.com/api/v0.8/vapp/15639"));
      assertEquals(vApp.getVDC(), new NamedResourceImpl("32", null, VCloudMediaType.VDC_XML, URI
               .create("https://services.vcloudexpress.terremark.com/api/v0.8/vdc/32")));

      assertEquals(vApp.getSystem(), new VirtualSystem(0, "Virtual Hardware Family", "eduardo", "vmx-07"));
      assertEquals(vApp.getNetworkToAddresses().get("Internal"), ImmutableList.of("10.114.34.131"));

      ResourceAllocation cpu = new ResourceAllocation(1, "2 virtual CPU(s)", "Number of Virtual CPUs",
               ResourceType.PROCESSOR, null, null, null, null, null, null, 2, "hertz * 10^6");

      ResourceAllocation controller = new ResourceAllocation(3, "SCSI Controller 0", "SCSI Controller",
               ResourceType.SCSI_CONTROLLER, "lsilogic", null, 0, null, null, null, 1, null);
      ResourceAllocation memory = new ResourceAllocation(2, "1024MB of memory", "Memory Size", ResourceType.MEMORY,
               null, null, null, null, null, null, 1024, "byte * 2^20");
      ResourceAllocation disk = new ResourceAllocation(9, "Hard Disk 1", null, ResourceType.DISK_DRIVE, null,
               "4194304", null, 0, 3, null, 4194304, "byte * 2^20");
      ResourceAllocation disk2 = new ResourceAllocation(9, "Hard Disk 2", null, ResourceType.DISK_DRIVE, null,
               "26214400", null, 1, 3, null, 26214400, "byte * 2^20");

      assertEquals(vApp.getResourceAllocations(), ImmutableSet.of(cpu, memory, controller, disk, disk2));
      assertEquals(Iterables.getOnlyElement(vApp.getResourceAllocationByType().get(ResourceType.PROCESSOR))
               .getVirtualQuantity(), 2);
      assertEquals(Iterables.getOnlyElement(vApp.getResourceAllocationByType().get(ResourceType.SCSI_CONTROLLER))
               .getVirtualQuantity(), 1);
      assertEquals(Iterables.getOnlyElement(vApp.getResourceAllocationByType().get(ResourceType.MEMORY))
               .getVirtualQuantity(), 1024);

      // extract the disks on the vApp sorted by addressOnParent
      List<ResourceAllocation> disks = Lists.newArrayList(vApp.getResourceAllocationByType().get(
               ResourceType.DISK_DRIVE));

      assertEquals(disks.get(0).getVirtualQuantity(), 4194304);
      assertEquals(disks.get(1).getVirtualQuantity(), 26214400);

   }

}
