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

import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;

/**
 * Tests behavior of {@code BindDeployVAppParamsToXmlPayload}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class BindDeployVAppParamsToXmlPayloadTest {
   Injector injector = Guice.createInjector(new AbstractModule() {

      @Override
      protected void configure() {
         Properties props = new Properties();
         Names.bindProperties(binder(), checkNotNull(new VCloudPropertiesBuilder(props).build(), "properties"));
      }
   });

   public void testPowerOnTrue() throws IOException {
      String expected = "<DeployVAppParams xmlns=\"http://www.vmware.com/vcloud/v1\" powerOn=\"true\"/>";

      GeneratedHttpRequest<?> request = createMock(GeneratedHttpRequest.class);
      expect(request.getEndpoint()).andReturn(URI.create("http://localhost/key")).anyTimes();
      request.setPayload(expected);
      replay(request);

      BindDeployVAppParamsToXmlPayload binder = injector.getInstance(BindDeployVAppParamsToXmlPayload.class);

      Map<String, String> map = Maps.newHashMap();
      map.put("powerOn", "true");
      binder.bindToRequest(request, map);
      verify(request);
   }

   public void testDefault() throws IOException {
      String expected = "<DeployVAppParams xmlns=\"http://www.vmware.com/vcloud/v1\"/>";

      GeneratedHttpRequest<?> request = createMock(GeneratedHttpRequest.class);
      expect(request.getEndpoint()).andReturn(URI.create("http://localhost/key")).anyTimes();
      request.setPayload(expected);
      replay(request);

      BindDeployVAppParamsToXmlPayload binder = injector.getInstance(BindDeployVAppParamsToXmlPayload.class);

      Map<String, String> map = Maps.newHashMap();

      binder.bindToRequest(request, map);
      verify(request);
   }
}
