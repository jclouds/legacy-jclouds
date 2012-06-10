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
package org.jclouds.openstack.nova.v2_0;

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
import org.jclouds.io.Payloads;
import org.jclouds.openstack.nova.v2_0.handlers.NovaErrorHandler;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.InsufficientResourcesException;
import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import com.google.inject.Guice;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = { "unit" })
public class NovaErrorHandlerTest {

   @Test
   public void test401MakesAuthorizationException() {
      assertCodeMakes("GET", URI.create("https://api.openstack.nova.com/foo"), 401, "", "Unauthorized",
               AuthorizationException.class);
   }

   @Test
   public void test400MakesIllegalStateExceptionOnQuotaExceededOnNoFixedIps() {
      // should wait until ips are associated w/the server
      assertCodeMakes(
               "POST",
               URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/37936628937291/servers/71554/action"),
               400,
               "HTTP/1.1 400 Bad Request",
               "{\"badRequest\": {\"message\": \"instance |71554| has no fixed_ips. unable to associate floating ip\", \"code\": 400}}",
               IllegalStateException.class);
   }
   
   @Test
   public void test400MakesIllegalStateExceptionOnAlreadyExists() {
      assertCodeMakes(
               "POST",
               URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/37936628937291/servers"),
               400,
               "HTTP/1.1 400 Bad Request",
               "{\"badRequest\": {\"message\": \"Server with the name 'test' already exists\", \"code\": 400}}",
               IllegalStateException.class);
   }
   
   @Test
   public void test400MakesInsufficientResourcesExceptionOnQuotaExceeded() {
      assertCodeMakes(
               "POST",
               URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/37936628937291/os-floating-ips"),
               400,
               "HTTP/1.1 400 Bad Request",
               "{\"badRequest\": {\"message\": \"AddressLimitExceeded: Address quota exceeded. You cannot allocate any more addresses\", \"code\": 400}}",
               InsufficientResourcesException.class);
   }
   
   @Test
   public void test413MakesInsufficientResourcesException() {
      assertCodeMakes(
               "POST",
               URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/37936628937291/os-volumes"),
               413,
               "HTTP/1.1 413 Request Entity Too Large",
               "{\"badRequest\": {\"message\": \"Volume quota exceeded. You cannot create a volume of size 1G\", \"code\": 413, \"retryAfter\": 0}}",
               InsufficientResourcesException.class);
   }

   @Test
   public void test404MakesResourceNotFoundException() {
      assertCodeMakes("GET", URI.create("https://api.openstack.nova.com/foo"), 404, "", "Not Found",
               ResourceNotFoundException.class);
   }

   private void assertCodeMakes(String method, URI uri, int statusCode, String message, String content,
            Class<? extends Exception> expected) {
      assertCodeMakes(method, uri, statusCode, message, "text/json", content, expected);
   }

   private void assertCodeMakes(String method, URI uri, int statusCode, String message, String contentType,
            String content, Class<? extends Exception> expected) {

      NovaErrorHandler function = Guice.createInjector().getInstance(NovaErrorHandler.class);

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
