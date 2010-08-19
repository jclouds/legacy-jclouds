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

package org.jclouds.vcloud.terremark.binders;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Properties;

import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.util.Utils;
import org.jclouds.vcloud.VCloudPropertiesBuilder;
import org.jclouds.vcloud.domain.ResourceAllocation;
import org.jclouds.vcloud.domain.ResourceType;
import org.jclouds.vcloud.domain.VAppStatus;
import org.jclouds.vcloud.domain.internal.VAppImpl;
import org.jclouds.vcloud.terremark.domain.VAppConfiguration;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;

/**
 * Tests behavior of {@code BindVAppConfigurationToXmlPayload}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "vcloud.BindVAppConfigurationToXmlPayloadTest")
public class BindVAppConfigurationToXmlPayloadTest {
   Injector injector = Guice.createInjector(new AbstractModule() {

      @Override
      protected void configure() {
         Properties props = new Properties();
         Names.bindProperties(binder(), checkNotNull(new VCloudPropertiesBuilder(props).build(), "properties"));
      }
   });

   public void testChangeName() throws IOException {
      VAppImpl vApp = new VAppImpl("MyAppServer6", URI
               .create("https://services.vcloudexpress/terremark.com/api/v0.8/vapp/4213"), VAppStatus.OFF, 4194304l,
               null, ImmutableListMultimap.<String, String> of(), null, null, null, ImmutableSet.of(
                        new ResourceAllocation(1, "n/a", null, ResourceType.PROCESSOR, null, null, null, null, null,
                                 null, 2, null), new ResourceAllocation(2, "n/a", null, ResourceType.MEMORY, null,
                                 null, null, null, null, null, 1024, null), new ResourceAllocation(9, "n/a", null,
                                 ResourceType.DISK_DRIVE, null, "1048576", null, 0, null, null, 209152, null)));

      String expected = Utils.toStringAndClose(getClass().getResourceAsStream("/terremark/configureVApp.xml")).replace(
               "eduardo", "roberto");

      VAppConfiguration config = new VAppConfiguration().changeNameTo("roberto");

      GeneratedHttpRequest<?> request = createMock(GeneratedHttpRequest.class);
      expect(request.getEndpoint()).andReturn(URI.create("http://localhost/key")).anyTimes();
      expect(request.getArgs()).andReturn(new Object[] { vApp, config }).atLeastOnce();
      request.setPayload(expected);
      replay(request);

      BindVAppConfigurationToXmlPayload binder = injector.getInstance(BindVAppConfigurationToXmlPayload.class);

      Map<String, String> map = Maps.newHashMap();
      binder.bindToRequest(request, map);
      verify(request);
   }

   public void testRemoveDisk() throws IOException {
      VAppImpl vApp = new VAppImpl("MyAppServer6", URI
               .create("https://services.vcloudexpress/terremark.com/api/v0.8/vapp/4213"), VAppStatus.OFF, 4194304l,
               null, ImmutableListMultimap.<String, String> of(), null, null, null, ImmutableSet.of(
                        new ResourceAllocation(1, "n/a", null, ResourceType.PROCESSOR, null, null, null, null, null,
                                 null, 2, null), new ResourceAllocation(2, "n/a", null, ResourceType.MEMORY, null,
                                 null, null, null, null, null, 1024, null), new ResourceAllocation(9, "n/a", null,
                                 ResourceType.DISK_DRIVE, null, "1048576", null, 0, null, null, 209152, null),
                        new ResourceAllocation(9, "n/a", null, ResourceType.DISK_DRIVE, null, "1048576", null, 1, null,
                                 null, 209152, null)));

      String expected = Utils.toStringAndClose(getClass().getResourceAsStream("/terremark/configureVApp.xml")).replace(
               "eduardo", "MyAppServer6");

      VAppConfiguration config = new VAppConfiguration().deleteDiskWithAddressOnParent(1);

      GeneratedHttpRequest<?> request = createMock(GeneratedHttpRequest.class);
      expect(request.getEndpoint()).andReturn(URI.create("http://localhost/key")).anyTimes();
      expect(request.getArgs()).andReturn(new Object[] { vApp, config }).atLeastOnce();
      request.setPayload(expected);
      replay(request);

      BindVAppConfigurationToXmlPayload binder = injector.getInstance(BindVAppConfigurationToXmlPayload.class);

      Map<String, String> map = Maps.newHashMap();
      binder.bindToRequest(request, map);
      verify(request);
   }

   public void testChangeCPUCountTo4() throws IOException {
      VAppImpl vApp = new VAppImpl("eduardo", URI
               .create("https://services.vcloudexpress/terremark.com/api/v0.8/vapp/4213"), VAppStatus.OFF, 4194304l,
               null, ImmutableListMultimap.<String, String> of(), null, null, null, ImmutableSet.of(
                        new ResourceAllocation(1, "n/a", null, ResourceType.PROCESSOR, null, null, null, null, null,
                                 null, 4, null), new ResourceAllocation(2, "n/a", null, ResourceType.MEMORY, null,
                                 null, null, null, null, null, 1024, null), new ResourceAllocation(9, "n/a", null,
                                 ResourceType.DISK_DRIVE, null, "1048576", null, 0, null, null, 209152, null)));
      String expected = Utils.toStringAndClose(getClass().getResourceAsStream("/terremark/configureVApp4.xml"));

      VAppConfiguration config = new VAppConfiguration().changeProcessorCountTo(4);

      GeneratedHttpRequest<?> request = createMock(GeneratedHttpRequest.class);
      expect(request.getEndpoint()).andReturn(URI.create("http://localhost/key")).anyTimes();
      expect(request.getArgs()).andReturn(new Object[] { vApp, config }).atLeastOnce();
      request.setPayload(expected);
      replay(request);

      BindVAppConfigurationToXmlPayload binder = injector.getInstance(BindVAppConfigurationToXmlPayload.class);

      Map<String, String> map = Maps.newHashMap();
      binder.bindToRequest(request, map);
      verify(request);
   }

   public void testChangeMemoryTo1536() throws IOException {
      VAppImpl vApp = new VAppImpl("MyAppServer6", URI
               .create("https://services.vcloudexpress/terremark.com/api/v0.8/vapp/4213"), VAppStatus.OFF, 4194304l,
               null, ImmutableListMultimap.<String, String> of(), null, null, null, ImmutableSet.of(
                        new ResourceAllocation(1, "n/a", null, ResourceType.PROCESSOR, null, null, null, null, null,
                                 null, 2, null), new ResourceAllocation(2, "n/a", null, ResourceType.MEMORY, null,
                                 null, null, null, null, null, 1536, null), new ResourceAllocation(9, "n/a", null,
                                 ResourceType.DISK_DRIVE, null, "1048576", null, 0, null, null, 209152, null)));

      String expected = Utils.toStringAndClose(getClass().getResourceAsStream("/terremark/configureVApp.xml")).replace(
               "eduardo", "MyAppServer6").replace("1024", "1536");

      VAppConfiguration config = new VAppConfiguration().changeMemoryTo(1536);

      GeneratedHttpRequest<?> request = createMock(GeneratedHttpRequest.class);
      expect(request.getEndpoint()).andReturn(URI.create("http://localhost/key")).anyTimes();
      expect(request.getArgs()).andReturn(new Object[] { vApp, config }).atLeastOnce();
      request.setPayload(expected);
      replay(request);

      BindVAppConfigurationToXmlPayload binder = injector.getInstance(BindVAppConfigurationToXmlPayload.class);

      Map<String, String> map = Maps.newHashMap();
      binder.bindToRequest(request, map);
      verify(request);
   }
}
