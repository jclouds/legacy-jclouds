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

import com.google.common.collect.Maps;
import com.google.inject.TypeLiteral;
import org.jclouds.glesys.options.*;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseFirstJsonValueNamed;
import org.jclouds.rest.functions.MapHttp4xxCodesToExceptions;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Tests annotation parsing of {@code ServerAsyncClient}
 *
 * @author Adrian Cole
 * @author Adam Lowe
 */
@Test(groups = "unit", testName = "ServerAsyncClientTest")
public class ServerAsyncClientTest extends BaseGleSYSAsyncClientTest<ServerAsyncClient> {

   public ServerAsyncClientTest() {
      asyncClientClass = ServerAsyncClient.class;
      remoteServicePrefix = "server";
   }
   
   private Map.Entry<String, String> serverIdOnly = newEntry("serverid", "abcd");
   
   public void testListServers() throws Exception {
      testMethod("listServers", "list", "POST", true, ReturnEmptySetOnNotFoundOr404.class);
   }
   
   public void testGetAllowedArguments() throws Exception {
      testMethod("getServerAllowedArguments", "allowedarguments", "GET", true, MapHttp4xxCodesToExceptions.class);
   }

   public void testGetTemplates() throws Exception {
      testMethod("getTemplates", "templates", "GET", true, MapHttp4xxCodesToExceptions.class);
   }

   public void testGetServer() throws Exception {
      testMethod("getServerDetails", "details", "POST", true, ReturnNullOnNotFoundOr404.class, serverIdOnly);
   }
   
   @Test
   public void testCreateServer() throws Exception {
      testMethod("createServer", "create", "POST", true, MapHttp4xxCodesToExceptions.class,
            newEntry("datacenter", "Falkenberg"), newEntry("platform", "OpenVZ"),
            newEntry("hostname", "jclouds-test"), newEntry("template", "Ubuntu%2032-bit"),
            newEntry("disksize", 5), newEntry("memorysize", 512), newEntry("cpucores", 1),
            newEntry("rootpw", "password"), newEntry("transfer", 50));
      testMethod("createServer", "create", "POST", true, MapHttp4xxCodesToExceptions.class,
            newEntry("datacenter", "Falkenberg"), newEntry("platform", "OpenVZ"),
            newEntry("hostname", "jclouds-test"), newEntry("template", "Ubuntu%2032-bit"),
            newEntry("disksize", 5), newEntry("memorysize", 512), newEntry("cpucores", 1),
            newEntry("rootpw", "password"), newEntry("transfer", 50), 
            ServerCreateOptions.Builder.description("Description-of-server").ip("10.0.0.1"));
   }

   @Test
   public void testEditServer() throws Exception {
      testMethod("editServer", "edit", "POST", false, MapHttp4xxCodesToExceptions.class, serverIdOnly);
      testMethod("editServer", "edit", "POST", false, MapHttp4xxCodesToExceptions.class, serverIdOnly,
            ServerEditOptions.Builder.description("Description-of-server").disksize(1).memorysize(512).cpucores(1).hostname("jclouds-test"));
   }

   @Test
   public void testCloneServer() throws Exception {
      testMethod("cloneServer", "clone", "POST", false, MapHttp4xxCodesToExceptions.class, serverIdOnly, newEntry("hostname", "somename"));
      testMethod("cloneServer", "clone", "POST", false, MapHttp4xxCodesToExceptions.class, serverIdOnly, newEntry("hostname", "somename"),
            ServerCloneOptions.Builder.description("Description-of-server").disksize(1).memorysize(512).cpucores(1).hostname("jclouds-test"));
   }

   public void testGetServerStatus() throws Exception {
      testMethod("getServerStatus", "status", "POST", true, ReturnNullOnNotFoundOr404.class, serverIdOnly);
      testMethod("getServerStatus", "status", "POST", true, ReturnNullOnNotFoundOr404.class, serverIdOnly, ServerStatusOptions.Builder.state());
   }

   public void testGetServerLimits() throws Exception {
      testMethod("getServerLimits", "limits", "POST", true, ReturnNullOnNotFoundOr404.class, serverIdOnly);
   }

   public void testGetServerConsole() throws Exception {
      testMethod("getServerConsole", "console", "POST", true, ReturnNullOnNotFoundOr404.class, serverIdOnly);
   }

   public void testStartServer() throws Exception {
      testMethod("startServer", "start", "POST", false, MapHttp4xxCodesToExceptions.class, serverIdOnly);
   }
   
   public void testStopServer() throws Exception {
      testMethod("stopServer", "stop", "POST", false, MapHttp4xxCodesToExceptions.class, serverIdOnly);
      testMethod("stopServer", "stop", "POST", false, MapHttp4xxCodesToExceptions.class, serverIdOnly, ServerStopOptions.Builder.hard());
   }

   public void testRebootServer() throws Exception {
      testMethod("rebootServer", "reboot", "POST", false, MapHttp4xxCodesToExceptions.class, serverIdOnly);
   }

   public void testDestroyServer() throws Exception {
      testMethod("destroyServer", "destroy", "POST", false, MapHttp4xxCodesToExceptions.class, serverIdOnly, ServerDestroyOptions.Builder.keepIp());
      testMethod("destroyServer", "destroy", "POST", false, MapHttp4xxCodesToExceptions.class, serverIdOnly, ServerDestroyOptions.Builder.discardIp());
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<ServerAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<ServerAsyncClient>>() {
      };
   }
}
