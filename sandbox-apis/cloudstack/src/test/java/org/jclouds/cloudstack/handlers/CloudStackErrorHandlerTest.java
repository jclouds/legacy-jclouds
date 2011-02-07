/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.cloudstack.handlers;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.reportMatcher;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import java.net.URI;

import org.easymock.IArgumentMatcher;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.io.Payloads;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import com.google.inject.Guice;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = { "unit" })
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
   public void test409MakesIllegalStateException() {
      assertCodeMakes("GET", URI.create("https://cloudstack.com/foo"), 409, "", "Conflict", IllegalStateException.class);
   }

   private void assertCodeMakes(String method, URI uri, int statusCode, String message, String content,
         Class<? extends Exception> expected) {
      assertCodeMakes(method, uri, statusCode, message, "text/xml", content, expected);
   }

   private void assertCodeMakes(String method, URI uri, int statusCode, String message, String contentType,
         String content, Class<? extends Exception> expected) {

      CloudStackErrorHandler function = Guice.createInjector().getInstance(CloudStackErrorHandler.class);

      HttpCommand command = createMock(HttpCommand.class);
      HttpRequest request = new HttpRequest(method, uri);
      HttpResponse response = new HttpResponse(statusCode, message, Payloads.newInputStreamPayload(Strings2
            .toInputStream(content)));
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
