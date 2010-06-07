#set( $lcaseProviderName = ${providerName.toLowerCase()} )
#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package ${package};

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Method;

import ${package}.config.${providerName}RestClientModule;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.http.functions.CloseContentAndReturn;
import org.jclouds.http.functions.ReturnStringIf200;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

/**
 * Tests annotation parsing of {@code ${providerName}AsyncClient}
 * 
 * @author ${author}
 */
@Test(groups = "unit", testName = "${lcaseProviderName}.${providerName}AsyncClientTest")
public class ${providerName}AsyncClientTest extends RestClientTest<${providerName}AsyncClient> {


   public void testList() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ${providerName}AsyncClient.class.getMethod("list");
      GeneratedHttpRequest<${providerName}AsyncClient> httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest, "GET ${providerEndpoint}/item HTTP/1.1");
      assertHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null);

      // now make sure request filters apply by replaying
      Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);
      Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(httpRequest, "GET ${providerEndpoint}/item HTTP/1.1");
      // for example, using basic authentication, we should get "only one" header
      assertHeadersEqual(httpRequest, "Accept: application/json\nAuthorization: Basic Zm9vOmJhcg==\n");
      assertPayloadEquals(httpRequest, null);

      // TODO: insert expected response class, which probably extends ParseJson
      assertResponseParserClassEquals(method, httpRequest, ReturnStringIf200.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testGet() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ${providerName}AsyncClient.class.getMethod("get", long.class);
      GeneratedHttpRequest<${providerName}AsyncClient> httpRequest = processor.createRequest(method, 1);

      assertRequestLineEquals(httpRequest, "GET ${providerEndpoint}/item/1 HTTP/1.1");
      assertHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null);

      // TODO: insert expected response class, which probably extends ParseJson
      assertResponseParserClassEquals(method, httpRequest, ReturnStringIf200.class);
      assertSaxResponseParserClassEquals(method, null);
      // note that get methods should convert 404's to null
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testDelete() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ${providerName}AsyncClient.class.getMethod("delete", long.class);
      GeneratedHttpRequest<${providerName}AsyncClient> httpRequest = processor.createRequest(
               method, 1);

      assertRequestLineEquals(httpRequest,
               "DELETE ${providerEndpoint}/item/1 HTTP/1.1");
      assertHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null);

      assertResponseParserClassEquals(method, httpRequest, CloseContentAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnVoidOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }
   @Override
   protected void checkFilters(GeneratedHttpRequest<${providerName}AsyncClient> httpRequest) {
      assertEquals(httpRequest.getFilters().size(), 1);
      assertEquals(httpRequest.getFilters().get(0).getClass(), BasicAuthentication.class);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<${providerName}AsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<${providerName}AsyncClient>>() {
      };
   }

   @Override
   protected Module createModule() {
      return new ${providerName}RestClientModule() {
         @Override
         protected void configure() {
            Names.bindProperties(binder(), new ${providerName}PropertiesBuilder("user", "key").build());
            install(new NullLoggingModule());
            super.configure();
         }
      };
   }
}
