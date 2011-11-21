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
import static org.testng.Assert.assertTrue;

import java.io.InputStream;
import java.net.UnknownHostException;
import java.text.ParseException;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.UnwrapOnlyJsonValue;
import org.jclouds.io.Payloads;
import org.jclouds.json.config.GsonModule;
import org.jclouds.openstack.nova.domain.Server;
import org.jclouds.openstack.nova.domain.ServerStatus;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

@Test(groups = "unit")
public class ParseServerFromJsonNoAddressesResponseTest {

   @Test
   public void testApplyInputStreamDetails() throws UnknownHostException, NoSuchMethodException, ClassNotFoundException, ParseException {
      Server response = parseServer();

      assertEquals(response.getId(), 847);
      assertEquals(response.getName(), "cmsNode-fa2");
      assertEquals(response.getImageRef(), "http://dragon004.hw.griddynamics.net:8774/v1.1/images/106");
      assertEquals(response.getFlavorRef(), "http://dragon004.hw.griddynamics.net:8774/v1.1/flavors/2");
      assertEquals(response.getStatus(), ServerStatus.BUILD);

      assertTrue(response.getAddresses().getPublicAddresses().isEmpty());
      assertTrue(response.getAddresses().getPrivateAddresses().isEmpty());
   }

   public static Server parseServer() throws NoSuchMethodException, ClassNotFoundException {

      Injector i = Guice.createInjector(new GsonModule() {
         @Override
         protected void configure() {
            super.configure();
            bind(DateAdapter.class).to(Iso8601DateAdapter.class);
         }
      });

      InputStream is = ParseServerFromJsonNoAddressesResponseTest.class.getResourceAsStream("/test_get_server_detail_no_addresses.json");

      UnwrapOnlyJsonValue<Server> parser = i.getInstance(Key.get(new TypeLiteral<UnwrapOnlyJsonValue<Server>>() {
      }));

      return parser.apply(new HttpResponse(200, "ok", Payloads.newInputStreamPayload(is)));
   }

}
