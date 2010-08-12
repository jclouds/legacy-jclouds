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

package org.jclouds.ohai.functions;

import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.jclouds.chef.config.ChefParserModule;
import org.jclouds.domain.JsonBall;
import org.jclouds.json.Json;
import org.jclouds.json.config.GsonModule;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMultimap;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code NestSlashKeys}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", sequential = true, testName = "ohai.NestSlashKeysTest")
public class NestSlashKeysTest {

   private NestSlashKeys converter;
   private Json json;

   @BeforeTest
   protected void setUpInjector() throws IOException {
      Injector injector = Guice.createInjector(new ChefParserModule(), new GsonModule());
      converter = injector.getInstance(NestSlashKeys.class);
      json = injector.getInstance(Json.class);
   }

   @Test
   public void testBase() {
      assertEquals(json.toJson(converter.apply(ImmutableMultimap.<String, Supplier<JsonBall>> of("java", Suppliers
            .ofInstance(new JsonBall("java"))))), "{\"java\":\"java\"}");
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testIllegal() {
      json.toJson(converter.apply(ImmutableMultimap.<String, Supplier<JsonBall>> of("java", Suppliers
            .ofInstance(new JsonBall("java")), "java/system", Suppliers.ofInstance(new JsonBall("system")))));
   }

   @Test
   public void testOne() {
      assertEquals(json.toJson(converter.apply(ImmutableMultimap.<String, Supplier<JsonBall>> of("java", Suppliers
            .ofInstance(new JsonBall("{\"time\":\"time\"}")), "java/system", Suppliers
            .ofInstance(new JsonBall("system"))))), "{\"java\":{\"time\":\"time\",\"system\":\"system\"}}");
   }

   @Test
   public void testOneDuplicate() {
      assertEquals(json.toJson(converter.apply(ImmutableMultimap.<String, Supplier<JsonBall>> of("java", Suppliers
            .ofInstance(new JsonBall("{\"time\":\"time\"}")), "java", Suppliers.ofInstance(new JsonBall(
            "{\"system\":\"system\"}"))))), "{\"java\":{\"time\":\"time\",\"system\":\"system\"}}");
   }

   @Test
   public void testMerge() {
      assertEquals(json.toJson(converter.apply(ImmutableMultimap.<String, Supplier<JsonBall>> of("java", Suppliers
            .ofInstance(new JsonBall("{\"time\":{\"1\":\"hello\"}}")), "java/time", Suppliers.ofInstance(new JsonBall(
            "{\"2\":\"goodbye\"}"))))), "{\"java\":{\"time\":{\"1\":\"hello\",\"2\":\"goodbye\"}}}");
   }
   @Test
   public void testMergeNestedTwice() {
      assertEquals(json.toJson(converter.apply(ImmutableMultimap.<String, Supplier<JsonBall>> of("java", Suppliers
            .ofInstance(new JsonBall("{\"time\":{\"1\":\"hello\"}}")), "java", Suppliers.ofInstance(new JsonBall(
            "{\"time\":{\"2\":\"goodbye\"}}"))))), "{\"java\":{\"time\":{\"1\":\"hello\",\"2\":\"goodbye\"}}}");
   }

   @Test
   public void testReplaceList() {
      assertEquals(json.toJson(converter.apply(ImmutableMultimap.<String, Supplier<JsonBall>> of("java", Suppliers
            .ofInstance(new JsonBall("{\"time\":{\"1\":[\"hello\"]}}")), "java/time", Suppliers.ofInstance(new JsonBall(
            "{\"1\":[\"goodbye\"]}"))))), "{\"java\":{\"time\":{\"1\":[\"goodbye\"]}}}");
   }
}
