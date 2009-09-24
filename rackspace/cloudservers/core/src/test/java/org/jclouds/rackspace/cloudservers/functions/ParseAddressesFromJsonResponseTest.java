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
package org.jclouds.rackspace.cloudservers.functions;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.rackspace.cloudservers.domain.Addresses;
import org.jclouds.util.DateService;
import org.testng.annotations.Test;

import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.internal.ImmutableList;

/**
 * Tests behavior of {@code ParseAddressesFromJsonResponse}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "cloudservers.ParseAddressesFromJsonResponseTest")
public class ParseAddressesFromJsonResponseTest {

   Injector i = Guice.createInjector(new ParserModule());
   DateService dateService = new DateService();

   public void testApplyInputStreamDetails() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/test_list_addresses.json");

      ParseAddressesFromJsonResponse parser = new ParseAddressesFromJsonResponse(i
               .getInstance(Gson.class));
      Addresses response = parser.apply(is);
      List<InetAddress> publicAddresses = ImmutableList.of(InetAddress.getByAddress(new byte[] {
               67, 23, 10, (byte) 132 }), InetAddress.getByAddress(new byte[] { 67, 23, 10,
               (byte) 131 }));

      List<InetAddress> privateAddresses = ImmutableList.of(InetAddress.getByAddress(new byte[] {
               10, (byte) 176, 42, 16 }));

      assertEquals(response.getPublicAddresses(), publicAddresses);
      assertEquals(response.getPrivateAddresses(), privateAddresses);
   }

}
