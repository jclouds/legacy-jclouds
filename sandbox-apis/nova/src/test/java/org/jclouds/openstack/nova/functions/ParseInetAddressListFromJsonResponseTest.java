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

package org.jclouds.openstack.nova.functions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.UnwrapOnlyJsonValue;
import org.jclouds.io.Payloads;
import org.jclouds.openstack.nova.domain.Address;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;

/**
 * Tests behavior of {@code ParseInetAddressListFromJsonResponse}
 *
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ParseInetAddressListFromJsonResponseTest {

   Injector i = Guice.createInjector(new ParserModule());

   @Test
   public void testPublic() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/test_list_addresses_public.json");

      UnwrapOnlyJsonValue<Map<String, List<Address>>> parser = i.getInstance(
            Key.get(new TypeLiteral<UnwrapOnlyJsonValue<Map<String, List<Address>>>>() {
            }));
      List<Address> response = parser.apply(new HttpResponse(200, "ok", Payloads.newInputStreamPayload(is)))
            .get("public");

      List<Address> addresses = ImmutableList.copyOf(Iterables.transform(
            ImmutableList.of("67.23.10.132", "::babe:67.23.10.132", "67.23.10.131", "::babe:4317:0A83"),
            Address.newString2AddressFunction()));
      assertEquals(response, addresses);
   }

   @Test
   public void testPrivate() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/test_list_addresses_private.json");

      UnwrapOnlyJsonValue<Map<String, List<Address>>> parser = i.getInstance(
            Key.get(new TypeLiteral<UnwrapOnlyJsonValue<Map<String, List<Address>>>>() {
            }));
      List<Address> response = parser.apply(new HttpResponse(200, "ok", Payloads.newInputStreamPayload(is)))
            .get("private");

      List<Address> addresses = ImmutableList.copyOf(Iterables.transform(
            ImmutableList.of("67.23.10.132", "::babe:67.23.10.132", "67.23.10.131", "::babe:4317:0A83"),
            Address.newString2AddressFunction()));

      assertEquals(response, addresses);
   }
}
