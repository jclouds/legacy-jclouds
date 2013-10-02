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
package org.jclouds.http.handlers;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reportMatcher;
import static org.easymock.EasyMock.verify;

import java.net.URI;

import org.easymock.IArgumentMatcher;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import com.google.inject.Guice;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = { "unit" })
public abstract class BaseHttpErrorHandlerTest<T extends HttpErrorHandler> {
   abstract protected Class<T> getClassToTest();

   protected void assertCodeMakes(String method, URI uri, int statusCode, String message, String content,
            Class<? extends Exception> expected, String exceptionMessage) {
      assertCodeMakes(method, uri, statusCode, message, "text/xml", content, expected, exceptionMessage);
   }

   private void assertCodeMakes(String method, URI uri, int statusCode, String message, String contentType,
            String content, Class<? extends Exception> expected, String exceptionMessage) {

      T function = Guice.createInjector().getInstance(getClassToTest());

      HttpCommand command = createMock(HttpCommand.class);
      HttpRequest request = HttpRequest.builder().method(method).endpoint(uri).build();
      HttpResponse response = HttpResponse.builder()
                                          .statusCode(statusCode)
                                          .message(message)
                                          .payload(content).build();
      response.getPayload().getContentMetadata().setContentType(contentType);

      expect(command.getCurrentRequest()).andReturn(request).atLeastOnce();
      command.setException(exceptionEq(expected, exceptionMessage));

      replay(command);

      function.handleError(command, response);

      verify(command);
   }

   public static Exception exceptionEq(final Class<? extends Exception> in, final String exceptionMessage) {
      reportMatcher(new IArgumentMatcher() {

         @Override
         public void appendTo(StringBuffer buffer) {
            buffer.append("exceptionEq(");
            buffer.append(in);
            buffer.append(",");
            buffer.append(exceptionMessage);
            buffer.append(")");
         }

         @Override
         public boolean matches(Object arg) {
            return arg.getClass() == in && exceptionMessage.equals(Exception.class.cast(arg).getMessage());
         }

      });
      return null;
   }

}
