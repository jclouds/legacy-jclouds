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
package org.jclouds.vcloud;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Properties;

import javax.ws.rs.core.HttpHeaders;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.logging.Logger;
import org.jclouds.logging.Logger.LoggerFactory;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.util.Jsr330;
import org.jclouds.vcloud.endpoints.VCloud;
import org.jclouds.vcloud.internal.VCloudVersionsAsyncClient;
import org.jclouds.vcloud.xml.SupportedVersionsHandler;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code VCloudVersions}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "vcloud.VCloudVersionsTest")
public class VCloudVersionsTest extends RestClientTest<VCloudVersionsAsyncClient> {

   public void testLogin() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudVersionsAsyncClient.class.getMethod("getSupportedVersions");
      GeneratedHttpRequest<VCloudVersionsAsyncClient> httpMethod = processor.createRequest(method);

      assertEquals(httpMethod.getRequestLine(), "GET http://localhost:8080/versions HTTP/1.1");
      assertHeadersEqual(httpMethod, HttpHeaders.ACCEPT
               + ": application/vnd.vmware.vcloud.vcloud+xml\n");
      assertPayloadEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, SupportedVersionsHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   @Override
   protected void checkFilters(GeneratedHttpRequest<VCloudVersionsAsyncClient> httpMethod) {
      assertEquals(httpMethod.getFilters().size(), 0);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<VCloudVersionsAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<VCloudVersionsAsyncClient>>() {
      };
   }

   @Override
   protected Module createModule() {
      return new AbstractModule() {
         @Override
         protected void configure() {
            Jsr330.bindProperties(binder(), checkNotNull(
                     new VCloudPropertiesBuilder(new Properties()).build(), "properties"));
            bind(URI.class).annotatedWith(VCloud.class).toInstance(
                     URI.create("http://localhost:8080"));
            bind(Logger.LoggerFactory.class).toInstance(new LoggerFactory() {
               public Logger getLogger(String category) {
                  return Logger.NULL;
               }
            });
         }

      };
   }

}
