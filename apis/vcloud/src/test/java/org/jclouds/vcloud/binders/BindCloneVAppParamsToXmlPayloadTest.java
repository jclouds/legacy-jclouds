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
import java.util.Properties;

import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.util.Strings2;
import org.jclouds.vcloud.VCloudApiMetadata;
import org.jclouds.vcloud.options.CloneVAppOptions;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;

/**
 * Tests behavior of {@code BindCloneVAppParamsToXmlPayload}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class BindCloneVAppParamsToXmlPayloadTest {
   Injector injector = Guice.createInjector(new AbstractModule() {

      @Override
      protected void configure() {
         Properties props = new VCloudApiMetadata().getDefaultProperties();
         props.setProperty("jclouds.vcloud.xml.ns", "http://www.vmware.com/vcloud/v1");
         props.setProperty("jclouds.vcloud.xml.schema", "http://vcloud.safesecureweb.com/ns/vcloud.xsd");
         Names.bindProperties(binder(), props);
      }
   });

   public void testWithDescriptionDeployOn() throws IOException {
      String expected = Strings2.toStringAndClose(getClass().getResourceAsStream("/copyVApp.xml"));

      CloneVAppOptions options = new CloneVAppOptions().deploy().powerOn().description(
               "The description of the new vApp");
      GeneratedHttpRequest request = createMock(GeneratedHttpRequest.class);
      expect(request.getEndpoint()).andReturn(URI.create("http://localhost/key")).anyTimes();
      expect(request.getArgs()).andReturn(ImmutableList.<Object> of(options)).atLeastOnce();
      request.setPayload(expected);
      replay(request);

      BindCloneVAppParamsToXmlPayload binder = injector.getInstance(BindCloneVAppParamsToXmlPayload.class);

      Builder<String, Object> map = ImmutableMap.builder();
      map.put("name", "new-linux-server");
      map.put("Source", "https://vcenterprise.bluelock.com/api/v1.0/vapp/201");
      binder.bindToRequest(request, map.build());
      verify(request);
   }

   public void testWithDescriptionDeployOnSourceDelete() throws IOException {
      String expected = Strings2.toStringAndClose(getClass().getResourceAsStream("/moveVApp.xml"));

      CloneVAppOptions options = new CloneVAppOptions().deploy().powerOn().description(
               "The description of the new vApp");
      GeneratedHttpRequest request = createMock(GeneratedHttpRequest.class);
      expect(request.getEndpoint()).andReturn(URI.create("http://localhost/key")).anyTimes();
      expect(request.getArgs()).andReturn(ImmutableList.<Object> of(options)).atLeastOnce();
      request.setPayload(expected);
      replay(request);

      BindCloneVAppParamsToXmlPayload binder = injector.getInstance(BindCloneVAppParamsToXmlPayload.class);

      Builder<String, Object> map = ImmutableMap.builder();
      map.put("name", "new-linux-server");
      map.put("Source", "https://vcenterprise.bluelock.com/api/v1.0/vapp/201");
      map.put("IsSourceDelete", "true");
      binder.bindToRequest(request, map.build());
      verify(request);
   }

   public void testDefault() throws IOException {
      String expected = Strings2.toStringAndClose(getClass().getResourceAsStream("/copyVApp-default.xml"));

      GeneratedHttpRequest request = createMock(GeneratedHttpRequest.class);
      expect(request.getEndpoint()).andReturn(URI.create("http://localhost/key")).anyTimes();
      expect(request.getArgs()).andReturn(ImmutableList.<Object> of()).atLeastOnce();
      request.setPayload(expected);
      replay(request);

      BindCloneVAppParamsToXmlPayload binder = injector.getInstance(BindCloneVAppParamsToXmlPayload.class);

      Builder<String, Object> map = ImmutableMap.builder();
      map.put("name", "my-vapp");
      map.put("Source", "https://vcenterprise.bluelock.com/api/v1.0/vapp/4181");
      binder.bindToRequest(request, map.build());
      verify(request);
   }
}
