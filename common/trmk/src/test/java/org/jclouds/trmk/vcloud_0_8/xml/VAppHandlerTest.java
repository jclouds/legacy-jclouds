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
package org.jclouds.trmk.vcloud_0_8.xml;

import static com.google.common.collect.Iterables.find;
import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Properties;

import org.jclouds.Constants;
import org.jclouds.cim.CIMPredicates;
import org.jclouds.cim.ResourceAllocationSettingData;
import org.jclouds.cim.ResourceAllocationSettingData.ResourceType;
import org.jclouds.cim.VirtualSystemSettingData;
import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.trmk.vcloud_0_8.TerremarkVCloudMediaType;
import org.jclouds.trmk.vcloud_0_8.domain.Status;
import org.jclouds.trmk.vcloud_0_8.domain.VApp;
import org.jclouds.trmk.vcloud_0_8.domain.internal.ReferenceTypeImpl;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.name.Names;

/**
 * Tests behavior of {@code VAppHandler}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "VAppHandlerTest")
public class VAppHandlerTest extends BaseHandlerTest {

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

   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream("/launched_vapp.xml");

      VApp result = factory.create(
               injector.getInstance(VAppHandler.class)).parse(is);

      assertEquals(result.getName(), "adriantest");
      assertEquals(result.getStatus(), Status.UNRESOLVED);

      assertEquals(result.getSize().longValue(), 4l);

      assertEquals(result.getHref(), URI.create("https://services.vcloudexpress.terremark.com/api/v0.8/vapp/13775"));
      assertEquals(result.getVDC(), new ReferenceTypeImpl(null, "application/vnd.vmware.vcloud.vdc+xml", URI
               .create("https://services.vcloudexpress.terremark.com/api/v0.8/vdc/32")));

   }

   public void testGetVApp() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/get_vapp.xml");

      VApp result = factory.create(
               injector.getInstance(VAppHandler.class)).parse(is);

      assertEquals(result.getName(), "centos-53");
      assertEquals(result.getStatus(), Status.OFF);

      assertEquals(result.getSize().longValue(), 10485760);
      assertEquals(result.getOperatingSystemDescription(), "Red Hat Enterprise Linux 5 (64-bit)");

      assertEquals(result.getHref(), URI.create("https://services.vcloudexpress.terremark.com/api/v0.8/vapp/16238"));
      assertEquals(result.getVDC(), new ReferenceTypeImpl(null, TerremarkVCloudMediaType.VDC_XML, URI
               .create("https://services.vcloudexpress.terremark.com/api/v0.8/vdc/32")));

      VirtualSystemSettingData system = VirtualSystemSettingData.builder().instanceID("0").elementName(
               "Virtual Hardware Family").virtualSystemIdentifier("centos-53").virtualSystemType("vmx-07").build();
      assertEquals(result.getSystem().toString(), system.toString());
      assertEquals(result.getNetworkToAddresses().get("Internal"), ImmutableList.<String> of("10.114.34.132"));

      ResourceAllocationSettingData cpu = ResourceAllocationSettingData.builder().instanceID("1").elementName(
               "1 virtual CPU(s)").description("Number of Virtual CPUs").resourceType(ResourceType.PROCESSOR)
               .virtualQuantity(2l).virtualQuantityUnits("hertz * 10^6").build();

      ResourceAllocationSettingData controller = ResourceAllocationSettingData.builder().instanceID("3").elementName(
               "SCSI Controller 0").description("SCSI Controller").resourceType(ResourceType.PARALLEL_SCSI_HBA)
               .resourceSubType("lsilogic").address("0").virtualQuantity(1l).build();
      ResourceAllocationSettingData memory = ResourceAllocationSettingData.builder().instanceID("2").elementName(
               "512MB of memory").description("Memory Size").resourceType(ResourceType.MEMORY).virtualQuantity(512l)
               .virtualQuantityUnits("byte * 2^20").build();
      ResourceAllocationSettingData disk = ResourceAllocationSettingData.builder().instanceID("9").elementName(
               "Hard Disk 1").resourceType(ResourceType.DISK_DRIVE).hostResource("10485760").addressOnParent("0")
               .virtualQuantity(10485760l).virtualQuantityUnits("byte * 2^20").build();
      assertEquals(result.getResourceAllocations(), ImmutableSet.of(cpu, memory, controller, disk));
      assertEquals(find(result.getResourceAllocations(), CIMPredicates.resourceTypeIn(ResourceType.PROCESSOR))
               .getVirtualQuantity().intValue(), 1);
      find(result.getResourceAllocations(), CIMPredicates.resourceTypeIn(ResourceType.PARALLEL_SCSI_HBA));

      assertEquals(find(result.getResourceAllocations(), CIMPredicates.resourceTypeIn(ResourceType.MEMORY))
               .getVirtualQuantity().intValue(), 512);
      assertEquals(find(result.getResourceAllocations(), CIMPredicates.resourceTypeIn(ResourceType.DISK_DRIVE))
               .getVirtualQuantity().longValue(), 10485760);
      assertEquals(result.getSize().longValue(), find(result.getResourceAllocations(),
               CIMPredicates.resourceTypeIn(ResourceType.DISK_DRIVE)).getVirtualQuantity().longValue());
   }

   public void testGetVApp2disks() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/get_vapp2disks.xml");

      VApp vApp = factory.create(injector.getInstance(VAppHandler.class)).parse(is);

      assertEquals(vApp.getName(), "eduardo");
      assertEquals(vApp.getStatus(), Status.OFF);

      assertEquals(vApp.getSize().longValue(), 30408704);
      assertEquals(vApp.getOperatingSystemDescription(), "Ubuntu Linux (32-bit)");

      assertEquals(vApp.getHref(), URI.create("https://services.vcloudexpress.terremark.com/api/v0.8/vapp/15639"));
      assertEquals(vApp.getVDC(), new ReferenceTypeImpl(null, TerremarkVCloudMediaType.VDC_XML, URI
               .create("https://services.vcloudexpress.terremark.com/api/v0.8/vdc/32")));
      VirtualSystemSettingData system = VirtualSystemSettingData.builder().instanceID("0").elementName(
               "Virtual Hardware Family").virtualSystemIdentifier("eduardo").virtualSystemType("vmx-07").build();
      assertEquals(vApp.getSystem().toString(), system.toString());

      assertEquals(vApp.getNetworkToAddresses().get("Internal"), ImmutableList.of("10.114.34.131"));

      ResourceAllocationSettingData cpu = ResourceAllocationSettingData.builder().instanceID("1").elementName(
               "2 virtual CPU(s)").description("Number of Virtual CPUs").resourceType(ResourceType.PROCESSOR)
               .virtualQuantity(2l).virtualQuantityUnits("hertz * 10^6").build();

      ResourceAllocationSettingData controller = ResourceAllocationSettingData.builder().instanceID("3").elementName(
               "SCSI Controller 0").description("SCSI Controller").resourceType(ResourceType.PARALLEL_SCSI_HBA)
               .resourceSubType("lsilogic").address("0").virtualQuantity(1l).build();
      ResourceAllocationSettingData memory = ResourceAllocationSettingData.builder().instanceID("2").elementName(
               "1024MB of memory").description("Memory Size").resourceType(ResourceType.MEMORY).virtualQuantity(1024l)
               .virtualQuantityUnits("byte * 2^20").build();
      ResourceAllocationSettingData disk = ResourceAllocationSettingData.builder().instanceID("9").elementName(
               "Hard Disk 1").resourceType(ResourceType.DISK_DRIVE).hostResource("4194304").addressOnParent("0")
               .virtualQuantity(4194304l).virtualQuantityUnits("byte * 2^20").build();
      ResourceAllocationSettingData disk2 = ResourceAllocationSettingData.builder().instanceID("9").elementName(
               "Hard Disk 2").resourceType(ResourceType.DISK_DRIVE).hostResource("26214400").addressOnParent("1")
               .virtualQuantity(26214400l).virtualQuantityUnits("byte * 2^20").build();

      assertEquals(vApp.getResourceAllocations(), ImmutableSet.of(cpu, memory, controller, disk, disk2));
      assertEquals(find(vApp.getResourceAllocations(), CIMPredicates.resourceTypeIn(ResourceType.PROCESSOR))
               .getVirtualQuantity().intValue(), 2);
      find(vApp.getResourceAllocations(), CIMPredicates.resourceTypeIn(ResourceType.PARALLEL_SCSI_HBA));
      assertEquals(find(vApp.getResourceAllocations(), CIMPredicates.resourceTypeIn(ResourceType.MEMORY))
               .getVirtualQuantity().longValue(), 1024);

      // extract the disks on the vApp sorted by addressOnParent
      List<ResourceAllocationSettingData> disks = Lists.newArrayList(Iterables.filter(vApp.getResourceAllocations(),
               CIMPredicates.resourceTypeIn(ResourceType.DISK_DRIVE)));

      assertEquals(disks.get(0).getVirtualQuantity().longValue(), 4194304);
      assertEquals(disks.get(1).getVirtualQuantity().longValue(), 26214400);

   }

}
