/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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

import org.apache.commons.io.IOUtils;
import org.jclouds.http.HttpRequest;
import org.jclouds.util.Utils;
import org.testng.annotations.Test;

import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;

/**
 * Tests behavior of {@code BindAddInternetServiceToXmlPayload}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "vcloud.BindAddInternetServiceToXmlPayloadTest")
public class BindAddInternetServiceToXmlPayloadTest {
   Injector injector = Guice.createInjector(new AbstractModule() {

      @Override
      protected void configure() {
      }

      @SuppressWarnings("unused")
      @Singleton
      @Provides
      @Named("CreateInternetService")
      String provideInstantiateVAppTemplateParams() throws IOException {
         InputStream is = getClass().getResourceAsStream("/terremark/CreateInternetService.xml");
         return Utils.toStringAndClose(is);
      }
   });

   public void testApplyInputStream() throws IOException {
      String expected = IOUtils.toString(getClass().getResourceAsStream(
               "/terremark/CreateInternetService-test.xml"));
      HttpRequest request = new HttpRequest("GET", URI.create("http://test"));
      BindAddInternetServiceToXmlPayload binder = injector
               .getInstance(BindAddInternetServiceToXmlPayload.class);

      Map<String, String> map = Maps.newHashMap();
      map.put("name", "name");
      map.put("protocol", "TCP");
      map.put("port", "22");
      map.put("enabled", "true");
      map.put("description", "name TCP 22");
      binder.bindToRequest(request, map);
      assertEquals(request.getFirstHeaderOrNull(HttpHeaders.CONTENT_TYPE), "application/unknown");
      assertEquals(request.getFirstHeaderOrNull(HttpHeaders.CONTENT_LENGTH), "346");
      assertEquals(request.getPayload().getRawContent(), expected);

   }
}
