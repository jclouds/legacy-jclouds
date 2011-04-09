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
package org.jclouds.savvis.vpdc.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;

import org.jclouds.cim.ResourceAllocationSettingData;
import org.jclouds.cim.VirtualSystemSettingData;
import org.jclouds.cim.ResourceAllocationSettingData.ResourceType;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseSax.Factory;
import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.ovf.OperatingSystemSection;
import org.jclouds.ovf.ProductSection;
import org.jclouds.ovf.Property;
import org.jclouds.ovf.VirtualHardwareSection;
import org.jclouds.savvis.vpdc.domain.NetworkConfigSection;
import org.jclouds.savvis.vpdc.domain.NetworkConnectionSection;
import org.jclouds.savvis.vpdc.domain.VM;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code VMHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class VMHandlerTest {
   public void testVCloud1_0() {
      InputStream is = getClass().getResourceAsStream("/vm.xml");
      Injector injector = Guice.createInjector(new SaxParserModule());
      Factory factory = injector.getInstance(ParseSax.Factory.class);
      VM result = factory.create(injector.getInstance(VMHandler.class)).parse(is);

      VM expected = VM
               .builder()
               .id("1001")
               .status(VM.Status.ON)
               .name("znHost2")
               .type("application/vnd.vmware.vcloud.vApp+xml")
               .href(
                        URI
                                 .create("https://api.sandbox.symphonyVPDC.savvis.net/rest/api/v0.8/org/100000.0/vdc/2736/vApp/1001"))
               .networkConfigSection(
                        NetworkConfigSection.builder().network("VM Tier01").gateway("0.0.0.0").netmask("0.0.0.0").info(
                                 "MAC=00:00:00:00:00:00").fenceMode("allowInOut").dhcp(true).internalToExternalNATRule(
                                 "10.76.2.4", "206.24.124.1").build())
               .networkConnectionSection(
                        NetworkConnectionSection.builder().network("VM Tier01").info(
                                 "Read only description of a network connection to a deployed vApp").ipAddress(
                                 "10.76.0.5").build())
               .operatingSystemSection(
                        OperatingSystemSection.builder().id(80).info("Specifies the operating system installed")
                                 .description("Red Hat Enterprise Linux 5.x 64bit").build())
               .productSection(
                        ProductSection
                                 .builder()
                                 .info("vCenter Information")
                                 .property(
                                          Property.builder().value("false").key("vmwareToolsEnabled").label(
                                                   "VMWare Tools Enabled status").description(
                                                   "VMWare Tools Enabled status").build())
                                 .property(

                                          Property.builder().value("10.12.46.171").key("vmwareESXHost").label(
                                                   "VMWare ESXHost Ipaddress").description("VMWare ESXHost Ipaddress")
                                                   .build())
                                 .property(

                                          Property.builder().value("cussl01s01c01alun088bal,cussl01s01c01alun089bal")
                                                   .key("datastores").label("Datastore Name").description(
                                                            "Datastore Name").build())
                                 .property(
                                          Property
                                                   .builder()
                                                   .value("[Totalcapacity=1335915184128,UsedCapacity=549755813888]")
                                                   .key("cussl01s01c01alun088bal")
                                                   .label("Datastore Capacity")
                                                   .description(
                                                            "Datastore cussl01s01c01alun088bal Total Capacity, Used Capacity in comma separated")
                                                   .build())
                                 .property(

                                          Property
                                                   .builder()
                                                   .value("[Totalcapacity=1335915184129,UsedCapacity=549755813889]")
                                                   .key("cussl01s01c01alun089bal")
                                                   .label("Datastore Capacity")
                                                   .description(
                                                            "Datastore cussl01s01c01alun089bal Total Capacity, Used Capacity in comma separated")
                                                   .build()).property(

                                          Property.builder().value(
                                                   "[name=3282176-1949-bal-tier01,ip=0.0.0.0,mac=00:50:56:8c:3f:3c]")
                                                   .key("customerPortprofile").label("customerPortprofile")
                                                   .description("customerPortprofile").build()).property(

                                          Property.builder().value(
                                                   "[name=vm-server-mgmt,ip=0.0.0.0,mac=00:50:56:8c:39:75]").key(
                                                   "savvisPortprofile").label("savvisPortprofile").description(
                                                   "savvisPortprofile").build()).build()).virtualHardwareSection(
                        VirtualHardwareSection.builder().info("UUID=52254cd2-d848-4e7d-b8f3-3d257fed7666").system(
                                 VirtualSystemSettingData.builder().description("Virtual Hardware Family").elementName(
                                          "znHost2").instanceID("1").virtualSystemIdentifier("znHost2").build()).item(
                                 ResourceAllocationSettingData.builder().allocationUnits("3 GHz").description(
                                          "Number of Virtual CPUs").elementName("1 CPU").instanceID("2").resourceType(
                                          ResourceType.PROCESSOR).virtualQuantity(1l).build()).item(
                                 ResourceAllocationSettingData.builder().allocationUnits("Gigabytes").description(
                                          "Memory Size").elementName("Memory").instanceID("3").resourceType(
                                          ResourceType.MEMORY).virtualQuantity(2l).build()).item(
                                 ResourceAllocationSettingData.builder().connection("VM Tier01").elementName("Network")
                                          .instanceID("4").resourceType(ResourceType.ETHERNET_ADAPTER).virtualQuantity(
                                                   1l).build()).item(
                                 ResourceAllocationSettingData.builder().allocationUnits("Gigabytes")
                                          .caption("1234567").description("Hard Disk").elementName("C:\\")
                                          .hostResource("boot").instanceID("5").resourceType(
                                                   ResourceType.BASE_PARTITIONABLE_UNIT).virtualQuantity(25l).build())
                                 .item(
                                          ResourceAllocationSettingData.builder().allocationUnits("Gigabytes").caption(
                                                   "1234568").description("Hard Disk").elementName("D:\\")
                                                   .hostResource("data").instanceID("6").resourceType(
                                                            ResourceType.PARTITIONABLE_UNIT).virtualQuantity(50l)
                                                   .build()).build())

               .build();
      assertEquals(result, expected);
   }
}
