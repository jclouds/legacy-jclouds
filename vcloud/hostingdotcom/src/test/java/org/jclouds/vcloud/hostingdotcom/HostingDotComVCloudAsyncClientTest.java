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
package org.jclouds.vcloud.hostingdotcom;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;

import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.logging.Logger;
import org.jclouds.logging.Logger.LoggerFactory;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.util.Utils;
import org.jclouds.vcloud.endpoints.Catalog;
import org.jclouds.vcloud.endpoints.Network;
import org.jclouds.vcloud.endpoints.VCloudApi;
import org.jclouds.vcloud.endpoints.VDC;
import org.jclouds.vcloud.endpoints.internal.CatalogItemRoot;
import org.jclouds.vcloud.endpoints.internal.VAppRoot;
import org.jclouds.vcloud.filters.SetVCloudTokenCookie;
import org.jclouds.vcloud.xml.CatalogHandler;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code HostingDotComVCloudAsyncClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", sequential = true, testName = "vcloud.HostingDotComVCloudAsyncClientTest")
public class HostingDotComVCloudAsyncClientTest extends
         RestClientTest<HostingDotComVCloudAsyncClient> {
   public void testCatalog() throws SecurityException, NoSuchMethodException, IOException {
      Method method = HostingDotComVCloudAsyncClient.class.getMethod("getCatalog");
      GeneratedHttpRequest<HostingDotComVCloudAsyncClient> httpMethod = processor
               .createRequest(method);

      assertRequestLineEquals(httpMethod, "GET http://catalog HTTP/1.1");
      assertHeadersEqual(
               httpMethod,
               "Accept: application/vnd.vmware.vcloud.catalog+xml\nContent-Type: application/vnd.vmware.vcloud.catalog+xml\n");
      assertPayloadEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, CatalogHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   @Override
   protected void checkFilters(GeneratedHttpRequest<HostingDotComVCloudAsyncClient> httpMethod) {
      assertEquals(httpMethod.getFilters().size(), 1);
      assertEquals(httpMethod.getFilters().get(0).getClass(), SetVCloudTokenCookie.class);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<HostingDotComVCloudAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<HostingDotComVCloudAsyncClient>>() {
      };
   }

   @Override
   protected Module createModule() {
      return new AbstractModule() {
         @Override
         protected void configure() {
            bind(URI.class).annotatedWith(Catalog.class).toInstance(URI.create("http://catalog"));
            bind(String.class).annotatedWith(CatalogItemRoot.class)
                     .toInstance("http://catalogItem");
            bind(URI.class).annotatedWith(VCloudApi.class).toInstance(URI.create("http://vcloud"));
            bind(String.class).annotatedWith(VAppRoot.class).toInstance("http://vapp");
            bind(URI.class).annotatedWith(VDC.class).toInstance(URI.create("http://vdc"));
            bind(URI.class).annotatedWith(Network.class).toInstance(URI.create("http://network"));
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

         @SuppressWarnings("unused")
         @Singleton
         @Provides
         @Named("InstantiateVAppTemplateParams")
         String provideInstantiateVAppTemplateParams() throws IOException {
            return Utils.toStringAndClose(getClass().getResourceAsStream(
                     "/terremark/InstantiateVAppTemplateParams.xml"));
         }

         @SuppressWarnings("unused")
         @Singleton
         @Provides
         @Named("CreateInternetService")
         String provideCreateInternetService() throws IOException {
            return Utils.toStringAndClose(getClass().getResourceAsStream(
                     "/terremark/CreateInternetService.xml"));
         }

         @SuppressWarnings("unused")
         @Singleton
         @Provides
         @Named("CreateNodeService")
         String provideCreateNodeService() throws IOException {
            return Utils.toStringAndClose(getClass().getResourceAsStream(
                     "/terremark/CreateNodeService.xml"));
         }
      };
   }

}
