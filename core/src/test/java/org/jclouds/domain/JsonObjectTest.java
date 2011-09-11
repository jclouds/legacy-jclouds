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
package org.jclouds.domain;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Map;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.io.Payloads;
import org.jclouds.json.Json;
import org.jclouds.json.config.GsonModule;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class JsonObjectTest {
   private ParseJson<Map<String, Object>> handler;
   private Json mapper;

   @BeforeTest
   protected void setUpInjector() throws IOException {
      Injector injector = Guice.createInjector(new GsonModule());
      handler = injector.getInstance(Key.get(new TypeLiteral<ParseJson<Map<String, Object>>>() {
      }));
      mapper = injector.getInstance(Json.class);

   }

   public void testHash() {
      String json = "{\"tomcat6\":{\"ssl_port\":8433}}";

      Map<String, Object> map = ImmutableMap.<String, Object> of("tomcat6", ImmutableMap.of("ssl_port", 8433));

      assertEquals(handler.apply(new HttpResponse(200, "ok", Payloads.newStringPayload(json))), map);
      assertEquals(mapper.toJson(map), json);

   }

   public void testList() {
      String json = "{\"list\":[8431,8433]}";

      Map<String, Object> map = ImmutableMap.<String, Object> of("list", ImmutableList.of(8431, 8433));

      assertEquals(handler.apply(new HttpResponse(200, "ok", Payloads.newStringPayload(json))), map);
      assertEquals(mapper.toJson(map), json);

   }

   public void testString() {
      String json = "{\"name\":\"fooy\"}";

      Map<String, Object> map = ImmutableMap.<String, Object> of("name", "fooy");

      assertEquals(handler.apply(new HttpResponse(200, "ok", Payloads.newStringPayload(json))), map);
      assertEquals(mapper.toJson(map), json);

   }

   public void testNumber() {
      String json = "{\"number\":1}";

      Map<String, Object> map = ImmutableMap.<String, Object> of("number", 1);

      assertEquals(handler.apply(new HttpResponse(200, "ok", Payloads.newStringPayload(json))), map);
      assertEquals(mapper.toJson(map), json);

   }
}
