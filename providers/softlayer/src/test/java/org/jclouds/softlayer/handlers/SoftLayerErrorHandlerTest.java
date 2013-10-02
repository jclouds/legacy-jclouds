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
package org.jclouds.softlayer.handlers;

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
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import com.google.inject.Guice;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = { "unit" })
public class SoftLayerErrorHandlerTest {

   @Test
   public void test500MakesResourceNotFoundExceptionOnUnableToDeterminePackage() {
      assertCodeMakes(
            "GET",
            URI.create("https://api.softlayer.com/foo"),
            500,
            "",
            "{\"error\":\"Unable to determine package for 'node2102835255.me.org'.\"}",
            ResourceNotFoundException.class);
   }
   
   @Test
   public void test500MakesIllegalStateExceptionOnActiveTransaction() {
      assertCodeMakes(
            "GET",
            URI.create("https://api.softlayer.com/rest/v3/SoftLayer_Billing_Item/8676376/cancelService.json"),
            500,
            "",
            "{\"error\":\"There is currently an active transaction.\"}",
            IllegalStateException.class);
   }

   @Test
   public void test401MakesAuthorizationException() {
      assertCodeMakes("GET", URI.create("https://api.softlayer.com/foo"), 401, "", "Unauthorized",
            AuthorizationException.class);
   }

   @Test
   public void test404MakesResourceNotFoundException() {
      assertCodeMakes("GET", URI.create("https://api.softlayer.com/foo"), 404, "", "Not Found",
            ResourceNotFoundException.class);
   }

   private void assertCodeMakes(String method, URI uri, int statusCode, String message, String content,
         Class<? extends Exception> expected) {
      assertCodeMakes(method, uri, statusCode, message, "text/xml", content, expected);
   }

   private void assertCodeMakes(String method, URI uri, int statusCode, String message, String contentType,
         String content, Class<? extends Exception> expected) {

      SoftLayerErrorHandler function = Guice.createInjector().getInstance(SoftLayerErrorHandler.class);

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
