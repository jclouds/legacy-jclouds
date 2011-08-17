#set( $lcaseProviderName = ${providerName.toLowerCase()} )
#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package ${package};

import static org.jclouds.rest.RestContextFactory.contextSpec;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Method;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.http.functions.ReturnStringIf2xx;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.RestContextFactory.ContextSpec;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import ${package}.${providerName}AsyncClient;
import ${package}.${providerName}Client;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.inject.TypeLiteral;

/**
 * Tests annotation parsing of {@code ${providerName}AsyncClient}
 * 
 * @author ${author}
 */
@Test(groups = "unit", testName = "${lcaseProviderName}.${providerName}AsyncClientTest")
public class ${providerName}AsyncClientTest extends RestClientTest<${providerName}AsyncClient> {


   public void testList() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ${providerName}AsyncClient.class.getMethod("list");
      GeneratedHttpRequest<${providerName}AsyncClient> request = processor.createRequest(method);

      assertRequestLineEquals(request, "GET ${providerEndpoint}/items HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: text/plain\n");
      assertPayloadEquals(request, null, null, false);

      // now make sure request filters apply by replaying
      Iterables.getOnlyElement(request.getFilters()).filter(request);
      Iterables.getOnlyElement(request.getFilters()).filter(request);

      assertRequestLineEquals(request, "GET ${providerEndpoint}/items HTTP/1.1");
      // for example, using basic authentication, we should get "only one" header
      assertNonPayloadHeadersEqual(request, "Accept: text/plain\nAuthorization: Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReturnStringIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(request);

   }

   public void testGet() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ${providerName}AsyncClient.class.getMethod("get", long.class);
      GeneratedHttpRequest<${providerName}AsyncClient> request = processor.createRequest(method, 1);

      assertRequestLineEquals(request, "GET ${providerEndpoint}/items/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: text/plain\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReturnStringIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);

   }

   public void testDelete() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ${providerName}AsyncClient.class.getMethod("delete", long.class);
      GeneratedHttpRequest<${providerName}AsyncClient> request = processor.createRequest(
               method, 1);

      assertRequestLineEquals(request, "DELETE ${providerEndpoint}/items/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnVoidOnNotFoundOr404.class);

      checkFilters(request);

   }
   
   @Override
   protected void checkFilters(HttpRequest request) {
      assertEquals(request.getFilters().size(), 1);
      assertEquals(request.getFilters().get(0).getClass(), BasicAuthentication.class);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<${providerName}AsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<${providerName}AsyncClient>>() {
      };
   }

   @Override
   public ContextSpec<${providerName}Client, ${providerName}AsyncClient> createContextSpec() {
      return contextSpec("${lcaseProviderName}", "${providerEndpoint}", "${providerApiVersion}", "identity", "credential", ${providerName}Client.class,
               ${providerName}AsyncClient.class);
   }
}
