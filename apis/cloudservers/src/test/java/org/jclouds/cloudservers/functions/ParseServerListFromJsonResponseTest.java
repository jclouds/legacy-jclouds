/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.cloudservers.functions;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.List;

import org.jclouds.cloudservers.domain.Addresses;
import org.jclouds.cloudservers.domain.Server;
import org.jclouds.cloudservers.domain.ServerStatus;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.UnwrapOnlyJsonValue;
import org.jclouds.json.config.GsonModule;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
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

   Injector i = Guice.createInjector(new GsonModule());

   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream("/test_list_servers.json");

      List<Server> expects = ImmutableList.of(Server.builder().id(1234).name("sample-server").build(),
            Server.builder().id(5678).name("sample-server2").build());

      UnwrapOnlyJsonValue<List<Server>> parser = i.getInstance(Key
            .get(new TypeLiteral<UnwrapOnlyJsonValue<List<Server>>>() {
            }));
      List<Server> response = parser.apply(HttpResponse.builder().statusCode(200).message("ok").payload(is).build());

      assertEquals(response, expects);
   }

   public void testApplyInputStreamDetails() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/test_list_servers_detail.json");

      UnwrapOnlyJsonValue<List<Server>> parser = i.getInstance(Key
            .get(new TypeLiteral<UnwrapOnlyJsonValue<List<Server>>>() {
            }));
      List<Server> response = parser.apply(HttpResponse.builder().statusCode(200).message("ok").payload(is).build());

      assertEquals(response.get(0).getId(), 1234);
      assertEquals(response.get(0).getName(), "sample-server");
      assertEquals(response.get(0).getImageId(), Integer.valueOf(2));
      assertEquals(response.get(0).getFlavorId(), Integer.valueOf(1));
      assertEquals(response.get(0).getHostId(), "e4d909c290d0fb1ca068ffaddf22cbd0");
      assertEquals(response.get(0).getStatus(), ServerStatus.BUILD);
      assertEquals(response.get(0).getProgress(), Integer.valueOf(60));
      List<String> publicAddresses = Lists.newArrayList("67.23.10.132", "67.23.10.131");
      List<String> privateAddresses = Lists.newArrayList("10.176.42.16");
      Addresses addresses1 = Addresses.builder().privateAddresses(privateAddresses).publicAddresses(publicAddresses).build();
      assertEquals(response.get(0).getAddresses(), addresses1);
      assertEquals(response.get(0).getMetadata(), ImmutableMap.of("Server Label", "Web Head 1", "Image Version", "2.1"));
      assertEquals(response.get(1).getId(), 5678);
      assertEquals(response.get(1).getName(), "sample-server2");
      assertEquals(response.get(1).getImageId(), Integer.valueOf(2));
      assertEquals(response.get(1).getFlavorId(), Integer.valueOf(1));
      assertEquals(response.get(1).getHostId(), "9e107d9d372bb6826bd81d3542a419d6");
      assertEquals(response.get(1).getStatus(), ServerStatus.ACTIVE);
      assertEquals(response.get(1).getProgress(), null);
      List<String> publicAddresses2 = Lists.newArrayList("67.23.10.133");
      List<String> privateAddresses2 = Lists.newArrayList("10.176.42.17");
      Addresses addresses2 = Addresses.builder().privateAddresses(privateAddresses2).publicAddresses(publicAddresses2).build();
      assertEquals(response.get(1).getAddresses(), addresses2);
      assertEquals(response.get(1).getMetadata(), ImmutableMap.of("Server Label", "DB 1"));

   }

}
