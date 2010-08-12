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

package org.jclouds.chef.functions;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Collections;

import org.jclouds.chef.config.ChefParserModule;
import org.jclouds.chef.domain.Node;
import org.jclouds.domain.JsonBall;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.io.Payloads;
import org.jclouds.json.config.GsonModule;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code ParseNodeFromJson}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", sequential = true, testName = "chef.ParseNodeFromJsonTest")
public class ParseNodeFromJsonTest {

   private ParseJson<Node> handler;

   @BeforeTest
   protected void setUpInjector() throws IOException {
      Injector injector = Guice.createInjector(new ChefParserModule(), new GsonModule());
      handler = injector.getInstance(Key.get(new TypeLiteral<ParseJson<Node>>() {
      }));
   }

   public void test() {

      Node node = new Node("adrian-jcloudstest", ImmutableMap.<String, JsonBall> of("tomcat6", new JsonBall(
            "{\"ssl_port\":8433}")), ImmutableMap.<String, JsonBall> of(), ImmutableMap.<String, JsonBall> of(),
            ImmutableMap.<String, JsonBall> of(), Collections.singleton("recipe[java]"));

      assertEquals(handler.apply(new HttpResponse(200, "ok", Payloads.newPayload(ParseCookbookVersionFromJsonTest.class
            .getResourceAsStream("/node.json")))), node);
   }
}
