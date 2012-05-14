/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.vcloud.binders;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.vcloud.VCloudApiMetadata;
import org.nnsoft.guice.rocoto.Rocoto;
import org.nnsoft.guice.rocoto.configuration.ConfigurationModule;
import org.testng.annotations.Test;

import com.google.common.collect.Maps;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code BindDeployVAppParamsToXmlPayload}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class BindDeployVAppParamsToXmlPayloadTest {
   Injector injector = Guice.createInjector(Rocoto.expandVariables(new ConfigurationModule() {

      @Override
      protected void bindConfigurations() {
         bindProperties(new VCloudApiMetadata().getDefaultProperties());
      }
   }));

   public void testPowerOnTrue() throws IOException {
      String expected = "<DeployVAppParams xmlns=\"http://www.vmware.com/vcloud/v1\" powerOn=\"true\"/>";

      GeneratedHttpRequest<?> request = createMock(GeneratedHttpRequest.class);
      expect(request.getEndpoint()).andReturn(URI.create("http://localhost/key")).anyTimes();
      request.setPayload(expected);
      replay(request);

      BindDeployVAppParamsToXmlPayload binder = injector.getInstance(BindDeployVAppParamsToXmlPayload.class);

      Map<String, Object> map = Maps.newHashMap();
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

      Map<String, Object> map = Maps.newHashMap();

      binder.bindToRequest(request, map);
      verify(request);
   }
}
