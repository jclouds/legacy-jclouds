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

import org.jclouds.cloudservers.domain.Flavor;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.UnwrapOnlyJsonValue;
import org.jclouds.json.config.GsonModule;
import org.testng.annotations.Test;

import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code ParseFlavorFromJsonResponse}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ParseFlavorFromJsonResponseTest {
   public void test() {
      Flavor response = parseFlavor();

      String json = new Gson().toJson(response);

      assertEquals(json, "{\"id\":1,\"name\":\"256 MB Server\",\"disk\":10,\"ram\":256}");
   }

   public static Flavor parseFlavor() {
      Injector i = Guice.createInjector(new GsonModule());

      InputStream is = ParseFlavorFromJsonResponseTest.class.getResourceAsStream("/test_get_flavor_details.json");

      UnwrapOnlyJsonValue<Flavor> parser = i.getInstance(Key.get(new TypeLiteral<UnwrapOnlyJsonValue<Flavor>>() {
      }));
      Flavor response = parser.apply(HttpResponse.builder().statusCode(200).message("ok").payload(is).build());
      return response;
   }

}
