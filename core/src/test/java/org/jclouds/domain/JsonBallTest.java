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
import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.io.Payloads;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
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
   private Gson mapper;

   @BeforeTest
   protected void setUpInjector() throws IOException {
      Injector injector = Guice.createInjector(new ParserModule());
      handler = injector.getInstance(Key.get(new TypeLiteral<ParseJson<Map<String, JsonBall>>>() {
      }));
      mapper = injector.getInstance(Gson.class);

   }

   public void test() {
      String json = "{\"tomcat6\":{\"ssl_port\":8433}}";

      Map<String, JsonBall> map = ImmutableMap.<String, JsonBall> of("tomcat6", new JsonBall("{\"ssl_port\":8433}"));

      assertEquals(handler.apply(new HttpResponse(200, "ok", Payloads
            .newStringPayload(json))), map);
      assertEquals(mapper.toJson(map), json);

   }

}
