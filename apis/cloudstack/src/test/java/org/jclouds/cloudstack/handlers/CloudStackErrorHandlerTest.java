/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.cloudstack.handlers;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reportMatcher;
import static org.easymock.EasyMock.verify;

import java.net.URI;

import org.easymock.IArgumentMatcher;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.InsufficientResourcesException;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import com.google.inject.Guice;

/**
 * @author Adrian Cole
 */
@Test(groups = {"unit"})
public class CloudStackErrorHandlerTest {

   @Test
   public void test400MakesIllegalArgumentException() {
      assertCodeMakes("GET", URI.create("https://cloudstack.com/foo"), 400, "", "Bad Request",
         IllegalArgumentException.class);
   }

   @Test
   public void test401MakesAuthorizationException() {
      assertCodeMakes("GET", URI.create("https://cloudstack.com/foo"), 401, "", "Unauthorized",
         AuthorizationException.class);
   }

   @Test
   public void test404MakesResourceNotFoundException() {
      assertCodeMakes("GET", URI.create("https://cloudstack.com/foo"), 404, "", "Not Found",
         ResourceNotFoundException.class);
   }

   @Test
   public void test405MakesIllegalArgumentException() {
      assertCodeMakes("GET", URI.create("https://cloudstack.com/foo"), 405, "", "Method Not Allowed",
         IllegalArgumentException.class);
   }

   @Test
   public void test431MakesIllegalStateException() {
      assertCodeMakes("GET", URI.create("https://cloudstack.com/foo"), 431, "", "Method Not Allowed",
         IllegalStateException.class);
   }

   @Test
   public void test431MakesResourceNotFoundExceptionOnDelete() {
      assertCodeMakes(
         "GET",
         URI.create("https://api.ninefold.com/compute/v1.0/?response=json&command=deleteSSHKeyPair"),
         431,
         "",
         "{ \"deletekeypairresponse\" : {\"errorcode\" : 431, \"errortext\" : \"A key pair with name 'adriancole-adapter-test-keypair' does not exist for account jclouds in domain id=457\"}  }",
         ResourceNotFoundException.class);
   }

   @Test
   public void test409MakesIllegalStateException() {
      assertCodeMakes("GET", URI.create("https://cloudstack.com/foo"), 409, "", "Conflict", IllegalStateException.class);
   }

   @Test
   public void test531MakesAuthorizationException() {
      assertCodeMakes("GET", URI.create("https://cloudstack.com/foo"), 531, "", "Unauthorized",
         AuthorizationException.class);
   }

   @Test
   void test534WithMaximumResourcesMakesInsufficientResourcesException() {
      assertCodeMakes(
         "GET",
         URI.create("http://10.26.26.155:8080/client/api?response=json&command=deployVirtualMachine&zoneid=7dbc4787-ec2f-498d-95f0-848c8c81e5da&templateid=240937c8-d695-419c-9908-5c7b2a07e6f1&serviceofferingid=c376102e-b683-4d43-b583-4eeab4627e65&displayname=bousa-4&name=bousa-4"),
         534,
         "",
         "{ \"createipforwardingruleresponse\" : {\"errorcode\" : 534, \"errortext\" : \"Maximum number of resources of type 'volume' for account name=jarcec in domain id=1 has been exceeded.\"}  }",
         InsufficientResourcesException.class);
   }

   @Test
   void test537MakesIllegalStateException() {
      assertCodeMakes(
         "GET",
         URI.create("http://10.26.26.155:8080/client/api?response=json&command=createIpForwardingRule&ipaddressid=37&startport=22&protocol=tcp"),
         537,
         "",
         "{ \"createipforwardingruleresponse\" : {\"errorcode\" : 537, \"errortext\" : \"There is already firewall rule specified for the ip address id=37\"}  }",
         IllegalStateException.class);
   }

   private void assertCodeMakes(String method, URI uri, int statusCode, String message, String content,
                                Class<? extends Exception> expected) {
      assertCodeMakes(method, uri, statusCode, message, "text/xml", content, expected);
   }

   private void assertCodeMakes(String method, URI uri, int statusCode, String message, String contentType,
                                String content, Class<? extends Exception> expected) {

      CloudStackErrorHandler function = Guice.createInjector().getInstance(CloudStackErrorHandler.class);

      HttpCommand command = createMock(HttpCommand.class);
      HttpRequest request = HttpRequest.builder().method(method).endpoint(uri).build();
      HttpResponse response = HttpResponse.builder().statusCode(statusCode).message(message).payload(content).build();
      response.getPayload().getContentMetadata().setContentType(contentType);

      expect(command.getCurrentRequest()).andReturn(request).atLeastOnce();
      command.setException(classEq(expected));

      replay(command);

      function.handleError(command, response);

      verify(command);
   }

   public static Exception classEq(final Class<? extends Exception> in) {
      reportMatcher(new IArgumentMatcher() {

         @Override
         public void appendTo(StringBuffer buffer) {
            buffer.append("classEq(");
            buffer.append(in);
            buffer.append(")");
         }

         @Override
         public boolean matches(Object arg) {
            return arg.getClass() == in;
         }

      });
      return null;
   }

}
