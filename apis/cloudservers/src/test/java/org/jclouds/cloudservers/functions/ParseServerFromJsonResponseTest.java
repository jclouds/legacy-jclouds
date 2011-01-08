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

package org.jclouds.cloudservers.functions;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.List;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.UnwrapOnlyJsonValue;
import org.jclouds.io.Payloads;
import org.jclouds.json.config.GsonModule;
import org.jclouds.cloudservers.domain.Addresses;
import org.jclouds.cloudservers.domain.Server;
import org.jclouds.cloudservers.domain.ServerStatus;
import org.jclouds.rackspace.config.RackspaceParserModule;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code ParseServerFromJsonResponse}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ParseServerFromJsonResponseTest {

   public void testApplyInputStreamDetails() throws UnknownHostException {
      Server response = parseServer();

      assertEquals(response.getId(), 1234);
      assertEquals(response.getName(), "sample-server");
      assertEquals(response.getImageId(), new Integer(2));
      assertEquals(response.getFlavorId(), new Integer(1));
      assertEquals(response.getHostId(), "e4d909c290d0fb1ca068ffaddf22cbd0");
      assertEquals(response.getStatus(), ServerStatus.BUILD);
      assertEquals(response.getProgress(), new Integer(60));
      List<String> publicAddresses = Lists.newArrayList("67.23.10.132", "67.23.10.131");
      List<String> privateAddresses = Lists.newArrayList("10.176.42.16");
      Addresses addresses1 = new Addresses();
      addresses1.getPrivateAddresses().addAll(privateAddresses);
      addresses1.getPublicAddresses().addAll(publicAddresses);
      assertEquals(response.getAddresses(), addresses1);
      assertEquals(response.getMetadata(), ImmutableMap.of("Server Label", "Web Head 1", "Image Version", "2.1"));

   }

   public static Server parseServer() {
      Injector i = Guice.createInjector(new RackspaceParserModule(), new GsonModule());

      InputStream is = ParseServerFromJsonResponseTest.class.getResourceAsStream("/test_get_server_detail.json");

      UnwrapOnlyJsonValue<Server> parser = i.getInstance(Key.get(new TypeLiteral<UnwrapOnlyJsonValue<Server>>() {
      }));
      Server response = parser.apply(new HttpResponse(200, "ok", Payloads.newInputStreamPayload(is)));
      return response;
   }

}
