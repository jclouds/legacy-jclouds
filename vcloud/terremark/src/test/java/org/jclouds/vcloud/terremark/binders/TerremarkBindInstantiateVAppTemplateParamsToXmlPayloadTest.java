/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_DEFAULT_NETWORK;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Properties;

import org.jclouds.rest.internal.GeneratedHttpRequest;
import com.google.inject.name.Names;
import org.jclouds.util.Utils;
import org.jclouds.vcloud.terremark.TerremarkVCloudPropertiesBuilder;
import org.jclouds.vcloud.terremark.options.TerremarkInstantiateVAppTemplateOptions;
import org.testng.annotations.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code TerremarkBindInstantiateVAppTemplateParamsToXmlPayload}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "vcloud.TerremarkBindInstantiateVAppTemplateParamsToXmlPayloadTest")
public class TerremarkBindInstantiateVAppTemplateParamsToXmlPayloadTest {
   Injector injector = Guice.createInjector(new AbstractModule() {

      @Override
      protected void configure() {
         Properties props = new Properties();
         props
                  .put(PROPERTY_VCLOUD_DEFAULT_NETWORK,
                           "https://vcloud.safesecureweb.com/network/1990");
         Names.bindProperties(binder(), checkNotNull(new TerremarkVCloudPropertiesBuilder(props)
                  .build(), "properties"));
      }

   });

   public void testAllOptions() throws IOException {

      String expected = Utils.toStringAndClose(getClass().getResourceAsStream(
               "/terremark/InstantiateVAppTemplateParams-options-test.xml"));
      Multimap<String, String> headers = Multimaps.synchronizedMultimap(HashMultimap
               .<String, String> create());
      GeneratedHttpRequest<?> request = createMock(GeneratedHttpRequest.class);
      expect(request.getEndpoint()).andReturn(URI.create("http://localhost/key")).anyTimes();
      expect(request.getArgs()).andReturn(
               new Object[] { TerremarkInstantiateVAppTemplateOptions.Builder.processorCount(2)
                        .memory(512).inRow("row").inGroup("group").withPassword(
                                 "password").inNetwork(URI.create("http://network")) })
               .atLeastOnce();
      expect(request.getFirstHeaderOrNull("Content-Type")).andReturn("application/unknown")
               .atLeastOnce();
      expect(request.getHeaders()).andReturn(headers).atLeastOnce();
      request.setPayload(expected);
      replay(request);

      TerremarkBindInstantiateVAppTemplateParamsToXmlPayload binder = injector
               .getInstance(TerremarkBindInstantiateVAppTemplateParamsToXmlPayload.class);

      Map<String, String> map = Maps.newHashMap();
      map.put("name", "name");
      map.put("template", "https://vcloud/catalogItem/3");
      binder.bindToRequest(request, map);
   }

}
