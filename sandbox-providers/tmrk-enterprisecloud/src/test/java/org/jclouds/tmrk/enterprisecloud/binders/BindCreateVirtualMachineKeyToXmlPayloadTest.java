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
package org.jclouds.tmrk.enterprisecloud.binders;

import com.google.common.collect.ImmutableSet;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.jclouds.http.HttpRequest;
import org.jclouds.tmrk.enterprisecloud.domain.NamedResource;
import org.jclouds.tmrk.enterprisecloud.domain.internal.ResourceCapacity;
import org.jclouds.tmrk.enterprisecloud.domain.layout.LayoutRequest;
import org.jclouds.tmrk.enterprisecloud.domain.network.LinuxCustomization;
import org.jclouds.tmrk.enterprisecloud.domain.network.NetworkAdapterSetting;
import org.jclouds.tmrk.enterprisecloud.domain.network.NetworkAdapterSettings;
import org.jclouds.tmrk.enterprisecloud.domain.network.NetworkSettings;
import org.jclouds.tmrk.enterprisecloud.domain.vm.CreateVirtualMachine;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URI;

import static org.testng.Assert.assertEquals;

/**
 * Tests behavior of {@code BindCreateVirtualMachineKeyToXmlPayload}
 * @author Jason King
 */
@Test(groups = "unit", testName = "BindCreateVirtualMachineKeyToXmlPayloadTest")
public class BindCreateVirtualMachineKeyToXmlPayloadTest {
   Injector injector = Guice.createInjector(new AbstractModule() {

      @Override
      protected void configure() {
      }
   });

   public void testPayloadXmlContent() throws IOException {
       String expected =
         "<CreateVirtualMachine name='VirtualMachine2'>" +
            "<ProcessorCount>2</ProcessorCount>" +
            "<Memory><Unit>MB</Unit><Value>1024</Value></Memory>" +
            "<Layout><Group href='/cloudapi/ecloud/layoutgroups/308' type='application/vnd.tmrk.cloud.layoutGroup'/></Layout>" +
            "<Description>This is my first VM</Description>" +
            "<Tags><Tag>Web</Tag></Tags>" +
            "<LinuxCustomization>" +
               "<NetworkSettings>" +
                  "<NetworkAdapterSettings>" +
                     "<NetworkAdapter>" +
                           "<Network href='/cloudapi/ecloud/networks/3936' name='10.146.204.64/28' type='application/vnd.tmrk.cloud.network'/>" +
                           "<IpAddress>10.146.204.68</IpAddress>" +
                     "</NetworkAdapter>" +
                  "</NetworkAdapterSettings>" +
               "</NetworkSettings>" +
               "<SshKey href='/cloudapi/ecloud/admin/sshkeys/77' type='application/vnd.tmrk.cloud.admin.sshKey'/>" +
            "</LinuxCustomization>" +
            "<PoweredOn>false</PoweredOn>" +
            "<Template href='/cloudapi/ecloud/templates/6/computepools/89' type='application/vnd.tmrk.cloud.template'/>" +
         "</CreateVirtualMachine>";

      HttpRequest request = new HttpRequest("GET", URI.create("http://test"));
      BindCreateVirtualMachineKeyToXmlPayload binder = injector
               .getInstance(BindCreateVirtualMachineKeyToXmlPayload.class);

      CreateVirtualMachine.Builder builder = CreateVirtualMachine.builder();
            builder.name("VirtualMachine2")
                   .processorCount(2)
                   .memory(ResourceCapacity.builder().value(1024).unit("MB").build());

      NamedResource group = NamedResource.builder().href(URI.create("/cloudapi/ecloud/layoutgroups/308")).type("application/vnd.tmrk.cloud.layoutGroup").build();
      builder.layout(LayoutRequest.builder().group(group).build());
      builder.description("This is my first VM");
      builder.tags(ImmutableSet.of("Web"));
      NamedResource sshKey = NamedResource.builder().href(URI.create("/cloudapi/ecloud/admin/sshkeys/77")).type("application/vnd.tmrk.cloud.admin.sshKey").build();

      NamedResource network = NamedResource.builder()
            .href(URI.create("/cloudapi/ecloud/networks/3936"))
            .name("10.146.204.64/28")
            .type("application/vnd.tmrk.cloud.network")
            .build();

      NetworkAdapterSetting adapterSetting = NetworkAdapterSetting.builder()
                                                           .network(network)
                                                           .ipAddress("10.146.204.68")
                                                           .build();

      NetworkAdapterSettings adapterSettings = NetworkAdapterSettings.builder()
                                                                     .addNetworkAdapterSetting(adapterSetting).build();
      NetworkSettings networkSettings = NetworkSettings.builder().networkAdapterSettings(adapterSettings).build();

      LinuxCustomization linuxCustomization = LinuxCustomization.builder()
            .sshKey(sshKey)
            .networkSettings(networkSettings)
            .build();
      builder.linuxCustomization(linuxCustomization);

      NamedResource template = NamedResource.builder().href(URI.create("/cloudapi/ecloud/templates/6/computepools/89")).type("application/vnd.tmrk.cloud.template").build();
      builder.template(template);
      binder.bindToRequest(request, builder.build());
      assertEquals(request.getPayload().getRawContent(), expected.replaceAll("'","\""));
   }
}
