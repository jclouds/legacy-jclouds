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
package org.jclouds.vcloud;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.logging.Logger;
import org.jclouds.logging.Logger.LoggerFactory;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.vcloud.endpoints.VCloud;
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
public class VCloudVersionsTest extends RestClientTest<VCloudVersions> {

   public void testLogin() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudVersions.class.getMethod("getSupportedVersions");
      GeneratedHttpRequest<VCloudVersions> httpMethod = processor.createRequest(method);

      assertEquals(httpMethod.getRequestLine(), "GET http://localhost:8080/versions HTTP/1.1");
      assertHeadersEqual(httpMethod, HttpHeaders.ACCEPT + ": " + MediaType.APPLICATION_XML + "\n");
      assertEntityEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, SupportedVersionsHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   @Override
   protected void checkFilters(GeneratedHttpRequest<VCloudVersions> httpMethod) {
      assertEquals(httpMethod.getFilters().size(), 0);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<VCloudVersions>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<VCloudVersions>>() {
      };
   }

   @Override
   protected Module createModule() {
      return new AbstractModule() {
         @Override
         protected void configure() {
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
