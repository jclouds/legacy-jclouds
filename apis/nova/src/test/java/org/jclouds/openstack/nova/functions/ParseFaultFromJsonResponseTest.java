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
package org.jclouds.openstack.nova.functions;

import java.io.InputStream;
import java.util.List;

import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.UnwrapOnlyJsonValue;
import org.jclouds.json.config.GsonModule;
import org.jclouds.openstack.nova.domain.Server;
import org.jclouds.openstack.nova.handlers.ParseNovaErrorFromHttpResponse;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code ParseServerListFromJsonResponse}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ParseFaultFromJsonResponseTest")
public class ParseFaultFromJsonResponseTest {

   Injector i = Guice.createInjector(new GsonModule());

   @Test
   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream("/test_list_servers.json");

      @SuppressWarnings("unused")
      UnwrapOnlyJsonValue<List<Server>> parser = i.getInstance(Key
               .get(new TypeLiteral<UnwrapOnlyJsonValue<List<Server>>>() {
               }));
      // List<Server> response = parser.apply(new HttpResponse(413, "Over limit",
      // Payloads.newInputStreamPayload(is)));
      new ParseNovaErrorFromHttpResponse().handleError(createHttpCommand(), HttpResponse.builder().statusCode(413)
               .message("Over limit").payload(is).build());

      // assertEquals(response, expects);
   }

   @Test
   public void testHandler() {
      // InputStream is = getClass().getResourceAsStream("/test_error_handler.json");

      //
      //
      // NovaErrorHandler handler = Guice.createInjector(new
      // GsonModule()).getInstance(GoGridErrorHandler.class);
      //
      // HttpCommand command = createHttpCommand();
      // handler.handleError(command,
      // HttpResponse.builder().statusCode(200).message("ok").payload(is).build());
      //
      // Exception createdException = command.getException();
      //
      // assertNotNull(createdException, "There should've been an exception generated");
      // String message = createdException.getMessage();
      // assertTrue(message.contains("No object found that matches your input criteria."),
      // "Didn't find the expected error cause in the exception message");
      // assertTrue(message.contains("IllegalArgumentException"),
      // "Didn't find the expected error code in the exception message");
      //
      // // make sure the InputStream is closed
      // try {
      // is.available();
      // throw new
      // TestException("Stream wasn't closed by the GoGridErrorHandler when it should've");
      // } catch (IOException e) {
      // // this is the excepted output
      // }
   }

   HttpCommand createHttpCommand() {
      return new HttpCommand() {
         private Exception exception;

         @Override
         public int incrementRedirectCount() {
            return 0; // To change body of implemented methods use File | Settings | File Templates.
         }

         @Override
         public int getRedirectCount() {
            return 0; // To change body of implemented methods use File | Settings | File Templates.
         }

         @Override
         public boolean isReplayable() {
            return false; // To change body of implemented methods use File | Settings | File
                          // Templates.
         }

         @Override
         public int incrementFailureCount() {
            return 0; // To change body of implemented methods use File | Settings | File Templates.
         }

         @Override
         public int getFailureCount() {
            return 0; // To change body of implemented methods use File | Settings | File Templates.
         }

         @Override
         public HttpRequest getCurrentRequest() {
            return HttpRequest.builder().method("method").endpoint("http://endpoint").build();
         }

         @Override
         public void setCurrentRequest(HttpRequest request) {
            // To change body of implemented methods use File | Settings | File Templates.
         }

         @Override
         public void setException(Exception exception) {
            this.exception = exception;
         }

         @Override
         public Exception getException() {
            return exception;
         }
      };
   }

}
