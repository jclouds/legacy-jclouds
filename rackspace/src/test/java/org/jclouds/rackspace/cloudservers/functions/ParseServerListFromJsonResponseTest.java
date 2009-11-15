/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.rackspace.cloudservers.functions;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.rackspace.cloudservers.domain.Addresses;
import org.jclouds.rackspace.cloudservers.domain.Server;
import org.jclouds.rackspace.cloudservers.domain.ServerStatus;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code ParseServerListFromJsonResponseTest}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "cloudservers.ParseServerListFromJsonResponseTest")
public class ParseServerListFromJsonResponseTest {

   Injector i = Guice.createInjector(new ParserModule());

   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream("/cloudservers/test_list_servers.json");

      List<Server> expects = ImmutableList.of(new Server(1234, "sample-server"), new Server(5678,
               "sample-server2"));

      ParseServerListFromJsonResponse parser = new ParseServerListFromJsonResponse(i
               .getInstance(Gson.class));
      assertEquals(parser.apply(is), expects);
   }

   public void testApplyInputStreamDetails() throws UnknownHostException {
      InputStream is = getClass()
               .getResourceAsStream("/cloudservers/test_list_servers_detail.json");

      ParseServerListFromJsonResponse parser = new ParseServerListFromJsonResponse(i
               .getInstance(Gson.class));
      List<Server> response = parser.apply(is);
      assertEquals(response.get(0).getId(), 1234);
      assertEquals(response.get(0).getName(), "sample-server");
      assertEquals(response.get(0).getImageId(), new Integer(2));
      assertEquals(response.get(0).getFlavorId(), new Integer(1));
      assertEquals(response.get(0).getHostId(), "e4d909c290d0fb1ca068ffaddf22cbd0");
      assertEquals(response.get(0).getStatus(), ServerStatus.BUILD);
      assertEquals(response.get(0).getProgress(), new Integer(60));
      List<InetAddress> publicAddresses = Lists.newArrayList(InetAddress.getByName("67.23.10.132"),
               InetAddress.getByName("67.23.10.131"));
      List<InetAddress> privateAddresses = Lists
               .newArrayList(InetAddress.getByName("10.176.42.16"));
      Addresses addresses1 = new Addresses();
      addresses1.getPrivateAddresses().addAll(privateAddresses);
      addresses1.getPublicAddresses().addAll(publicAddresses);
      assertEquals(response.get(0).getAddresses(), addresses1);
      assertEquals(response.get(0).getMetadata(), ImmutableMap.of("Server Label", "Web Head 1",
               "Image Version", "2.1"));
      assertEquals(response.get(1).getId(), 5678);
      assertEquals(response.get(1).getName(), "sample-server2");
      assertEquals(response.get(1).getImageId(), new Integer(2));
      assertEquals(response.get(1).getFlavorId(), new Integer(1));
      assertEquals(response.get(1).getHostId(), "9e107d9d372bb6826bd81d3542a419d6");
      assertEquals(response.get(1).getStatus(), ServerStatus.ACTIVE);
      assertEquals(response.get(1).getProgress(), null);
      List<InetAddress> publicAddresses2 = Lists
               .newArrayList(InetAddress.getByName("67.23.10.133"));
      List<InetAddress> privateAddresses2 = Lists.newArrayList(InetAddress
               .getByName("10.176.42.17"));
      Addresses addresses2 = new Addresses();
      addresses2.getPrivateAddresses().addAll(privateAddresses2);
      addresses2.getPublicAddresses().addAll(publicAddresses2);
      assertEquals(response.get(1).getAddresses(), addresses2);
      assertEquals(response.get(1).getMetadata(), ImmutableMap.of("Server Label", "DB 1"));

   }

}
