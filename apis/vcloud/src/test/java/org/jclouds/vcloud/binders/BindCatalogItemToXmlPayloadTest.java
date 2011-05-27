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

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Properties;

import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.vcloud.VCloudPropertiesBuilder;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;

/**
 * Tests behavior of {@code BindCatalogItemToXmlPayload}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class BindCatalogItemToXmlPayloadTest {
   Injector injector = Guice.createInjector(new AbstractModule() {

      @Override
      protected void configure() {
         Properties props = new Properties();
         Names.bindProperties(binder(), checkNotNull(new VCloudPropertiesBuilder(props).build(), "properties"));
      }
   });

   public void testDefault() throws IOException {
      String expected = "<CatalogItem xmlns=\"http://www.vmware.com/vcloud/v1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" name=\"myname\" xsi:schemaLocation=\"http://www.vmware.com/vcloud/v1 http://vcloud.safesecureweb.com/ns/vcloud.xsd\"><Description>mydescription</Description><Entity href=\"http://fooentity\"/><Property key=\"foo\">bar</Property></CatalogItem>";

      GeneratedHttpRequest<?> request = createMock(GeneratedHttpRequest.class);
      expect(request.getEndpoint()).andReturn(URI.create("http://localhost/key")).anyTimes();
      expect(request.getArgs()).andReturn(ImmutableList.<Object> of(ImmutableMap.of("foo", "bar"))).anyTimes();
      request.setPayload(expected);
      replay(request);

      BindCatalogItemToXmlPayload binder = injector.getInstance(BindCatalogItemToXmlPayload.class);

      Map<String, String> map = ImmutableMap.of("name", "myname", "description", "mydescription", "entity",
            "http://fooentity");

      binder.bindToRequest(request, map);
      verify(request);
   }
}
