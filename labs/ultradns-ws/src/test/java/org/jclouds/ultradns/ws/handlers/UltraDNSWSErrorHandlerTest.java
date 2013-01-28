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
package org.jclouds.ultradns.ws.handlers;

import static com.google.common.base.Throwables.propagate;
import static org.jclouds.rest.internal.BaseRestApiExpectTest.payloadFromStringWithContentType;
import static org.jclouds.util.Strings2.toStringAndClose;
import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.io.Payload;
import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.ultradns.ws.UltraDNSWSResponseException;
import org.testng.annotations.Test;

import com.google.inject.Guice;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit" )
public class UltraDNSWSErrorHandlerTest {
   UltraDNSWSErrorHandler function = Guice.createInjector(new SaxParserModule()).getInstance(
         UltraDNSWSErrorHandler.class);

   @Test
   public void testCode0SetsResourceNotFoundException() throws IOException {
      HttpRequest request = HttpRequest.builder().method("POST")
                                       .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
                                       .addHeader("Host", "ultra-api.ultradns.com:8443")
                                       .payload(payloadFromResource("/list_tasks.xml")).build();
      HttpCommand command = new HttpCommand(request);
      HttpResponse response = HttpResponse.builder().message("Server Error").statusCode(500)
                                          .payload(payloadFromResource("/task_doesnt_exist.xml")).build();

      function.handleError(command, response);

      assertEquals(command.getException().getClass(), ResourceNotFoundException.class);
      assertEquals(command.getException().getMessage(), "Cannot find task with guid AAAAAAAAAAAAAAAA");

      UltraDNSWSResponseException exception = UltraDNSWSResponseException.class.cast(command.getException().getCause());

      assertEquals(exception.getMessage(), "Error 0: Cannot find task with guid AAAAAAAAAAAAAAAA");
      assertEquals(exception.getError().getDescription(), "Cannot find task with guid AAAAAAAAAAAAAAAA");
      assertEquals(exception.getError().getCode(), 0);
   }

   @Test
   public void testCode2401SetsResourceNotFoundException() throws IOException {
      HttpRequest request = HttpRequest.builder().method("POST")
                                       .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
                                       .addHeader("Host", "ultra-api.ultradns.com:8443")
                                       .payload(payloadFromResource("/list_zones_by_account.xml")).build();
      HttpCommand command = new HttpCommand(request);
      HttpResponse response = HttpResponse.builder().message("Server Error").statusCode(500)
                                          .payload(payloadFromResource("/account_doesnt_exist.xml")).build();

      function.handleError(command, response);

      assertEquals(command.getException().getClass(), ResourceNotFoundException.class);
      assertEquals(command.getException().getMessage(), "Account not found in the system. ID: AAAAAAAAAAAAAAAA");

      UltraDNSWSResponseException exception = UltraDNSWSResponseException.class.cast(command.getException().getCause());

      assertEquals(exception.getMessage(), "Error 2401: Account not found in the system. ID: AAAAAAAAAAAAAAAA");
      assertEquals(exception.getError().getDescription(), "Account not found in the system. ID: AAAAAAAAAAAAAAAA");
      assertEquals(exception.getError().getCode(), 2401);
   }

   @Test
   public void testCode1801SetsResourceNotFoundException() throws IOException {
      HttpRequest request = HttpRequest.builder().method("POST")
                                       .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
                                       .addHeader("Host", "ultra-api.ultradns.com:8443")
                                       .payload(payloadFromResource("/get_zone.xml")).build();
      HttpCommand command = new HttpCommand(request);
      HttpResponse response = HttpResponse.builder().message("Server Error").statusCode(500)
                                          .payload(payloadFromResource("/zone_doesnt_exist.xml")).build();

      function.handleError(command, response);

      assertEquals(command.getException().getClass(), ResourceNotFoundException.class);
      assertEquals(command.getException().getMessage(), "Zone does not exist in the system.");

      UltraDNSWSResponseException exception = UltraDNSWSResponseException.class.cast(command.getException().getCause());

      assertEquals(exception.getMessage(), "Error 1801: Zone does not exist in the system.");
      assertEquals(exception.getError().getDescription(), "Zone does not exist in the system.");
      assertEquals(exception.getError().getCode(), 1801);
   }

   private Payload payloadFromResource(String resource) {
      try {
         return payloadFromStringWithContentType(toStringAndClose(getClass().getResourceAsStream(resource)),
               "application/xml");
      } catch (IOException e) {
         throw propagate(e);
      }
   }
}
