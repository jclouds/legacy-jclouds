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
package org.jclouds.glesys;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reportMatcher;
import static org.easymock.EasyMock.verify;

import java.net.URI;

import org.easymock.IArgumentMatcher;
import org.jclouds.glesys.handlers.GleSYSErrorHandler;
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
public class GleSYSErrorHandlerTest {

   @Test
   public void test401MakesAuthorizationException() {
      assertCodeMakes("GET", URI.create("https://api.glesys.com/foo"), 401, "", "Unauthorized",
            AuthorizationException.class);
   }
   @Test
   public void test500LockedMakesIllegalStateException() {
      assertCodeMakes(
            "POST",
            URI.create("https://api.glesys.com/server/destroy/format/json"),
            500,
            "",
            "{\"response\":{\"status\":{\"code\":606,\"timestamp\":\"2012-02-14T15:48:39+01:00\",\"text\":\"Server Locked\"},\"debug\":{\"input\":{\"serverid\":\"xm3270596\",\"keepip\":\"0\"}}}}",
            IllegalStateException.class);
   }

   @Test
   public void test400MakesResourceNotFoundExceptionOnCouldNotFind() {
      assertCodeMakes(
            "POST",
            URI.create("https://api.glesys.com/domain/delete/format/json"),
            400,
            "",
            "{\"response\":{\"status\":{\"code\":400,\"timestamp\":\"2012-02-10T12:07:56+01:00\",\"text\":\"Could not find server with this id on this account.\n\"},\"debug\":{\"input\":{\"domainname\":\"email-test.jclouds.org\"}}}}",
            ResourceNotFoundException.class);
   }
   
   @Test
   public void test404MakesResourceNotFoundException() {
      assertCodeMakes("GET", URI.create("https://api.glesys.com/foo"), 404, "", "Not Found",
            ResourceNotFoundException.class);
   }

   private void assertCodeMakes(String method, URI uri, int statusCode, String message, String content,
         Class<? extends Exception> expected) {
      assertCodeMakes(method, uri, statusCode, message, "text/xml", content, expected);
   }

   private void assertCodeMakes(String method, URI uri, int statusCode, String message, String contentType,
         String content, Class<? extends Exception> expected) {

      GleSYSErrorHandler function = Guice.createInjector().getInstance(GleSYSErrorHandler.class);

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
