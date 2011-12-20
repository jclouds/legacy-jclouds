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
package org.jclouds.glesys.features;

import com.google.inject.TypeLiteral;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseFirstJsonValueNamed;
import org.jclouds.rest.functions.MapHttp4xxCodesToExceptions;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Tests annotation parsing of {@code ServerAsyncClient}
 *
 * @author Adrian Cole
 * @author Adam Lowe
 */
@Test(groups = "unit", testName = "ServerAsyncClientTest")
public class ServerAsyncClientTest extends BaseGleSYSAsyncClientTest<ServerAsyncClient> {

   public void testListServers() throws Exception {
      testVoidArgsMethod("listServers", "list", "POST", ReturnEmptySetOnNotFoundOr404.class);
   }
   
   public void testGetAllowedArguments() throws Exception {
      testVoidArgsMethod("getServerAllowedArguments", "allowedarguments", "GET", MapHttp4xxCodesToExceptions.class);
   }

   public void testGetTemplates() throws Exception {
      testVoidArgsMethod("getTemplates", "templates", "GET", MapHttp4xxCodesToExceptions.class);
   }

   public void testGetServer() throws Exception {
      testServerMethod("getServerDetails", "details");
   }

   public void testGetgetServerStatus() throws Exception {
      testServerMethod("getServerStatus", "status");
   }

   public void testGetServerLimits() throws Exception {
      testServerMethod("getServerLimits", "limits");
   }

   public void testGetServerConsole() throws Exception {
      testServerMethod("getServerConsole", "console");
   }

   public void testStartServer() throws Exception {
      testServerMethodVoidReturn("startServer", "start");
   }
   
   public void testStopServer() throws Exception {
      testServerMethodVoidReturn("stopServer", "stop");
   }

   public void testRebootServer() throws Exception {
      testServerMethodVoidReturn("rebootServer", "reboot");
   }

   protected void testVoidArgsMethod(String localMethod, String remoteCall, String httpMethod, Class exceptionParser) throws Exception {
      Method method = ServerAsyncClient.class.getMethod(localMethod);
      HttpRequest httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest, httpMethod + " https://api.glesys.com/server/" + remoteCall + "/format/json HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, exceptionParser);

      checkFilters(httpRequest);
   }

   protected void testServerMethod(String localMethod, String remoteMethod) throws Exception {
       testServerMethod(localMethod, remoteMethod, "serverid", true);
   }
   
   protected void testServerMethodVoidReturn(String localMethod, String remoteMethod) throws Exception {
      testServerMethod(localMethod, remoteMethod, "id", false);
   }
 
   protected void testServerMethod(String localMethod, String remoteMethod, String serverIdField, boolean acceptHeader) throws Exception {
      Method method = ServerAsyncClient.class.getMethod(localMethod, String.class);
      HttpRequest httpRequest = processor.createRequest(method, "abcd");

      assertRequestLineEquals(httpRequest,
            "POST https://api.glesys.com/server/" + remoteMethod + "/format/json HTTP/1.1");

      if (acceptHeader) {
         assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
         assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
         assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);
      }

      assertPayloadEquals(httpRequest, serverIdField + "=abcd", "application/x-www-form-urlencoded", false);

      assertSaxResponseParserClassEquals(method, null);

      checkFilters(httpRequest);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<ServerAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<ServerAsyncClient>>() {
      };
   }
}
