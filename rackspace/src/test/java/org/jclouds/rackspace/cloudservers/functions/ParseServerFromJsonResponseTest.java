/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code ParseServerFromJsonResponseTest}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "cloudservers.ParseServerFromJsonResponseTest")
public class ParseServerFromJsonResponseTest {

   Injector i = Guice.createInjector(new ParserModule());

   public void testApplyInputStreamDetails() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/cloudservers/test_get_server_detail.json");

      ParseServerFromJsonResponse parser = new ParseServerFromJsonResponse(i
               .getInstance(Gson.class));
      Server response = parser.apply(is);
      assertEquals(response.getId(), 1234);
      assertEquals(response.getName(), "sample-server");
      assertEquals(response.getImageId(), new Integer(2));
      assertEquals(response.getFlavorId(), new Integer(1));
      assertEquals(response.getHostId(), "e4d909c290d0fb1ca068ffaddf22cbd0");
      assertEquals(response.getStatus(), ServerStatus.BUILD);
      assertEquals(response.getProgress(), new Integer(60));
      List<InetAddress> publicAddresses = Lists.newArrayList(InetAddress.getByName("67.23.10.132"),
               InetAddress.getByName("67.23.10.131"));
      List<InetAddress> privateAddresses = Lists
               .newArrayList(InetAddress.getByName("10.176.42.16"));
      Addresses addresses1 = new Addresses();
      addresses1.getPrivateAddresses().addAll(privateAddresses);
      addresses1.getPublicAddresses().addAll(publicAddresses);
      assertEquals(response.getAddresses(), addresses1);
      assertEquals(response.getMetadata(), ImmutableMap.of("Server Label", "Web Head 1",
               "Image Version", "2.1"));

   }

}
