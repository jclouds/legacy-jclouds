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
package org.jclouds.gogrid.services;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Properties;

import javax.inject.Singleton;

import org.jclouds.encryption.EncryptionService;
import org.jclouds.gogrid.GoGrid;
import org.jclouds.gogrid.GoGridPropertiesBuilder;
import org.jclouds.gogrid.domain.IpType;
import org.jclouds.gogrid.filters.SharedKeyLiteAuthentication;
import org.jclouds.gogrid.functions.ParseIpListFromJsonResponse;
import org.jclouds.gogrid.options.GetIpListOptions;
import org.jclouds.logging.Logger;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import com.google.inject.name.Names;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * @author Oleksiy Yarmula
 */
public class GridIpAsyncClientTest extends RestClientTest<GridIpAsyncClient> {

   @Test
   public void testGetIpListWithOptions() throws NoSuchMethodException, IOException {
      Method method = GridIpAsyncClient.class.getMethod("getIpList", GetIpListOptions[].class);
      GeneratedHttpRequest<GridIpAsyncClient> httpRequest = processor.createRequest(method,
               new GetIpListOptions().onlyUnassigned().onlyWithType(IpType.PUBLIC));

      assertRequestLineEquals(httpRequest,
               "GET https://api.gogrid.com/api/grid/ip/list?v=1.3&ip.state=Unassigned&"
                        + "ip.type=Public HTTP/1.1");
      assertHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null);

      assertResponseParserClassEquals(method, httpRequest, ParseIpListFromJsonResponse.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);
      Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(httpRequest,
               "GET https://api.gogrid.com/api/grid/ip/list?v=1.3&ip.state=Unassigned&"
                        + "ip.type=Public&sig=3f446f171455fbb5574aecff4997b273&api_key=foo "
                        + "HTTP/1.1");
      assertHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null);
   }

   @Test
   public void testGetAssignedIpList() throws NoSuchMethodException, IOException {
      Method method = GridIpAsyncClient.class.getMethod("getAssignedIpList");
      GeneratedHttpRequest<GridIpAsyncClient> httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest,
               "GET https://api.gogrid.com/api/grid/ip/list?v=1.3&ip.state=Assigned HTTP/1.1");
      assertHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null);

      assertResponseParserClassEquals(method, httpRequest, ParseIpListFromJsonResponse.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);
      Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(httpRequest,
               "GET https://api.gogrid.com/api/grid/ip/list?v=1.3&ip.state=Assigned&"
                        + "sig=3f446f171455fbb5574aecff4997b273&api_key=foo " + "HTTP/1.1");
      assertHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null);
   }

   @Override
   protected void checkFilters(GeneratedHttpRequest<GridIpAsyncClient> httpMethod) {
      assertEquals(httpMethod.getFilters().size(), 1);
      assertEquals(httpMethod.getFilters().get(0).getClass(), SharedKeyLiteAuthentication.class);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<GridIpAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<GridIpAsyncClient>>() {
      };
   }

   @Override
   protected Module createModule() {
      return new AbstractModule() {
         @Override
         protected void configure() {
            Names.bindProperties(binder(), checkNotNull(new GoGridPropertiesBuilder(
                     new Properties()).build(), "properties"));
            bind(URI.class).annotatedWith(GoGrid.class).toInstance(
                     URI.create("https://api.gogrid.com/api"));
            bind(Logger.LoggerFactory.class).toInstance(new Logger.LoggerFactory() {
               public Logger getLogger(String category) {
                  return Logger.NULL;
               }
            });
         }

         @Provides
         @Singleton
         @SuppressWarnings("unused")
         public SharedKeyLiteAuthentication provideAuthentication(
                  EncryptionService encryptionService) throws UnsupportedEncodingException {
            return new SharedKeyLiteAuthentication("foo", "bar", 1267243795L, encryptionService);
         }
      };
   }
}
