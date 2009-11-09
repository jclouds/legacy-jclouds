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
package org.jclouds.vcloud.terremark;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;

import javax.inject.Provider;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.logging.Logger;
import org.jclouds.logging.Logger.LoggerFactory;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.vcloud.endpoints.Catalog;
import org.jclouds.vcloud.endpoints.VDC;
import org.jclouds.vcloud.filters.SetVCloudTokenCookie;
import org.jclouds.vcloud.terremark.xml.TerremarkVDCHandler;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code TerremarkVCloudClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "vcloud.TerremarkVCloudClientTest")
public class TerremarkVCloudClientTest extends RestClientTest<TerremarkVCloudClient> {

   public void testGetDefaultVDC() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TerremarkVCloudClient.class.getMethod("getDefaultVDC");
      GeneratedHttpRequest<TerremarkVCloudClient> httpMethod = processor.createRequest(method);

      assertRequestLineEquals(httpMethod, "GET http://vdc HTTP/1.1");
      assertHeadersEqual(httpMethod, "Accept: application/vnd.vmware.vcloud.vdc+xml\n");
      assertEntityEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TerremarkVDCHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   @Override
   protected void checkFilters(GeneratedHttpRequest<TerremarkVCloudClient> httpMethod) {
      assertEquals(httpMethod.getFilters().size(), 1);
      assertEquals(httpMethod.getFilters().get(0).getClass(), SetVCloudTokenCookie.class);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<TerremarkVCloudClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<TerremarkVCloudClient>>() {
      };
   }

   @Override
   protected Module createModule() {
      return new AbstractModule() {
         @Override
         protected void configure() {
            bind(URI.class).annotatedWith(Catalog.class).toInstance(URI.create("http://catalog"));
            bind(URI.class).annotatedWith(VDC.class).toInstance(URI.create("http://vdc"));
            bind(SetVCloudTokenCookie.class).toInstance(
                     new SetVCloudTokenCookie(new Provider<String>() {

                        public String get() {
                           return "token";
                        }

                     }));

            bind(Logger.LoggerFactory.class).toInstance(new LoggerFactory() {
               public Logger getLogger(String category) {
                  return Logger.NULL;
               }
            });
         }

      };
   }

}
