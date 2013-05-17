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
package org.jclouds.trmk.vcloud_0_8.xml.ovf;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.Set;

import org.jclouds.Constants;
import org.jclouds.cim.ResourceAllocationSettingData;
import org.jclouds.cim.ResourceAllocationSettingData.ResourceType;
import org.jclouds.cim.VirtualSystemSettingData;
import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.trmk.vcloud_0_8.domain.Status;
import org.jclouds.trmk.vcloud_0_8.domain.VApp;
import org.jclouds.trmk.vcloud_0_8.domain.internal.ReferenceTypeImpl;
import org.jclouds.trmk.vcloud_0_8.domain.internal.VAppImpl;
import org.jclouds.trmk.vcloud_0_8.xml.VAppHandler;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ListMultimap;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.name.Names;

/**
 * Tests behavior of {@code VCloudExpressVAppHandler}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "VCloudExpressVAppHandlerTest")
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

   public void testInstantiated() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/instantiatedvapp.xml");

      VApp result = factory.create(injector.getInstance(VAppHandler.class)).parse(is);

      VApp expects = new VAppImpl("centos53", URI
               .create("http://10.150.4.49/api/v0.8/vApp/10"), Status.RESOLVED, 123456789l, new ReferenceTypeImpl(null,
               "application/vnd.vmware.vcloud.vdc+xml", URI.create("http://10.150.4.49/api/v0.8/vdc/4")),
               ImmutableListMultimap.<String, String> of(), null, null, null, ImmutableSet
                        .<ResourceAllocationSettingData> of());
      assertEquals(result, expects);
   }

   public void testDefault() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/vapp.xml");

      VApp result = factory.create(injector.getInstance(VAppHandler.class)).parse(is);

      ListMultimap<String, String> networkToAddresses = ImmutableListMultimap.<String, String> of("Public Network",
               "10.150.4.93");

      VirtualSystemSettingData system = VirtualSystemSettingData.builder().instanceID("0").elementName(
               "Virtual Hardware Family").virtualSystemIdentifier("centos53").virtualSystemType("vmx-07").build();

      Set<ResourceAllocationSettingData> resourceAllocations = ImmutableSet.<ResourceAllocationSettingData> of(
               ResourceAllocationSettingData.builder().instanceID("1").elementName("1 virtual CPU(s)").description(
                        "Number of Virtual CPUs").resourceType(ResourceType.PROCESSOR).virtualQuantity(1l)
                        .allocationUnits("hertz * 10^6").virtualQuantityUnits("count").build(),

               ResourceAllocationSettingData.builder().instanceID("2").elementName("16MB of memory").description(
                        "Memory Size").resourceType(ResourceType.MEMORY).virtualQuantity(16l).allocationUnits(
                        "byte * 2^20").virtualQuantityUnits("byte * 2^20").build(),

               ResourceAllocationSettingData.builder().instanceID("3").elementName("SCSI Controller 0").description(
                        "SCSI Controller").resourceType(ResourceType.PARALLEL_SCSI_HBA).resourceSubType("lsilogic")
                        .address("0").build(),

               ResourceAllocationSettingData.builder().instanceID("8").elementName("Network Adapter 1").description(
                        "PCNet32 ethernet adapter on \"Internal\" network").resourceType(ResourceType.ETHERNET_ADAPTER)
                        .resourceSubType("PCNet32").addressOnParent("7").connection("Internal").automaticAllocation(
                                 true).build(),

               ResourceAllocationSettingData.builder().instanceID("9").elementName("Hard Disk 1").resourceType(
                        ResourceType.DISK_DRIVE).hostResource("104857").addressOnParent("0").parent("3")
                        .virtualQuantity(104857l).build());

      VApp expects = new VAppImpl("centos53", URI
               .create("http://10.150.4.49/api/v0.8/vApp/10"), Status.ON, Long.valueOf(104857), new ReferenceTypeImpl(null,
               "application/vnd.vmware.vcloud.vdc+xml", URI.create("http://10.150.4.49/api/v0.8/vdc/4")),
               networkToAddresses, null, "Other Linux (32-bit)", system, resourceAllocations);
      assertEquals(result.getHref(), expects.getHref());
      assertEquals(result.getName(), expects.getName());
      assertEquals(result.getNetworkToAddresses(), expects.getNetworkToAddresses());
      assertEquals(result.getOperatingSystemDescription(), expects.getOperatingSystemDescription());
      assertEquals(result.getResourceAllocations().toString(), expects.getResourceAllocations().toString());
      assertEquals(result.getSize(), expects.getSize());
      assertEquals(result.getStatus(), expects.getStatus());
      assertEquals(result.getSystem().toString(), expects.getSystem().toString());
      assertEquals(result.getType(), expects.getType());
      assertEquals(result.getVDC(), expects.getVDC());
   }

   public void testLatest() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/vapp2.xml");

      VApp result = factory.create(injector.getInstance(VAppHandler.class)).parse(is);

      ListMultimap<String, String> networkToAddresses = ImmutableListMultimap.<String, String> of("Public Network",
               "10.23.119.221");
      VirtualSystemSettingData system = VirtualSystemSettingData.builder().instanceID("0").elementName(
               "Virtual Hardware Family").virtualSystemIdentifier("m1").virtualSystemType("vmx-07").build();

      Set<ResourceAllocationSettingData> resourceAllocations = ImmutableSet.<ResourceAllocationSettingData> of(
               ResourceAllocationSettingData.builder().instanceID("1").elementName("1 virtual CPU(s)").description(
                        "Number of Virtual CPUs").resourceType(ResourceType.PROCESSOR).virtualQuantity(1l)
                        .allocationUnits("hertz * 10^6").virtualQuantityUnits("count").build(),

               ResourceAllocationSettingData.builder().instanceID("2").elementName("512MB of memory").description(
                        "Memory Size").resourceType(ResourceType.MEMORY).virtualQuantity(512l).allocationUnits(
                        "byte * 2^20").virtualQuantityUnits("byte * 2^20").build(),

               ResourceAllocationSettingData.builder().instanceID("3").elementName("SCSI Controller 0").description(
                        "SCSI Controller").resourceType(ResourceType.PARALLEL_SCSI_HBA).resourceSubType("lsilogic")
                        .address("0").build(),

               ResourceAllocationSettingData.builder().instanceID("8").elementName("Network Adapter 1").description(
                        "PCNet32 ethernet adapter on \"Internal\" network").resourceType(ResourceType.ETHERNET_ADAPTER)
                        .resourceSubType("PCNet32").addressOnParent("7").connection("Internal").automaticAllocation(
                                 true).build(),

               ResourceAllocationSettingData.builder().instanceID("9").elementName("Hard Disk 1").resourceType(
                        ResourceType.DISK_DRIVE).hostResource("10485760").addressOnParent("0").parent("3")
                        .virtualQuantity(10485760l).build());

      VApp expects = new VAppImpl("m1", URI.create("http://localhost:8000/api/v0.8/vApp/80"),
               Status.ON, Long.valueOf(10485760), new ReferenceTypeImpl(null, "application/vnd.vmware.vcloud.vdc+xml", URI
                        .create("http://localhost:8000/api/v0.8/vdc/28")), networkToAddresses, null,
               "Microsoft Windows XP Professional (32-bit)", system, resourceAllocations);
      assertEquals(result.getHref(), expects.getHref());
      assertEquals(result.getName(), expects.getName());
      assertEquals(result.getNetworkToAddresses(), expects.getNetworkToAddresses());
      assertEquals(result.getOperatingSystemDescription(), expects.getOperatingSystemDescription());
      assertEquals(result.getResourceAllocations().toString(), expects.getResourceAllocations().toString());
      assertEquals(result.getSize(), expects.getSize());
      assertEquals(result.getStatus(), expects.getStatus());
      assertEquals(result.getSystem().toString(), expects.getSystem().toString());
      assertEquals(result.getType(), expects.getType());
      assertEquals(result.getVDC(), expects.getVDC());
   }
}
