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
package org.jclouds.trmk.vcloud_0_8.binders;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Properties;

import javax.inject.Singleton;

import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.trmk.vcloud_0_8.TerremarkVCloudPropertiesBuilder;
import org.jclouds.trmk.vcloud_0_8.binders.TerremarkBindInstantiateVAppTemplateParamsToXmlPayload;
import org.jclouds.trmk.vcloud_0_8.domain.ReferenceType;
import org.jclouds.trmk.vcloud_0_8.domain.internal.ReferenceTypeImpl;
import org.jclouds.trmk.vcloud_0_8.domain.network.NetworkConfig;
import org.jclouds.trmk.vcloud_0_8.endpoints.Network;
import org.jclouds.trmk.vcloud_0_8.options.TerremarkInstantiateVAppTemplateOptions;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.name.Names;

/**
 * Tests behavior of {@code TerremarkBindInstantiateVAppTemplateParamsToXmlPayload}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class TerremarkBindInstantiateVAppTemplateParamsToXmlPayloadTest {
   Injector injector = Guice.createInjector(new AbstractModule() {

      @Override
      protected void configure() {
         Properties props = new Properties();
         Names.bindProperties(binder(), checkNotNull(new TerremarkVCloudPropertiesBuilder(props).build(), "properties"));
      }

      @SuppressWarnings("unused")
      @Network
      @Provides
      @Singleton
      ReferenceType provideNetwork() {
         return new ReferenceTypeImpl(null, null, URI.create("https://vcloud.safesecureweb.com/network/1990"));
      }
   });

   public void testAllOptions() throws IOException {

      String expected = Strings2.toStringAndClose(getClass().getResourceAsStream(
            "/InstantiateVAppTemplateParams-options-test.xml"));
      Multimap<String, String> headers = Multimaps.synchronizedMultimap(HashMultimap.<String, String> create());
      GeneratedHttpRequest<?> request = createMock(GeneratedHttpRequest.class);
      expect(request.getEndpoint()).andReturn(URI.create("http://localhost/key")).anyTimes();
      expect(request.getArgs()).andReturn(
            ImmutableList.<Object> of(TerremarkInstantiateVAppTemplateOptions.Builder.processorCount(2).memory(512)
                  .inGroup("group").withPassword("password").inRow("row")
                  .addNetworkConfig(new NetworkConfig(URI.create("http://network"))))).atLeastOnce();
      expect(request.getFirstHeaderOrNull("Content-Type")).andReturn("application/unknown").atLeastOnce();
      expect(request.getHeaders()).andReturn(headers).atLeastOnce();
      request.setPayload(expected);
      replay(request);

      TerremarkBindInstantiateVAppTemplateParamsToXmlPayload binder = injector
            .getInstance(TerremarkBindInstantiateVAppTemplateParamsToXmlPayload.class);

      Map<String, String> map = Maps.newHashMap();
      map.put("name", "name");
      map.put("template", "https://vcloud/vAppTemplate/3");
      binder.bindToRequest(request, map);
   }

}
