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

import static org.jclouds.trmk.vcloud_0_8.reference.TerremarkConstants.PROPERTY_TERREMARK_EXTENSION_NS;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.http.HttpRequest;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.name.Names;

/**
 * Tests behavior of {@code BindAddNodeServiceToXmlPayload}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class BindAddNodeServiceToXmlPayloadTest {
   Injector injector = Guice.createInjector(new AbstractModule() {

      @Override
      protected void configure() {
         bindConstant().annotatedWith(Names.named(PROPERTY_TERREMARK_EXTENSION_NS)).to(
               "urn:tmrk:vCloudExpressExtensions-1.6");
      }

      @Singleton
      @Provides
      @Named("CreateNodeService")
      String provideInstantiateVAppTemplateParams() throws IOException {
         InputStream is = getClass().getResourceAsStream("/CreateNodeService.xml");
         return Strings2.toStringAndClose(is);
      }
   });

   public void testApplyInputStream() throws IOException {
      String expected = Strings2.toStringAndClose(getClass().getResourceAsStream("/CreateNodeService-test.xml"));
      HttpRequest request = HttpRequest.builder().method("GET").endpoint("http://test").build();
      BindAddNodeServiceToXmlPayload binder = injector.getInstance(BindAddNodeServiceToXmlPayload.class);

      Map<String, Object> map = Maps.newHashMap();
      map.put("name", "Node for Jim");
      map.put("ipAddress", "172.16.20.3");
      map.put("port", "80");
      map.put("enabled", "false");
      map.put("description", "Some test node");
      assertEquals(binder.bindToRequest(request, map).getPayload().getRawContent(), expected);
   }
}
