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
package org.jclouds.trmk.vcloud_0_8.binders;

import static org.jclouds.trmk.vcloud_0_8.domain.VAppConfiguration.Builder.changeNameTo;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import org.jclouds.cim.ResourceAllocationSettingData;
import org.jclouds.cim.ResourceAllocationSettingData.ResourceType;
import org.jclouds.rest.annotations.ApiVersion;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.trmk.vcloud_0_8.domain.Status;
import org.jclouds.trmk.vcloud_0_8.domain.VAppConfiguration;
import org.jclouds.trmk.vcloud_0_8.domain.internal.VAppImpl;
import org.jclouds.trmk.vcloud_0_8.internal.BasePayloadTest;
import org.jclouds.trmk.vcloud_0_8.internal.TerremarkVCloudApiMetadata;
import org.jclouds.util.Strings2;
import org.nnsoft.guice.rocoto.Rocoto;
import org.nnsoft.guice.rocoto.configuration.ConfigurationModule;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code BindVAppConfigurationToXmlPayload}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class BindVAppConfigurationToXmlPayloadTest extends BasePayloadTest {
   Injector injector = Guice.createInjector(Rocoto.expandVariables(new ConfigurationModule() {

      @Override
      protected void bindConfigurations() {
         bind(String.class).annotatedWith(ApiVersion.class).toInstance("0.8");
         bindProperties(TerremarkVCloudApiMetadata.defaultProperties());
      }
   }));

   public void testChangeName() throws IOException {
      VAppImpl vApp = new VAppImpl("MyAppServer6", URI
               .create("https://services.vcloudexpress/terremark.com/api/v0.8/vapp/4213"), Status.OFF, 4194304l, null,
               ImmutableListMultimap.<String, String> of(), null, null, null, ImmutableSet.of(
                        ResourceAllocationSettingData.builder().instanceID("1").elementName("foo").resourceType(
                                 ResourceType.PROCESSOR).virtualQuantity(2l).build(), ResourceAllocationSettingData
                                 .builder().instanceID("2").elementName("foo").resourceType(ResourceType.MEMORY)
                                 .virtualQuantity(1024l).build(), ResourceAllocationSettingData.builder().instanceID(
                                 "9").elementName("foo").addressOnParent("0").hostResource("1048576").resourceType(
                                 ResourceType.DISK_DRIVE).virtualQuantity(209152l).build()));

      String expected = Strings2.toStringAndClose(getClass().getResourceAsStream("/configureVApp.xml"))
               .replace("eduardo", "roberto");

      VAppConfiguration config = new VAppConfiguration().changeNameTo("roberto");

      GeneratedHttpRequest request = requestForArgs(ImmutableList.<Object> of(vApp, config));
      BindVAppConfigurationToXmlPayload binder = injector.getInstance(BindVAppConfigurationToXmlPayload.class);

      Map<String, Object> map = Maps.newHashMap();
      assertEquals(binder.bindToRequest(request, map).getPayload().getRawContent(), expected);
   }

   public void testRemoveDisk() throws IOException {
      VAppImpl vApp = new VAppImpl("MyAppServer6", URI
               .create("https://services.vcloudexpress/terremark.com/api/v0.8/vapp/4213"), Status.OFF, 4194304l, null,
               ImmutableListMultimap.<String, String> of(), null, null, null, ImmutableSet.of(
                        ResourceAllocationSettingData.builder().instanceID("1").elementName("foo").resourceType(
                                 ResourceType.PROCESSOR).virtualQuantity(2l).build(),//
                        ResourceAllocationSettingData.builder().instanceID("2").elementName("foo").resourceType(
                                 ResourceType.MEMORY).virtualQuantity(1024l).build(), //
                        ResourceAllocationSettingData.builder().instanceID("9").elementName("foo").addressOnParent("0")
                                 .hostResource("1048576").resourceType(ResourceType.DISK_DRIVE)
                                 .virtualQuantity(209152l).build(),//
                        ResourceAllocationSettingData.builder().instanceID("9").elementName("foo").addressOnParent("1")
                                 .hostResource("1048576").resourceType(ResourceType.DISK_DRIVE)
                                 .virtualQuantity(209152l).build()//
                        )

      );

      String expected = Strings2.toStringAndClose(getClass().getResourceAsStream("/configureVApp.xml"))
               .replace("eduardo", "MyAppServer6");

      VAppConfiguration config = new VAppConfiguration().deleteDiskWithAddressOnParent(1);
      
      GeneratedHttpRequest request = requestForArgs(ImmutableList.<Object> of(vApp, config));

      BindVAppConfigurationToXmlPayload binder = injector.getInstance(BindVAppConfigurationToXmlPayload.class);

      Map<String, Object> map = Maps.newHashMap();
      assertEquals(binder.bindToRequest(request, map).getPayload().getRawContent(), expected);
   }

   public void testChangeAll() throws IOException {
      VAppImpl vApp = new VAppImpl("MyAppServer6", URI
               .create("https://services.vcloudexpress/terremark.com/api/v0.8/vapp/4213"), Status.OFF, 4194304l, null,
               ImmutableListMultimap.<String, String> of(), null, null, null, ImmutableSet.of(
                        ResourceAllocationSettingData.builder().instanceID("1").elementName("foo").resourceType(
                                 ResourceType.PROCESSOR).virtualQuantity(2l).build(), ResourceAllocationSettingData
                                 .builder().instanceID("2").elementName("foo").resourceType(ResourceType.MEMORY)
                                 .virtualQuantity(1024l).build(), ResourceAllocationSettingData.builder().instanceID(
                                 "9").elementName("foo").addressOnParent("0").hostResource("1048576").resourceType(
                                 ResourceType.DISK_DRIVE).virtualQuantity(209152l).build()));

      String expected = Strings2.toStringAndClose(getClass().getResourceAsStream("/configureVAppAll.xml"));

      VAppConfiguration config = changeNameTo("eduardo").changeMemoryTo(1536).changeProcessorCountTo(1).addDisk(
               25 * 1048576).addDisk(25 * 1048576);

      GeneratedHttpRequest request = requestForArgs(ImmutableList.<Object> of(vApp, config));

      BindVAppConfigurationToXmlPayload binder = injector.getInstance(BindVAppConfigurationToXmlPayload.class);

      Map<String, Object> map = Maps.newHashMap();
      assertEquals(binder.bindToRequest(request, map).getPayload().getRawContent(), expected);
   }

   public void testChangeCPUCountTo4() throws IOException {
      VAppImpl vApp = new VAppImpl("eduardo", URI
               .create("https://services.vcloudexpress/terremark.com/api/v0.8/vapp/4213"), Status.OFF, 4194304l, null,
               ImmutableListMultimap.<String, String> of(), null, null, null, ImmutableSet.of(
                        ResourceAllocationSettingData.builder().instanceID("1").elementName("foo").resourceType(
                                 ResourceType.PROCESSOR).virtualQuantity(4l).build(), ResourceAllocationSettingData
                                 .builder().instanceID("2").elementName("foo").resourceType(ResourceType.MEMORY)
                                 .virtualQuantity(1024l).build(), ResourceAllocationSettingData.builder().instanceID(
                                 "9").elementName("foo").addressOnParent("0").hostResource("1048576").resourceType(
                                 ResourceType.DISK_DRIVE).virtualQuantity(209152l).build()));
      String expected = Strings2.toStringAndClose(getClass().getResourceAsStream("/configureVApp4.xml"));

      VAppConfiguration config = new VAppConfiguration().changeProcessorCountTo(4);

      GeneratedHttpRequest request = requestForArgs(ImmutableList.<Object> of(vApp, config));

      BindVAppConfigurationToXmlPayload binder = injector.getInstance(BindVAppConfigurationToXmlPayload.class);

      Map<String, Object> map = Maps.newHashMap();
      assertEquals(binder.bindToRequest(request, map).getPayload().getRawContent(), expected);
   }

   public void testChangeMemoryTo1536() throws IOException {
      VAppImpl vApp = new VAppImpl("MyAppServer6", URI
               .create("https://services.vcloudexpress/terremark.com/api/v0.8/vapp/4213"), Status.OFF, 4194304l, null,
               ImmutableListMultimap.<String, String> of(), null, null, null, ImmutableSet.of(
                        ResourceAllocationSettingData.builder().instanceID("1").elementName("foo").resourceType(
                                 ResourceType.PROCESSOR).virtualQuantity(2l).build(), ResourceAllocationSettingData
                                 .builder().instanceID("2").elementName("foo").resourceType(ResourceType.MEMORY)
                                 .virtualQuantity(1536l).build(), ResourceAllocationSettingData.builder().instanceID(
                                 "9").elementName("foo").addressOnParent("0").hostResource("1048576").resourceType(
                                 ResourceType.DISK_DRIVE).virtualQuantity(209152l).build()));

      String expected = Strings2.toStringAndClose(getClass().getResourceAsStream("/configureVApp.xml"))
               .replace("eduardo", "MyAppServer6").replace("1024", "1536");

      VAppConfiguration config = new VAppConfiguration().changeMemoryTo(1536);

      GeneratedHttpRequest request = requestForArgs(ImmutableList.<Object> of(vApp, config));

      BindVAppConfigurationToXmlPayload binder = injector.getInstance(BindVAppConfigurationToXmlPayload.class);

      Map<String, Object> map = Maps.newHashMap();
      assertEquals(binder.bindToRequest(request, map).getPayload().getRawContent(), expected);
   }
}
