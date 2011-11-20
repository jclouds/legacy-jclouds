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
package org.jclouds.tmrk.enterprisecloud.xml;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import org.jclouds.crypto.Crypto;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseXMLWithJAXB;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.BaseRestClientTest;
import org.jclouds.rest.RestContextSpec;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.tmrk.enterprisecloud.domain.*;
import org.jclouds.tmrk.enterprisecloud.domain.VirtualMachine.VirtualMachineStatus;
import org.jclouds.tmrk.enterprisecloud.features.VirtualMachineAsyncClient;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.inject.Named;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Set;

import static org.jclouds.io.Payloads.newInputStreamPayload;
import static org.jclouds.rest.RestContextFactory.contextSpec;
import static org.jclouds.rest.RestContextFactory.createContextBuilder;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

/**
 * Tests behavior of JAXB parsing for VirtualMachines
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "VirtualMachineJAXBParsingTest")
public class VirtualMachineJAXBParsingTest extends BaseRestClientTest {
   private SimpleDateFormatDateService dateService;

  @BeforeMethod
  public void setUp() {
     dateService = new SimpleDateFormatDateService();
  }

   @BeforeClass
   void setupFactory() {
   RestContextSpec<String, Integer> contextSpec = contextSpec("test", "http://localhost:9999", "1", "", "userfoo",
        "credentialFoo", String.class, Integer.class,
        ImmutableSet.<Module> of(new MockModule(), new NullLoggingModule(), new AbstractModule() {

            @Override
            protected void configure() {}

            @SuppressWarnings("unused")
            @Provides
            @Named("exception")
            Set<String> exception() {
                throw new AuthorizationException();
            }

        }));

      injector = createContextBuilder(contextSpec).buildInjector();
      parserFactory = injector.getInstance(ParseSax.Factory.class);
      crypto = injector.getInstance(Crypto.class);
   }

   @Test
   public void testParseVirtualMachineWithJAXB() throws Exception {

      Method method = VirtualMachineAsyncClient.class.getMethod("getVirtualMachine", URI.class);
      HttpRequest request = factory(VirtualMachineAsyncClient.class).createRequest(method,new URI("/1"));
      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);

      Function<HttpResponse, VirtualMachine> parser = (Function<HttpResponse, VirtualMachine>) RestAnnotationProcessor
            .createResponseParser(parserFactory, injector, method, request);

      InputStream is = getClass().getResourceAsStream("/virtualMachine.xml");
      VirtualMachine virtualMachine = parser.apply(new HttpResponse(200, "ok", newInputStreamPayload(is)));

      assertEquals("My first terremark server",virtualMachine.getDescription());
      assertEquals(6,virtualMachine.getLinks().size());
      assertEquals(11,virtualMachine.getActions().size());
      assertEquals(1,virtualMachine.getTasks().size());

      assertLayout(virtualMachine.getLayout());
      assertEquals(VirtualMachineStatus.DEPLOYED, virtualMachine.getStatus());
      assertFalse(virtualMachine.isPoweredOn(),"virtual machine is not powered on");
      assertEquals(ToolsStatus.NOT_RUNNING, virtualMachine.getToolsStatus());
      assertEquals(VirtualMachine.VirtualMachineMediaStatus.UNMOUNTED, virtualMachine.getMediaStatus());
      assertTrue(virtualMachine.isCustomizationPending(),"virtual machine is pending customization");
      assertOperatingSystem(virtualMachine.getOperatingSystem());
      assertHardwareConfiguration(virtualMachine.getHardwareConfiguration());
      assertIpAddresses(virtualMachine.getIpAddresses());
   }

   private void assertLayout(Layout layout) {
      assertNotNull(layout);
      assertEquals("test row", layout.getRow().getName());
      assertEquals("test group",layout.getGroup().getName());
   }

   private void assertOperatingSystem(OperatingSystem operatingSystem) throws Exception {
       String href = "/cloudapi/ecloud/operatingsystems/rhel5_64guest/computepools/89";
       String name = "Red Hat Enterprise Linux 5 (64-bit)";
       String type = "application/vnd.tmrk.cloud.operatingSystem";
       OperatingSystem os = OperatingSystem.builder().href(new URI(href)).name(name).type(type).build();
       Assert.assertEquals(os, operatingSystem);
   }

   private void assertHardwareConfiguration(HardwareConfiguration hardwareConfiguration) throws Exception {
       assertEquals(1,hardwareConfiguration.getActions().size());
       assertEquals(1,hardwareConfiguration.getProcessorCount());
       Memory memory = Memory.builder().value(384).unit("MB").build();
       assertEquals(memory,hardwareConfiguration.getMemory());
       assertDisks(hardwareConfiguration.getVirtualDisks());
       assertNics(hardwareConfiguration.getVirtualNics());
   }

   private void assertDisks(Set<VirtualDisk> disks) {
       VirtualDisk disk = VirtualDisk.builder().index(0).name("Hard Disk 1")
                                     .size(Size.builder().value(10).unit("GB").build())
                                     .build();

       assertEquals(ImmutableSet.of(disk), disks);
   }


   private void assertNics(Set<VirtualNic> nics) throws Exception {

       assertEquals(1, nics.size());

       NetworkReference network = NetworkReference.builder()
             .href(new URI("/cloudapi/ecloud/networks/3936"))
             .name("10.146.204.64/28")
             .type("application/vnd.tmrk.cloud.network")
             .networkType(NetworkReference.NetworkType.INTERNAL)
             .build();

       VirtualNic nic = VirtualNic.builder()
             .macAddress("00:50:56:b8:00:58")
             .name("Network adapter 1")
             .network(network)
             .unitNumber(7)
             .build();
       assertEquals(nic,nics.iterator().next());
   }

   private void assertIpAddresses(VirtualMachineIpAddresses ipAddresses) {
       AssignedIpAddresses assignedIpAddresses = ipAddresses.getAssignedIpAddresses();
       Assert.assertNotNull(assignedIpAddresses);
       Set<DeviceNetwork> deviceNetworks = assignedIpAddresses.getNetworks().getDeviceNetworks();
       assertEquals(1,deviceNetworks.size());
       DeviceNetwork network = Iterables.getOnlyElement(deviceNetworks);
       Set<String> ips = network.getIpAddresses().getIpAddresses();
       assertEquals(2,ips.size());
       assertTrue(ips.contains("10.146.204.67"));
       assertTrue(ips.contains("10.146.204.68"));
   }
}
