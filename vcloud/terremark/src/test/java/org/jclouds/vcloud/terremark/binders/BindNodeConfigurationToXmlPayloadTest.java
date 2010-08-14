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

import org.jclouds.http.HttpRequest;
import org.jclouds.vcloud.terremark.TerremarkVCloudExpressPropertiesBuilder;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;

/**
 * Tests behavior of {@code BindNodeConfigurationToXmlPayload}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "vcloud.BindNodeConfigurationToXmlPayloadTest")
public class BindNodeConfigurationToXmlPayloadTest {
   Injector injector = Guice.createInjector(new AbstractModule() {

      @Override
      protected void configure() {
         Properties props = new Properties();
         Names.bindProperties(binder(), checkNotNull(new TerremarkVCloudExpressPropertiesBuilder(props).build(),
               "properties"));
      }
   });

   public void testChangeDescription() throws IOException {
      String expectedPayload = "<NodeService xmlns=\"urn:tmrk:vCloudExpressExtensions-1.6\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><Name>willie</Name><Enabled>true</Enabled><Description>description</Description></NodeService>";
      assertConfigMakesPayload(ImmutableMap.<String, String> of("name", "willie", "enabled", "true", "description",
            "description"), expectedPayload);
   }

   public void testDisableTraffic() throws IOException {
      String expectedPayload = "<NodeService xmlns=\"urn:tmrk:vCloudExpressExtensions-1.6\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><Name>willie</Name><Enabled>false</Enabled></NodeService>";
      assertConfigMakesPayload(ImmutableMap.<String, String> of("name", "willie", "enabled", "false"), expectedPayload);
   }

   public void testTwoOptions() throws IOException {
      String expectedPayload = "<NodeService xmlns=\"urn:tmrk:vCloudExpressExtensions-1.6\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><Name>willie</Name><Enabled>true</Enabled></NodeService>";
      assertConfigMakesPayload(ImmutableMap.<String, String> of("name", "willie", "enabled", "true"), expectedPayload);
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testNoOptions() throws IOException {
      String expectedPayload = "<NodeService xmlns=\"urn:tmrk:vCloudExpressExtensions-1.6\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><Name>willie</Name><Enabled>false</Enabled></NodeService>";
      assertConfigMakesPayload(ImmutableMap.<String, String> of(), expectedPayload);
   }

   private void assertConfigMakesPayload(Map<String, String> config, String expectedPayload) {
      BindNodeConfigurationToXmlPayload binder = injector.getInstance(BindNodeConfigurationToXmlPayload.class);
      HttpRequest request = createMock(HttpRequest.class);
      expect(request.getEndpoint()).andReturn(URI.create("http://localhost/key")).anyTimes();
      request.setPayload(expectedPayload);
      replay(request);
      binder.bindToRequest(request, config);
      verify(request);
   }

}
