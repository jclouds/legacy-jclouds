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

package org.jclouds.vcloud.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.SortedSet;

import org.jclouds.Constants;
import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.vcloud.domain.ResourceAllocation;
import org.jclouds.vcloud.domain.ResourceType;
import org.jclouds.vcloud.domain.Status;
import org.jclouds.vcloud.domain.VCloudExpressVApp;
import org.jclouds.vcloud.domain.VirtualSystem;
import org.jclouds.vcloud.domain.internal.ReferenceTypeImpl;
import org.jclouds.vcloud.domain.internal.VCloudExpressVAppImpl;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.ListMultimap;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.name.Names;

/**
 * Tests behavior of {@code VCloudExpressVAppHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "vcloud.VCloudExpressVAppHandlerTest")
public class VCloudExpressVAppHandlerTest extends BaseHandlerTest {
   @BeforeTest
   @Override
   protected void setUpInjector() {
      injector = Guice.createInjector(new SaxParserModule(), new AbstractModule() {

         @Override
         protected void configure() {
            Properties toBind = new Properties();
            toBind.setProperty(Constants.PROPERTY_API_VERSION, "0.8");
            Names.bindProperties(binder(), toBind);
         }

      });
      factory = injector.getInstance(ParseSax.Factory.class);
   }

   public void testInstantiated() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/instantiatedvapp.xml");

      VCloudExpressVApp result = factory.create(injector.getInstance(VCloudExpressVAppHandler.class)).parse(is);

      VCloudExpressVApp expects = new VCloudExpressVAppImpl("centos53", URI
               .create("http://10.150.4.49/api/v0.8/vApp/10"), Status.RESOLVED, 123456789l, new ReferenceTypeImpl(null,
               "application/vnd.vmware.vcloud.vdc+xml", URI.create("http://10.150.4.49/api/v0.8/vdc/4")),
               ImmutableListMultimap.<String, String> of(), null, null, null, ImmutableSet.<ResourceAllocation> of());
      assertEquals(result, expects);
   }

   public void testDefault() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/vapp.xml");

      VCloudExpressVApp result = factory.create(injector.getInstance(VCloudExpressVAppHandler.class)).parse(is);

      ListMultimap<String, String> networkToAddresses = ImmutableListMultimap.<String, String> of("Public Network",
               "10.150.4.93");
      VirtualSystem system = new VirtualSystem(0, "Virtual Hardware Family", "centos53", "vmx-07");

      SortedSet<ResourceAllocation> resourceAllocations = ImmutableSortedSet.<ResourceAllocation> naturalOrder().add(
               new ResourceAllocation(1, "1 virtual CPU(s)", "Number of Virtual CPUs", ResourceType.PROCESSOR, null,
                        null, null, null, null, null, 1, "hertz * 10^6"),
               new ResourceAllocation(2, "16MB of memory", "Memory Size", ResourceType.MEMORY, null, null, null, null,
                        null, null, 16, "byte * 2^20")).add(
               new ResourceAllocation(3, "SCSI Controller 0", "SCSI Controller", ResourceType.SCSI_CONTROLLER,
                        "lsilogic", null, 0, null, null, null, 1, null)).add(
               new ResourceAllocation(8, "Network Adapter 1", "PCNet32 ethernet adapter on \"Internal\" network",
                        ResourceType.ETHERNET_ADAPTER, "PCNet32", null, null, 7, null, true, 1, null)).add(
               new ResourceAllocation(9, "Hard Disk 1", null, ResourceType.DISK_DRIVE, null, "104857", null, 0, 3,
                        null, 104857, "byte * 2^20")).build();

      VCloudExpressVApp expects = new VCloudExpressVAppImpl("centos53", URI
               .create("http://10.150.4.49/api/v0.8/vApp/10"), Status.ON, new Long(104857), new ReferenceTypeImpl(null,
               "application/vnd.vmware.vcloud.vdc+xml", URI.create("http://10.150.4.49/api/v0.8/vdc/4")),
               networkToAddresses, null, "Other Linux (32-bit)", system, resourceAllocations);
      assertEquals(result.getHref(), expects.getHref());
      assertEquals(result.getName(), expects.getName());
      assertEquals(result.getNetworkToAddresses(), expects.getNetworkToAddresses());
      assertEquals(result.getOperatingSystemDescription(), expects.getOperatingSystemDescription());
      assertEquals(result.getResourceAllocations(), expects.getResourceAllocations());
      assertEquals(result.getSize(), expects.getSize());
      assertEquals(result.getStatus(), expects.getStatus());
      assertEquals(result.getSystem(), expects.getSystem());
      assertEquals(result.getType(), expects.getType());
      assertEquals(result.getVDC(), expects.getVDC());
   }

   public void testLatest() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/vapp2.xml");

      VCloudExpressVApp result = factory.create(injector.getInstance(VCloudExpressVAppHandler.class)).parse(is);

      ListMultimap<String, String> networkToAddresses = ImmutableListMultimap.<String, String> of("Public Network",
               "10.23.119.221");
      VirtualSystem system = new VirtualSystem(0, "Virtual Hardware Family", "m1", "vmx-07");

      SortedSet<ResourceAllocation> resourceAllocations = ImmutableSortedSet.<ResourceAllocation> naturalOrder().add(
               new ResourceAllocation(1, "1 virtual CPU(s)", "Number of Virtual CPUs", ResourceType.PROCESSOR, null,
                        null, null, null, null, null, 1, "hertz * 10^6"),
               new ResourceAllocation(2, "512MB of memory", "Memory Size", ResourceType.MEMORY, null, null, null, null,
                        null, null, 512, "byte * 2^20")).add(
               new ResourceAllocation(3, "SCSI Controller 0", "SCSI Controller", ResourceType.SCSI_CONTROLLER,
                        "lsilogic", null, 0, null, null, null, 1, null)).add(
               new ResourceAllocation(8, "Network Adapter 1", "PCNet32 ethernet adapter on \"Internal\" network",
                        ResourceType.ETHERNET_ADAPTER, "PCNet32", null, null, 7, null, true, 1, null)).add(
               new ResourceAllocation(9, "Hard Disk 1", null, ResourceType.DISK_DRIVE, null, "10485760", null, 0, 3,
                        null, 10485760, "byte * 2^20")).build();

      VCloudExpressVApp expects = new VCloudExpressVAppImpl("m1", URI.create("http://localhost:8000/api/v0.8/vApp/80"),
               Status.ON, new Long(10485760), new ReferenceTypeImpl(null, "application/vnd.vmware.vcloud.vdc+xml", URI
                        .create("http://localhost:8000/api/v0.8/vdc/28")), networkToAddresses, null,
               "Microsoft Windows XP Professional (32-bit)", system, resourceAllocations);
      assertEquals(result.getHref(), expects.getHref());
      assertEquals(result.getName(), expects.getName());
      assertEquals(result.getNetworkToAddresses(), expects.getNetworkToAddresses());
      assertEquals(result.getOperatingSystemDescription(), expects.getOperatingSystemDescription());
      assertEquals(result.getResourceAllocations(), expects.getResourceAllocations());
      assertEquals(result.getSize(), expects.getSize());
      assertEquals(result.getStatus(), expects.getStatus());
      assertEquals(result.getSystem(), expects.getSystem());
      assertEquals(result.getType(), expects.getType());
      assertEquals(result.getVDC(), expects.getVDC());
   }

}
