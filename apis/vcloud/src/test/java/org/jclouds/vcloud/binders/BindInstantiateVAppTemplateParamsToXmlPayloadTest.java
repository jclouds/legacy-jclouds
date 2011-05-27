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
package org.jclouds.vcloud.binders;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.jclouds.vcloud.options.InstantiateVAppTemplateOptions.Builder.addNetworkConfig;
import static org.jclouds.vcloud.options.InstantiateVAppTemplateOptions.Builder.customizeOnInstantiate;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Properties;

import javax.inject.Singleton;

import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.util.Strings2;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.VCloudPropertiesBuilder;
import org.jclouds.vcloud.domain.VAppTemplate;
import org.jclouds.vcloud.domain.network.FenceMode;
import org.jclouds.vcloud.domain.network.NetworkConfig;
import org.jclouds.vcloud.domain.ovf.VCloudNetworkSection;
import org.jclouds.vcloud.endpoints.Network;
import org.jclouds.vcloud.options.InstantiateVAppTemplateOptions;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.name.Names;

/**
 * Tests behavior of {@code BindInstantiateVAppTemplateParamsToXmlPayload}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class BindInstantiateVAppTemplateParamsToXmlPayloadTest {
   Injector createInjector(URI vAppTemplate, VAppTemplate value) {
      final VCloudClient client = createMock(VCloudClient.class);

      expect(client.getVAppTemplate(vAppTemplate)).andReturn(value).anyTimes();
      replay(client);

      return Guice.createInjector(new AbstractModule() {

         @Override
         protected void configure() {
            Properties props = new Properties();
            Names.bindProperties(binder(), checkNotNull(new VCloudPropertiesBuilder(props).build(), "properties"));
            bind(VCloudClient.class).toInstance(client);
         }

         @SuppressWarnings("unused")
         @Network
         @Provides
         @Singleton
         URI provideNetwork() {
            return URI.create("https://vcenterprise.bluelock.com/api/v1.0/network/1990");
         }
      });
   }

   public void testDefault() throws IOException {
      URI templateUri = URI.create("https://vcenterprise.bluelock.com/api/v1.0/vAppTemplate/3");
      VAppTemplate template = createMock(VAppTemplate.class);
      VCloudNetworkSection net = createMock(VCloudNetworkSection.class);

      String expected = Strings2.toStringAndClose(getClass().getResourceAsStream("/instantiationparams.xml"));
      GeneratedHttpRequest<?> request = createMock(GeneratedHttpRequest.class);
      expect(request.getEndpoint()).andReturn(URI.create("http://localhost/key")).anyTimes();
      expect(request.getArgs()).andReturn(ImmutableList.<Object> of(new InstantiateVAppTemplateOptions()))
            .atLeastOnce();
      request.setPayload(expected);

      expect(template.getNetworkSection()).andReturn(net).atLeastOnce();
      expect(net.getNetworks()).andReturn(
            ImmutableSet.<org.jclouds.ovf.Network> of(new org.jclouds.ovf.Network("vAppNet-vApp Internal", null)));

      replay(request);
      replay(template);
      replay(net);

      BindInstantiateVAppTemplateParamsToXmlPayload binder = createInjector(templateUri, template).getInstance(
            BindInstantiateVAppTemplateParamsToXmlPayload.class);

      Map<String, String> map = Maps.newHashMap();
      map.put("name", "my-vapp");
      map.put("template", templateUri.toASCIIString());
      binder.bindToRequest(request, map);

      verify(request);
      verify(template);
      verify(net);

   }

   public void testDescription() throws IOException {
      URI templateUri = URI.create("https://vcenterprise.bluelock.com/api/v1.0/vAppTemplate/3");
      VAppTemplate template = createMock(VAppTemplate.class);
      VCloudNetworkSection net = createMock(VCloudNetworkSection.class);

      String expected = Strings2.toStringAndClose(getClass().getResourceAsStream("/instantiationparams-description.xml"));
      GeneratedHttpRequest<?> request = createMock(GeneratedHttpRequest.class);
      expect(request.getEndpoint()).andReturn(URI.create("http://localhost/key")).anyTimes();
      expect(request.getArgs()).andReturn(
            ImmutableList.<Object> of(new InstantiateVAppTemplateOptions().description("my foo"))).atLeastOnce();
      request.setPayload(expected);

      expect(template.getNetworkSection()).andReturn(net).atLeastOnce();
      expect(net.getNetworks()).andReturn(
            ImmutableSet.<org.jclouds.ovf.Network> of(new org.jclouds.ovf.Network("vAppNet-vApp Internal", null)));

      replay(request);
      replay(template);
      replay(net);

      BindInstantiateVAppTemplateParamsToXmlPayload binder = createInjector(templateUri, template).getInstance(
            BindInstantiateVAppTemplateParamsToXmlPayload.class);

      Map<String, String> map = Maps.newHashMap();
      map.put("name", "my-vapp");
      map.put("template", templateUri.toASCIIString());
      binder.bindToRequest(request, map);

      verify(request);
      verify(template);
      verify(net);

   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testWhenTemplateDoesntExist() throws IOException {
      URI templateUri = URI.create("https://vcenterprise.bluelock.com/api/v1.0/vAppTemplate/3");
      VAppTemplate template = null;

      String expected = Strings2.toStringAndClose(getClass().getResourceAsStream("/instantiationparams.xml"));
      GeneratedHttpRequest<?> request = createMock(GeneratedHttpRequest.class);
      expect(request.getEndpoint()).andReturn(URI.create("http://localhost/key")).anyTimes();
      expect(request.getArgs()).andReturn(ImmutableList.<Object> of()).atLeastOnce();
      request.setPayload(expected);
      replay(request);

      BindInstantiateVAppTemplateParamsToXmlPayload binder = createInjector(templateUri, template).getInstance(
            BindInstantiateVAppTemplateParamsToXmlPayload.class);

      Map<String, String> map = Maps.newHashMap();
      map.put("name", "my-vapp");
      map.put("template", templateUri.toASCIIString());
      binder.bindToRequest(request, map);
      verify(request);

   }

   // TODO!!! figure out how to get this to work
   @Test(enabled = false)
   public void testWithProcessorMemoryDisk() throws IOException {
      URI templateUri = URI.create("https://vcenterprise.bluelock.com/api/v1.0/vAppTemplate/3");
      VAppTemplate template = null;

      InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();

      String expected = Strings2.toStringAndClose(getClass().getResourceAsStream("/instantiationparams.xml"));
      GeneratedHttpRequest<?> request = createMock(GeneratedHttpRequest.class);
      expect(request.getEndpoint()).andReturn(URI.create("http://localhost/key")).anyTimes();
      expect(request.getArgs()).andReturn(ImmutableList.<Object> of(options)).atLeastOnce();
      request.setPayload(expected);
      replay(request);

      BindInstantiateVAppTemplateParamsToXmlPayload binder = createInjector(templateUri, template).getInstance(
            BindInstantiateVAppTemplateParamsToXmlPayload.class);

      Map<String, String> map = Maps.newHashMap();
      map.put("name", "my-vapp");
      map.put("template", "https://vcenterprise.bluelock.com/api/v1.0/vAppTemplate/3");

      binder.bindToRequest(request, map);
      verify(request);

   }

   public void testWithNetworkNameFenceMode() throws IOException {
      URI templateUri = URI.create("https://vcenterprise.bluelock.com/api/v1.0/vAppTemplate/3");
      VAppTemplate template = null;

      InstantiateVAppTemplateOptions options = addNetworkConfig(new NetworkConfig("aloha",
            URI.create("https://vcenterprise.bluelock.com/api/v1.0/network/1991"), FenceMode.NAT_ROUTED));

      String expected = Strings2.toStringAndClose(getClass().getResourceAsStream("/instantiationparams-network.xml"));

      GeneratedHttpRequest<?> request = createMock(GeneratedHttpRequest.class);
      expect(request.getEndpoint()).andReturn(URI.create("http://localhost/key")).anyTimes();
      expect(request.getArgs()).andReturn(ImmutableList.<Object> of(options)).atLeastOnce();
      request.setPayload(expected);
      replay(request);

      BindInstantiateVAppTemplateParamsToXmlPayload binder = createInjector(templateUri, template).getInstance(
            BindInstantiateVAppTemplateParamsToXmlPayload.class);

      Map<String, String> map = Maps.newHashMap();
      map.put("name", "my-vapp");
      map.put("template", "https://vcenterprise.bluelock.com/api/v1.0/vAppTemplate/3");

      binder.bindToRequest(request, map);
      verify(request);
   }

   public void testWithCustomization() throws IOException {

      URI templateUri = URI.create("https://vcenterprise.bluelock.com/api/v1.0/vAppTemplate/3");
      VAppTemplate template = createMock(VAppTemplate.class);
      VCloudNetworkSection net = createMock(VCloudNetworkSection.class);
      InstantiateVAppTemplateOptions options = customizeOnInstantiate(true);

      String expected = Strings2.toStringAndClose(getClass().getResourceAsStream(
            "/instantiationparams-customization.xml"));

      GeneratedHttpRequest<?> request = createMock(GeneratedHttpRequest.class);
      expect(request.getEndpoint()).andReturn(URI.create("http://localhost/key")).anyTimes();
      expect(request.getArgs()).andReturn(ImmutableList.<Object> of(options)).atLeastOnce();
      request.setPayload(expected);

      expect(template.getNetworkSection()).andReturn(net).atLeastOnce();
      expect(net.getNetworks()).andReturn(
            ImmutableSet.<org.jclouds.ovf.Network> of(new org.jclouds.ovf.Network("vAppNet-vApp Internal", null)));

      replay(request);
      replay(template);
      replay(net);

      BindInstantiateVAppTemplateParamsToXmlPayload binder = createInjector(templateUri, template).getInstance(
            BindInstantiateVAppTemplateParamsToXmlPayload.class);

      Map<String, String> map = Maps.newHashMap();
      map.put("name", "my-vapp");
      map.put("template", "https://vcenterprise.bluelock.com/api/v1.0/vAppTemplate/3");

      binder.bindToRequest(request, map);

      verify(request);
      verify(template);
      verify(net);

   }
}
