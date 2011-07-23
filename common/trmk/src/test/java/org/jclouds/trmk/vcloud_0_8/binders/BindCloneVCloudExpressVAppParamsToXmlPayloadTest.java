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

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Properties;

import org.jclouds.PropertiesBuilder;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.trmk.vcloud_0_8.binders.BindCloneVCloudExpressVAppParamsToXmlPayload;
import org.jclouds.trmk.vcloud_0_8.options.CloneVAppOptions;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
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
public class BindCloneVCloudExpressVAppParamsToXmlPayloadTest {
   Injector injector = Guice.createInjector(new AbstractModule() {

      @Override
      protected void configure() {
         Properties props = new Properties();
         props.setProperty("jclouds.vcloud.xml.ns", "http://www.vmware.com/vcloud/v0.8");
         props.setProperty("jclouds.vcloud.xml.schema", "http://vcloud.safesecureweb.com/ns/vcloud.xsd");
         Names.bindProperties(binder(), new PropertiesBuilder(props).build());
      }
   });

   public void testWithDescriptionDeployOn() throws IOException {
      String expected = Strings2.toStringAndClose(getClass().getResourceAsStream("/cloneVApp.xml"));

      CloneVAppOptions options = new CloneVAppOptions().deploy().powerOn().withDescription(
               "The description of the new vApp");
      GeneratedHttpRequest<?> request = createMock(GeneratedHttpRequest.class);
      expect(request.getEndpoint()).andReturn(URI.create("http://localhost/key")).anyTimes();
      expect(request.getArgs()).andReturn(ImmutableList.<Object> of(options)).atLeastOnce();
      request.setPayload(expected);
      replay(request);

      BindCloneVCloudExpressVAppParamsToXmlPayload binder = injector.getInstance(BindCloneVCloudExpressVAppParamsToXmlPayload.class);

      Map<String, String> map = Maps.newHashMap();
      map.put("newName", "new-linux-server");
      map.put("vApp", "https://vcloud.safesecureweb.com/api/v0.8/vapp/201");
      binder.bindToRequest(request, map);
      verify(request);
   }

   public void testDefault() throws IOException {
      String expected = Strings2.toStringAndClose(getClass().getResourceAsStream("/cloneVApp-default.xml"));

      GeneratedHttpRequest<?> request = createMock(GeneratedHttpRequest.class);
      expect(request.getEndpoint()).andReturn(URI.create("http://localhost/key")).anyTimes();
      expect(request.getArgs()).andReturn(ImmutableList.<Object> of()).atLeastOnce();
      request.setPayload(expected);
      replay(request);

      BindCloneVCloudExpressVAppParamsToXmlPayload binder = injector.getInstance(BindCloneVCloudExpressVAppParamsToXmlPayload.class);

      Map<String, String> map = Maps.newHashMap();
      map.put("newName", "my-vapp");
      map.put("vApp", "https://vcloud.safesecureweb.com/api/v0.8/vapp/4181");
      binder.bindToRequest(request, map);
      verify(request);
   }
}
