/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.List;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.UnwrapOnlyJsonValue;
import org.jclouds.io.Payloads;
import org.jclouds.json.config.GsonModule;
import org.jclouds.openstack.nova.domain.Address;
import org.jclouds.openstack.nova.domain.Addresses;
import org.jclouds.openstack.nova.domain.Server;
import org.jclouds.openstack.nova.domain.ServerStatus;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code ParseServerListFromJsonResponse}
 *
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ParseServerListFromJsonResponseTest {

   Injector i = Guice.createInjector(new GsonModule() {
      @Override
      protected void configure() {
         super.configure();
         bind(DateAdapter.class).to(Iso8601DateAdapter.class);
      }
   });

   @Test
   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream("/test_list_servers.json");

      List<Server> expects = ImmutableList.of(new Server(1234, "sample-server"), new Server(5678, "sample-server2"));

      UnwrapOnlyJsonValue<List<Server>> parser = i.getInstance(Key
            .get(new TypeLiteral<UnwrapOnlyJsonValue<List<Server>>>() {
            }));
      List<Server> response = parser.apply(new HttpResponse(200, "ok", Payloads.newInputStreamPayload(is)));

      assertEquals(response, expects);
   }

   @Test
   public void testApplyInputStreamDetails() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/test_list_servers_detail.json");

      UnwrapOnlyJsonValue<List<Server>> parser = i.getInstance(Key
            .get(new TypeLiteral<UnwrapOnlyJsonValue<List<Server>>>() {
            }));
      List<Server> response = parser.apply(new HttpResponse(200, "ok", Payloads.newInputStreamPayload(is)));

      assertEquals(response.get(0).getId(), 1234);
      assertEquals(response.get(0).getName(), "sample-server");
      assertEquals(response.get(0).getImageRef(), "https://servers.api.rackspacecloud.com/v1.1/32278/images/1234");
      assertEquals(response.get(0).getFlavorRef(), "https://servers.api.rackspacecloud.com/v1.1/32278/flavors/1");
      assertEquals(response.get(0).getHostId(), "e4d909c290d0fb1ca068ffaddf22cbd0");
      assertEquals(response.get(0).getStatus(), ServerStatus.BUILD);
      assertEquals(response.get(0).getProgress(), new Integer(60));

      List<Address> publicAddresses = ImmutableList.copyOf(Iterables.transform(
            ImmutableList.of("67.23.10.132", "::babe:67.23.10.132", "67.23.10.131", "::babe:4317:0A83"),
            Address.newString2AddressFunction()));
      List<Address> privateAddresses = ImmutableList.copyOf(Iterables.transform(
            ImmutableList.of("10.176.42.16", "::babe:10.176.42.16"),
            Address.newString2AddressFunction()));
      Addresses addresses1 = new Addresses(new HashSet<Address>(publicAddresses), new HashSet<Address>(privateAddresses));

      assertEquals(response.get(0).getAddresses(), addresses1);
      assertEquals(response.get(0).getMetadata(), ImmutableMap.of("Server Label", "Web Head 1", "Image Version", "2.1"));
      assertEquals(response.get(1).getId(), 5678);
      assertEquals(response.get(1).getName(), "sample-server2");
      assertEquals(response.get(1).getImageRef(), "https://servers.api.rackspacecloud.com/v1.1/32278/images/1");
      assertEquals(response.get(1).getFlavorRef(), "1");
      assertEquals(response.get(1).getAffinityId(), "b414fa41cb37b97dcb58d6c76112af1258e9eae2");
      assertEquals(response.get(1).getHostId(), "9e107d9d372bb6826bd81d3542a419d6");
      assertEquals(response.get(1).getStatus(), ServerStatus.ACTIVE);
      assertEquals(response.get(1).getProgress(), null);

      List<Address> publicAddresses2 = ImmutableList.copyOf(Iterables.transform(
            ImmutableList.of("67.23.10.133", "::babe:67.23.10.133"),
            Address.newString2AddressFunction()));
      List<Address> privateAddresses2 = ImmutableList.copyOf(Iterables.transform(
            ImmutableList.of("10.176.42.17", "::babe:10.176.42.17"),
            Address.newString2AddressFunction()));
      Addresses addresses2 = new Addresses(new HashSet<Address>(publicAddresses2), new HashSet<Address>(privateAddresses2));

      assertEquals(response.get(1).getAddresses(), addresses2);
      assertEquals(response.get(1).getMetadata(), ImmutableMap.of("Server Label", "DB 1"));
      assertEquals(response.get(1).getURI().toString(), "http://servers.api.openstack.org/1234/servers/56789");

   }

}
