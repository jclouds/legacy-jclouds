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

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.http.HttpRequest;
import org.jclouds.util.Utils;
import org.testng.annotations.Test;

import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;

/**
 * Tests behavior of {@code BindAddNodeServiceToXmlPayload}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "vcloud.BindAddNodeServiceToXmlPayloadTest")
public class BindAddNodeServiceToXmlPayloadTest {
   Injector injector = Guice.createInjector(new AbstractModule() {

      @Override
      protected void configure() {
      }

      @SuppressWarnings("unused")
      @Singleton
      @Provides
      @Named("CreateNodeService")
      String provideInstantiateVAppTemplateParams() throws IOException {
         InputStream is = getClass().getResourceAsStream("/terremark/CreateNodeService.xml");
         return Utils.toStringAndClose(is);
      }
   });

   public void testApplyInputStream() throws IOException {
      String expected = Utils.toStringAndClose(getClass().getResourceAsStream(
               "/terremark/CreateNodeService-test.xml"));
      HttpRequest request = new HttpRequest("GET", URI.create("http://test"));
      BindAddNodeServiceToXmlPayload binder = injector
               .getInstance(BindAddNodeServiceToXmlPayload.class);

      Map<String, String> map = Maps.newHashMap();
      map.put("name", "Node for Jim");
      map.put("ipAddress", "172.16.20.3");
      map.put("port", "80");
      map.put("enabled", "false");
      map.put("description", "Some test node");
      binder.bindToRequest(request, map);
      assertEquals(request.getFirstHeaderOrNull(HttpHeaders.CONTENT_TYPE), "application/unknown");
      assertEquals(request.getFirstHeaderOrNull(HttpHeaders.CONTENT_LENGTH), "356");
      assertEquals(request.getPayload().getRawContent(), expected);

   }
}
