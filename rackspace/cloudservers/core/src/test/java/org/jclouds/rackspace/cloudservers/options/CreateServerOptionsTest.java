/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.rackspace.cloudservers.options;

import static org.jclouds.rackspace.cloudservers.options.CreateServerOptions.Builder.withFile;
import static org.jclouds.rackspace.cloudservers.options.CreateServerOptions.Builder.withSharedIp;
import static org.jclouds.rackspace.cloudservers.options.CreateServerOptions.Builder.withSharedIpGroup;
import static org.testng.Assert.assertEquals;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

import javax.ws.rs.HttpMethod;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.config.ParserModule;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code ParseFlavorFromJsonResponse}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "cloudservers.CreateServerOptionsTest")
public class CreateServerOptionsTest {

   Injector injector = Guice.createInjector(new ParserModule());

   @Test
   public void testAddEntityToRequestMapOfStringStringHttpRequest() {
      CreateServerOptions options = new CreateServerOptions();
      HttpRequest request = buildRequest(options);
      assertEquals("{\"server\":{\"name\":\"foo\",\"imageId\":1,\"flavorId\":2}}", request
               .getEntity());
   }

   private HttpRequest buildRequest(CreateServerOptions options) {
      injector.injectMembers(options);
      HttpRequest request = new HttpRequest(HttpMethod.POST, URI.create("http://localhost"));
      options.addEntityToRequest(ImmutableMap.of("name", "foo", "imageId", "1", "flavorId", "2"),
               request);
      return request;
   }

   @Test
   public void testWithFile() {
      CreateServerOptions options = new CreateServerOptions();
      options.withFile("/tmp/rhubarb", "foo".getBytes());
      HttpRequest request = buildRequest(options);
      assertFile(request);
   }

   @Test
   public void testWithFileStatic() {
      CreateServerOptions options = withFile("/tmp/rhubarb", "foo".getBytes());
      HttpRequest request = buildRequest(options);
      assertFile(request);
   }

   private void assertFile(HttpRequest request) {
      assertEquals(
               "{\"server\":{\"name\":\"foo\",\"imageId\":1,\"flavorId\":2,\"personality\":[{\"path\":\"/tmp/rhubarb\",\"contents\":\"Zm9v\"}]}}",
               request.getEntity());
   }

   @Test
   public void testWithSharedIpGroup() {
      CreateServerOptions options = new CreateServerOptions();
      options.withSharedIpGroup(3);
      HttpRequest request = buildRequest(options);
      assertSharedIpGroup(request);
   }

   @Test
   public void testWithSharedIpGroupStatic() {
      CreateServerOptions options = withSharedIpGroup(3);
      HttpRequest request = buildRequest(options);
      assertSharedIpGroup(request);
   }

   private void assertSharedIpGroup(HttpRequest request) {
      assertEquals(
               "{\"server\":{\"name\":\"foo\",\"imageId\":1,\"flavorId\":2,\"sharedIpGroupId\":3}}",
               request.getEntity());
   }

   @Test
   public void testWithMetadata() {
   }

   @Test
   public void testWithSharedIp() throws UnknownHostException {
      CreateServerOptions options = new CreateServerOptions();
      options.withSharedIpGroup(3).withSharedIp(
               InetAddress.getByAddress(new byte[] { 127, 0, 0, 1 }));
      HttpRequest request = buildRequest(options);
      assertSharedIp(request);
   }

   @Test
   public void testWithSharedIpStatic() throws UnknownHostException {
      CreateServerOptions options = withSharedIpGroup(3).withSharedIp(
               InetAddress.getByAddress(new byte[] { 127, 0, 0, 1 }));
      HttpRequest request = buildRequest(options);
      assertSharedIp(request);
   }

   private void assertSharedIp(HttpRequest request) {
      assertEquals(
               "{\"server\":{\"name\":\"foo\",\"imageId\":1,\"flavorId\":2,\"sharedIpGroupId\":3,\"addresses\":{\"public\":[\"127.0.0.1\"]}}}",
               request.getEntity());
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testWithSharedIpNoGroup() throws UnknownHostException {
      CreateServerOptions options = new CreateServerOptions();
      options.withSharedIp(InetAddress.getByAddress(new byte[] { 127, 0, 0, 1 }));
      buildRequest(options);
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testWithSharedIpNoGroupStatic() throws UnknownHostException {
      CreateServerOptions options = withSharedIp(InetAddress
               .getByAddress(new byte[] { 127, 0, 0, 1 }));
      buildRequest(options);
   }
}
