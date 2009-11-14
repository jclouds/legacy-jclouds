/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.vcloud.terremark.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

import javax.ws.rs.core.MediaType;

import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.rest.domain.internal.LinkImpl;
import org.jclouds.vcloud.VCloudMediaType;
import org.jclouds.vcloud.domain.VAppStatus;
import org.jclouds.vcloud.endpoints.internal.VAppRoot;
import org.jclouds.vcloud.terremark.domain.ResourceAllocation;
import org.jclouds.vcloud.terremark.domain.ResourceType;
import org.jclouds.vcloud.terremark.domain.VApp;
import org.jclouds.vcloud.terremark.domain.VirtualSystem;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Provides;

/**
 * Tests behavior of {@code TerremarkVAppHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "vcloud.TerremarkVAppHandlerTest")
public class TerremarkVAppHandlerTest extends BaseHandlerTest {

   @BeforeTest
   @Override
   protected void setUpInjector() {
      injector = Guice.createInjector(new ParserModule(), new AbstractModule() {

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

      VApp result = (VApp) factory.create(injector.getInstance(TerremarkVAppHandler.class)).parse(
               is);
      assertEquals(result.getId(), 13775);

      assertEquals(result.getName(), "adriantest");
      assertEquals(result.getStatus(), VAppStatus.CREATING);

      assertEquals(result.getSize(), 4);

      assertEquals(result.getLocation(), URI
               .create("https://services.vcloudexpress.terremark.com/api/v0.8/vapp/13775"));
      assertEquals(result.getType(), "application/vnd.vmware.vcloud.vApp+xml");
      assertEquals(result.getVDC(), new LinkImpl("application/vnd.vmware.vcloud.vdc+xml", URI
               .create("https://services.vcloudexpress.terremark.com/api/v0.8/vdc/32")));

   }

   public void testGetVApp() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/terremark/get_vapp.xml");

      VApp result = (VApp) factory.create(injector.getInstance(TerremarkVAppHandler.class)).parse(
               is);
      assertEquals(result.getId(), 13850);

      assertEquals(result.getName(), "adriantest1");
      assertEquals(result.getStatus(), VAppStatus.OFF);

      assertEquals(result.getSize(), 4194304);
      assertEquals(result.getOperatingSystemDescription(), "Ubuntu Linux (32-bit)");

      assertEquals(result.getLocation(), URI
               .create("https://services.vcloudexpress.terremark.com/api/v0.8/vapp/13850"));
      assertEquals(result.getType(), "application/vnd.vmware.vcloud.vApp+xml");
      assertEquals(result.getVDC(), new LinkImpl(VCloudMediaType.VDC_XML, URI
               .create("https://services.vcloudexpress.terremark.com/api/v0.8/vdc/32")));
      assertEquals(
               result.getComputeOptions(),
               new LinkImpl(
                        MediaType.APPLICATION_XML,
                        URI
                                 .create("https://services.vcloudexpress.terremark.com/api/v0.8/vapp/13850/options/compute")));
      assertEquals(
               result.getCustomizationOptions(),
               new LinkImpl(
                        MediaType.APPLICATION_XML,
                        URI
                                 .create("https://services.vcloudexpress.terremark.com/api/v0.8/vapp/13850/options/customization")));
      assertEquals(result.getSystem(), new VirtualSystem(null, null, null, null, null, null, null,
               null, null, null, null, "Virtual Hardware Family", 0, null, null, null, null, null,
               "adriantest1", "vmx-07"));
      assertEquals(result.getNetworkToAddresses().get("Internal"), ImmutableList.of(InetAddress
               .getByName("10.114.34.132")));

      ResourceAllocation cpu = new ResourceAllocation(null, null, "hertz * 10^6", null, null, null,
               null, "Number of Virtual CPUs", "1 virtual CPU(s)", null, 1, null, null, null, null,
               null, null, null, ResourceType.VIRTUAL_CPU, 1, "count", null);
      ResourceAllocation controller = new ResourceAllocation(0, null, null, null, null, null, null,
               "SCSI Controller", "SCSI Controller 0", null, 3, null, null, null, null, null, null,
               "lsilogic", ResourceType.SCSI_CONTROLLER, 1, null, null);
      ResourceAllocation memory = new ResourceAllocation(null, null, "byte * 2^20", null, null,
               null, null, "Memory Size", "512MB of memory", null, 2, null, null, null, null, null,
               null, null, ResourceType.MEMORY, 512, "byte * 2^20", null);
      ResourceAllocation disk = new ResourceAllocation(null, 0, null, null, null, null, null, null,
               "Hard Disk 1", "4194304", 9, null, null, null, 3, null, null, null,
               ResourceType.VIRTUAL_DISK, 4194304, null, null);
      assertEquals(result.getResourceAllocations(), ImmutableSortedSet.of(cpu, controller, memory,
               disk));
      assertEquals(result.getResourceAllocationByType().get(ResourceType.VIRTUAL_CPU)
               .getVirtualQuantity(), 1);
      assertEquals(result.getResourceAllocationByType().get(ResourceType.SCSI_CONTROLLER)
               .getVirtualQuantity(), 1);
      assertEquals(result.getResourceAllocationByType().get(ResourceType.MEMORY)
               .getVirtualQuantity(), 512);
      assertEquals(result.getResourceAllocationByType().get(ResourceType.VIRTUAL_DISK)
               .getVirtualQuantity(), 4194304);
      assertEquals(result.getSize(), result.getResourceAllocationByType().get(
               ResourceType.VIRTUAL_DISK).getVirtualQuantity());
   }
}
