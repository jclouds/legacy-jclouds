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

import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "jclouds.JsonBallTest")
public class JsonBallTest {
   private ParseJson<Map<String, JsonBall>> handler;
   private Json mapper;

   @BeforeTest
   protected void setUpInjector() throws IOException {
      Injector injector = Guice.createInjector(new GsonModule());
      handler = injector.getInstance(Key.get(new TypeLiteral<ParseJson<Map<String, JsonBall>>>() {
      }));
      mapper = injector.getInstance(Json.class);

   }

   public void testHash() {
      String json = "{\"tomcat6\":{\"ssl_port\":8433}}";

      Map<String, JsonBall> map = ImmutableMap.<String, JsonBall> of("tomcat6", new JsonBall("{\"ssl_port\":8433}"));

      assertEquals(handler.apply(new HttpResponse(200, "ok", Payloads
            .newStringPayload(json))), map);
      assertEquals(mapper.toJson(map), json);

   }


   public void testList() {
      String json = "{\"list\":[8431,8433]}";

      Map<String, JsonBall> map = ImmutableMap.<String, JsonBall> of("list", new JsonBall("[8431,8433]"));

      assertEquals(handler.apply(new HttpResponse(200, "ok", Payloads
            .newStringPayload(json))), map);
      assertEquals(mapper.toJson(map), json);

   }

   public void testString() {
      String json = "{\"name\":\"fooy\"}";

      Map<String, JsonBall> map = ImmutableMap.<String, JsonBall> of("name", new JsonBall("fooy"));

      assertEquals(handler.apply(new HttpResponse(200, "ok", Payloads
            .newStringPayload(json))), map);
      assertEquals(mapper.toJson(map), json);

   }
   

   public void testNumber() {
      String json = "{\"number\":1}";

      Map<String, JsonBall> map = ImmutableMap.<String, JsonBall> of("number", new JsonBall("1"));

      assertEquals(handler.apply(new HttpResponse(200, "ok", Payloads
            .newStringPayload(json))), map);
      assertEquals(mapper.toJson(map), json);

   }
}
