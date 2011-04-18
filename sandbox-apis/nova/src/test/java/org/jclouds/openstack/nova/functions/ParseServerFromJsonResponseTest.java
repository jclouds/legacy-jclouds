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
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.UnwrapOnlyJsonValue;
import org.jclouds.io.Payloads;
import org.jclouds.openstack.nova.domain.Address;
import org.jclouds.openstack.nova.domain.Addresses;
import org.jclouds.openstack.nova.domain.Server;
import org.jclouds.openstack.nova.domain.ServerStatus;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.SimpleTimeZone;

import static org.testng.Assert.assertEquals;

/**
 * Tests behavior of {@code ParseServerFromJsonResponse}
 *
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ParseServerFromJsonResponseTest {

   @Test
   public void testApplyInputStreamDetails() throws UnknownHostException, NoSuchMethodException, ClassNotFoundException, ParseException {
      Server response = parseServer();

      assertEquals(response.getId(), 1234);
      assertEquals(response.getName(), "sample-server");
      assertEquals(response.getImageRef(), "https://servers.api.rackspacecloud.com/v1.1/32278/images/1234");
      assertEquals(response.getFlavorRef(), "https://servers.api.rackspacecloud.com/v1.1/32278/flavors/1");
      assertEquals(response.getHostId(), "e4d909c290d0fb1ca068ffaddf22cbd0");
      assertEquals(response.getStatus(), ServerStatus.BUILD);
      assertEquals(response.getProgress(), new Integer(60));
      SimpleDateFormat dateFormat = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
      dateFormat.setTimeZone(new SimpleTimeZone(0, "GMT"));
      assertEquals(response.getCreated(),
            dateFormat.parse("2010-08-10T12:00:00Z"));
      assertEquals(response.getUpdated(),
            dateFormat.parse("2010-10-10T12:00:00Z"));


      List<Address> publicAddresses = ImmutableList.copyOf(Iterables.transform(ImmutableList.of("67.23.10.132", "::babe:67.23.10.132", "67.23.10.131", "::babe:4317:0A83"), Address.newString2AddressFunction()));
      List<Address> privateAddresses = ImmutableList.copyOf(Iterables.transform(ImmutableList.of("10.176.42.16", "::babe:10.176.42.16"), Address.newString2AddressFunction()));
      Addresses addresses1 = new Addresses(new HashSet<Address>(publicAddresses), new HashSet<Address>(privateAddresses));
      assertEquals(response.getAddresses(), addresses1);
      assertEquals(response.getMetadata(), ImmutableMap.of("Server Label", "Web Head 1", "Image Version", "2.1"));
      assertEquals(response.getAddresses(), addresses1);
   }


   public static Server parseServer() throws NoSuchMethodException, ClassNotFoundException {

      Injector i = Guice.createInjector(new ParserModule());

      InputStream is = ParseServerFromJsonResponseTest.class.getResourceAsStream("/test_get_server_detail.json");

      UnwrapOnlyJsonValue<Server> parser = i.getInstance(Key.get(new TypeLiteral<UnwrapOnlyJsonValue<Server>>() {
      }));

      //Function<HttpResponse, ?> parser = i.getInstance(getParserOrThrowException(NovaClient.class.getMethod("getServer", int.class)));
      return (Server) parser.apply(new HttpResponse(200, "ok", Payloads.newInputStreamPayload(is)));
   }

}
