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

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.trmk.vcloud_0_8.domain.ReferenceType;
import org.jclouds.trmk.vcloud_0_8.domain.internal.ReferenceTypeImpl;
import org.jclouds.trmk.vcloud_0_8.endpoints.Network;
import org.jclouds.trmk.vcloud_0_8.internal.BasePayloadTest;
import org.jclouds.trmk.vcloud_0_8.internal.TerremarkVCloudApiMetadata;
import org.jclouds.trmk.vcloud_0_8.options.InstantiateVAppTemplateOptions;
import org.jclouds.trmk.vcloud_0_8.options.InstantiateVAppTemplateOptions.NetworkConfig;
import org.jclouds.util.Strings2;
import org.nnsoft.guice.rocoto.Rocoto;
import org.nnsoft.guice.rocoto.configuration.ConfigurationModule;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
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
   Injector injector = Guice.createInjector(Rocoto.expandVariables(new ConfigurationModule() {

      @Override
      protected void bindConfigurations() {
         bindProperties(TerremarkVCloudApiMetadata.defaultProperties());
      }

      @Network
      @Provides
      @Singleton
      Supplier<ReferenceType> provideNetwork() {
         return Suppliers.<ReferenceType> ofInstance(new ReferenceTypeImpl(null, null, URI
               .create("https://vcloud.safesecureweb.com/network/1990")));
      }
   }));

   public void testAllOptions() throws IOException {

      String expected = Strings2.toStringAndClose(getClass().getResourceAsStream(
            "/InstantiateVAppTemplateParams-options-test.xml"));

      GeneratedHttpRequest request = requestForArgs(ImmutableList.<Object> of(InstantiateVAppTemplateOptions.Builder
            .processorCount(2).memory(512).inGroup("group").withPassword("password").inRow("row")
            .addNetworkConfig(new NetworkConfig(URI.create("http://network")))));

      BindInstantiateVAppTemplateParamsToXmlPayload binder = injector
            .getInstance(BindInstantiateVAppTemplateParamsToXmlPayload.class);

      Map<String, Object> map = Maps.newHashMap();
      map.put("name", "name");
      map.put("template", "https://vcloud/vAppTemplate/3");
      assertEquals(binder.bindToRequest(request, map).getPayload().getRawContent(), expected);
   }

}
