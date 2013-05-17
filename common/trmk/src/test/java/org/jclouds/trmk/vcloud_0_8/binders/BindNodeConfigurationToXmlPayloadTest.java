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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.jclouds.Constants.PROPERTY_API_VERSION;
import static org.jclouds.Constants.PROPERTY_ENDPOINT;
import static org.jclouds.trmk.vcloud_0_8.reference.TerremarkConstants.PROPERTY_TERREMARK_EXTENSION_NAME;
import static org.jclouds.trmk.vcloud_0_8.reference.TerremarkConstants.PROPERTY_TERREMARK_EXTENSION_VERSION;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Properties;

import org.jclouds.http.HttpRequest;
import org.jclouds.trmk.vcloud_0_8.internal.TerremarkVCloudApiMetadata;
import org.nnsoft.guice.rocoto.Rocoto;
import org.nnsoft.guice.rocoto.configuration.ConfigurationModule;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code BindNodeConfigurationToXmlPayload}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class BindNodeConfigurationToXmlPayloadTest {
   Injector injector = Guice.createInjector(Rocoto.expandVariables(new ConfigurationModule() {

      @Override
      protected void bindConfigurations() {
         Properties properties = TerremarkVCloudApiMetadata.defaultProperties();
         properties.setProperty(PROPERTY_API_VERSION, "0.8a-ext1.6");
         properties.setProperty(PROPERTY_TERREMARK_EXTENSION_NAME, "vCloudExpressExtensions");
         properties.setProperty(PROPERTY_TERREMARK_EXTENSION_VERSION, "1.6");
         properties.setProperty(PROPERTY_ENDPOINT, "https://services.vcloudexpress.terremark.com/api");
         bindProperties(properties);
      }
   }));

   public void testChangeDescription() throws IOException {
      String expectedPayload = "<NodeService xmlns=\"urn:tmrk:vCloudExpressExtensions-1.6\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><Name>willie</Name><Enabled>true</Enabled><Description>description</Description></NodeService>";
      assertConfigMakesPayload(ImmutableMap.<String, Object> of("name", "willie", "enabled", "true", "description",
            "description"), expectedPayload);
   }

   public void testDisableTraffic() throws IOException {
      String expectedPayload = "<NodeService xmlns=\"urn:tmrk:vCloudExpressExtensions-1.6\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><Name>willie</Name><Enabled>false</Enabled></NodeService>";
      assertConfigMakesPayload(ImmutableMap.<String, Object> of("name", "willie", "enabled", "false"), expectedPayload);
   }

   public void testTwoOptions() throws IOException {
      String expectedPayload = "<NodeService xmlns=\"urn:tmrk:vCloudExpressExtensions-1.6\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><Name>willie</Name><Enabled>true</Enabled></NodeService>";
      assertConfigMakesPayload(ImmutableMap.<String, Object> of("name", "willie", "enabled", "true"), expectedPayload);
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testNoOptions() throws IOException {
      String expectedPayload = "<NodeService xmlns=\"urn:tmrk:vCloudExpressExtensions-1.6\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><Name>willie</Name><Enabled>false</Enabled></NodeService>";
      assertConfigMakesPayload(ImmutableMap.<String, Object> of(), expectedPayload);
   }

   private void assertConfigMakesPayload(Map<String, Object> config, String expectedPayload) {
      BindNodeConfigurationToXmlPayload binder = injector.getInstance(BindNodeConfigurationToXmlPayload.class);
      HttpRequest request = createMock(HttpRequest.class);
      expect(request.getEndpoint()).andReturn(URI.create("http://localhost/key")).anyTimes();
      request.setPayload(expectedPayload);
      replay(request);
      binder.bindToRequest(request, config);
      verify(request);
   }

}
