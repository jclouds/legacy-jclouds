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
package org.jclouds.openstack.swift.v1;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reportMatcher;
import static org.easymock.EasyMock.verify;

import java.net.URI;

import org.easymock.IArgumentMatcher;
import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.swift.v1.handlers.SwiftErrorHandler;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "SwiftErrorHandlerTest")
public class SwiftErrorHandlerTest {

   @Test
   public void test404SetsKeyNotFoundExceptionMosso() {
      assertCodeMakes("HEAD", URI
               .create("http://host/v1/MossoCloudFS_7064cdb1d49d4dcba3c899ac33e8409d/adriancole-blobstore1/key"), 404,
               "Not Found", "", KeyNotFoundException.class);
   }

   @Test
   public void test404SetsKeyNotFoundExceptionSwift() {
      assertCodeMakes("HEAD", URI
               .create("http://67.202.39.175:8080/v1/AUTH_7064cdb1d49d4dcba3c899ac33e8409d/adriancole-blobstore1/key"),
               404, "Not Found", "", KeyNotFoundException.class);
   }

   @Test
   public void test404SetsContainerNotFoundExceptionMosso() {
      assertCodeMakes("HEAD", URI
               .create("http://host/v1/MossoCloudFS_7064cdb1d49d4dcba3c899ac33e8409d/adriancole-blobstore1"), 404,
               "Not Found", "", ContainerNotFoundException.class);
   }

   @Test
   public void test404SetsContainerNotFoundExceptionSwift() {
      assertCodeMakes("HEAD", URI
               .create("http://67.202.39.175:8080/v1/AUTH_7064cdb1d49d4dcba3c899ac33e8409d/adriancole-blobstore1"),
               404, "Not Found", "", ContainerNotFoundException.class);
   }

   private void assertCodeMakes(String method, URI uri, int statusCode, String message, String content,
            Class<? extends Exception> expected) {
      assertCodeMakes(method, uri, statusCode, message, "text/plain", content, expected);
   }

   private void assertCodeMakes(String method, URI uri, int statusCode, String message, String contentType,
            String content, Class<? extends Exception> expected) {

      SwiftErrorHandler function = new SwiftErrorHandler();

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
