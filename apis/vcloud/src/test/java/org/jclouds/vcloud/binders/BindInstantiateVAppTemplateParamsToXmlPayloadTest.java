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
package org.jclouds.vcloud.binders;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.jclouds.vcloud.options.InstantiateVAppTemplateOptions.Builder.addNetworkConfig;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_DEFAULT_FENCEMODE;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.util.Strings2;
import org.jclouds.vcloud.VCloudApiMetadata;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.VAppTemplate;
import org.jclouds.vcloud.domain.internal.ReferenceTypeImpl;
import org.jclouds.vcloud.domain.network.FenceMode;
import org.jclouds.vcloud.domain.network.NetworkConfig;
import org.jclouds.vcloud.endpoints.Network;
import org.jclouds.vcloud.internal.BasePayloadTest;
import org.jclouds.vcloud.options.InstantiateVAppTemplateOptions;
import org.nnsoft.guice.rocoto.Rocoto;
import org.nnsoft.guice.rocoto.configuration.ConfigurationModule;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;

/**
 * Tests behavior of {@code BindInstantiateVAppTemplateParamsToXmlPayload}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class BindInstantiateVAppTemplateParamsToXmlPayloadTest extends BasePayloadTest {
   Injector createInjector(final URI vAppTemplate, final VAppTemplate value) {

      return Guice.createInjector(Rocoto.expandVariables(new ConfigurationModule() {

         @Provides
         @Singleton
         @Network
         protected Function<VAppTemplate, String> templateToDefaultNetworkName() {
            return Functions.forMap(ImmutableMap.of(value, "vAppNet-vApp Internal"));
         }

         @Provides
         @Singleton
         protected LoadingCache<URI, VAppTemplate> templateIdToVAppTemplate() {
            return CacheBuilder.newBuilder().build(
                     CacheLoader.from(Functions.forMap(ImmutableMap.of(vAppTemplate, value))));
         }

         @Override
         protected void bindConfigurations() {
            bindProperties(new VCloudApiMetadata().getDefaultProperties());
         }

         @Provides
         @Singleton
         public FenceMode defaultFenceMode(@Named(PROPERTY_VCLOUD_DEFAULT_FENCEMODE) String fenceMode) {
            return FenceMode.fromValue(fenceMode);
         }

         @Network
         @Provides
         @Singleton
         Supplier<ReferenceType> provideNetwork() {
            return Suppliers.<ReferenceType>ofInstance(new ReferenceTypeImpl(null, null, URI
                     .create("https://vcenterprise.bluelock.com/api/v1.0/network/1990")));
         }
      }));
   }

   public void testDefault() throws IOException {
      URI templateUri = URI.create("https://vcenterprise.bluelock.com/api/v1.0/vAppTemplate/3");
      VAppTemplate template = createMock(VAppTemplate.class);
      replay(template);

      String expected = Strings2.toStringAndClose(getClass().getResourceAsStream("/instantiationparams.xml"));
      GeneratedHttpRequest request = requestForArgs(ImmutableList.<Object> of(new InstantiateVAppTemplateOptions()));

      BindInstantiateVAppTemplateParamsToXmlPayload binder = createInjector(templateUri, template).getInstance(
               BindInstantiateVAppTemplateParamsToXmlPayload.class);

      Map<String, Object> map = Maps.newHashMap();
      map.put("name", "my-vapp");
      map.put("template", templateUri.toASCIIString());
      assertEquals(binder.bindToRequest(request, map).getPayload().getRawContent(), expected);
   }

   public void testDescription() throws IOException {
      URI templateUri = URI.create("https://vcenterprise.bluelock.com/api/v1.0/vAppTemplate/3");
      VAppTemplate template = createMock(VAppTemplate.class);
      replay(template);

      String expected = Strings2.toStringAndClose(getClass()
               .getResourceAsStream("/instantiationparams-description.xml"));
      GeneratedHttpRequest request = requestForArgs(ImmutableList.<Object> of(new InstantiateVAppTemplateOptions().description("my foo")));

      BindInstantiateVAppTemplateParamsToXmlPayload binder = createInjector(templateUri, template).getInstance(
               BindInstantiateVAppTemplateParamsToXmlPayload.class);

      Map<String, Object> map = Maps.newHashMap();
      map.put("name", "my-vapp");
      map.put("template", templateUri.toASCIIString());
      assertEquals(binder.bindToRequest(request, map).getPayload().getRawContent(), expected);
   }

   public void testWhenTemplateDoesntExist() throws IOException {
      URI templateUri = URI.create("https://vcenterprise.bluelock.com/api/v1.0/vAppTemplate/3");
      VAppTemplate template = createMock(VAppTemplate.class);
      replay(template);

      String expected = Strings2.toStringAndClose(getClass().getResourceAsStream("/instantiationparams.xml"));
      GeneratedHttpRequest request = requestForArgs(ImmutableList.<Object> of());

      BindInstantiateVAppTemplateParamsToXmlPayload binder = createInjector(templateUri, template).getInstance(
               BindInstantiateVAppTemplateParamsToXmlPayload.class);

      Map<String, Object> map = Maps.newHashMap();
      map.put("name", "my-vapp");
      map.put("template", templateUri.toASCIIString());
      assertEquals(binder.bindToRequest(request, map).getPayload().getRawContent(), expected);
   }

   public void testWithNetworkNameFenceMode() throws IOException {
      URI templateUri = URI.create("https://vcenterprise.bluelock.com/api/v1.0/vAppTemplate/3");
      VAppTemplate template = createMock(VAppTemplate.class);
      replay(template);

      InstantiateVAppTemplateOptions options = addNetworkConfig(new NetworkConfig("aloha", URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/network/1991"), FenceMode.NAT_ROUTED));

      String expected = Strings2.toStringAndClose(getClass().getResourceAsStream("/instantiationparams-network.xml"));
      GeneratedHttpRequest request = requestForArgs(ImmutableList.<Object> of(options));

      BindInstantiateVAppTemplateParamsToXmlPayload binder = createInjector(templateUri, template).getInstance(
               BindInstantiateVAppTemplateParamsToXmlPayload.class);

      Map<String, Object> map = Maps.newHashMap();
      map.put("name", "my-vapp");
      map.put("template", "https://vcenterprise.bluelock.com/api/v1.0/vAppTemplate/3");

      assertEquals(binder.bindToRequest(request, map).getPayload().getRawContent(), expected);
   }
}
