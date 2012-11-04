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
package org.jclouds.openstack.nova.functions;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.List;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.UnwrapOnlyJsonValue;
import org.jclouds.json.config.GsonModule;
import org.jclouds.openstack.nova.domain.Address;
import org.jclouds.openstack.nova.domain.Addresses;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code ParseAddressesFromJsonResponse}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ParseAddressesFromJsonResponseTest {
   Injector i = Guice.createInjector(new GsonModule());

   @Test
   public void testApplyInputStreamDetails() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/test_list_addresses.json");

      UnwrapOnlyJsonValue<Addresses> parser = i.getInstance(Key.get(new TypeLiteral<UnwrapOnlyJsonValue<Addresses>>() {
      }));
      Addresses response = parser.apply(HttpResponse.builder().statusCode(200).message("ok").payload(is).build());

      List<Address> publicAddresses = ImmutableList.copyOf(Iterables.transform(
               ImmutableList.of("67.23.10.132", "::babe:67.23.10.132", "67.23.10.131", "::babe:4317:0A83"),
               Address.newString2AddressFunction()));

      List<Address> privateAddresses = ImmutableList.copyOf(Iterables.transform(
               ImmutableList.of("10.176.42.16", "::babe:10.176.42.16"), Address.newString2AddressFunction()));

      assertEquals(Sets.newHashSet(publicAddresses), response.getPublicAddresses());
      assertEquals(Sets.newHashSet(privateAddresses), response.getPrivateAddresses());
   }
}
