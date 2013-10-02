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

import org.jclouds.cloudservers.domain.Flavor;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.UnwrapOnlyJsonValue;
import org.jclouds.json.config.GsonModule;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code ParseFlavorListFromJsonResponse}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ParseFlavorListFromJsonResponseTest {

   Injector i = Guice.createInjector(new GsonModule());

   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream("/test_list_flavors.json");

      List<Flavor> expects = ImmutableList.of(Flavor.builder().id(1).name("256 MB Server").build(),
            Flavor.builder().id(2).name("512 MB Server").build());

      UnwrapOnlyJsonValue<List<Flavor>> parser = i.getInstance(Key
            .get(new TypeLiteral<UnwrapOnlyJsonValue<List<Flavor>>>() {
            }));
      List<Flavor> response = parser.apply(HttpResponse.builder().statusCode(200).message("ok").payload(is).build());
      assertEquals(response, expects);
   }

   public void testApplyInputStreamDetails() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/test_list_flavors_detail.json");

      UnwrapOnlyJsonValue<List<Flavor>> parser = i.getInstance(Key
            .get(new TypeLiteral<UnwrapOnlyJsonValue<List<Flavor>>>() {
            }));
      List<Flavor> response = parser.apply(HttpResponse.builder().statusCode(200).message("ok").payload(is).build());
      assertEquals(response.get(0).getId(), 1);
      assertEquals(response.get(0).getName(), "256 MB Server");
      assertEquals(response.get(0).getDisk(), Integer.valueOf(10));
      assertEquals(response.get(0).getRam(), Integer.valueOf(256));

      assertEquals(response.get(1).getId(), 2);
      assertEquals(response.get(1).getName(), "512 MB Server");
      assertEquals(response.get(1).getDisk(), Integer.valueOf(20));
      assertEquals(response.get(1).getRam(), Integer.valueOf(512));

   }

}
