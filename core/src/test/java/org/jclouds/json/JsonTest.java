/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.json;

import static org.testng.Assert.assertEquals;

import java.util.Map;

import org.jclouds.json.config.GsonModule;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.JsonParseException;
import com.google.inject.Guice;
import com.google.inject.TypeLiteral;

@Test
public class JsonTest {
   private Json json = Guice.createInjector(new GsonModule()).getInstance(Json.class);

   private static class EnumInside {
      private static enum Test {
         FOO, BAR;
      }

      private Test enumValue;
   }

   public void testMapStringObjectWithAllValidValuesOneDeep() {
      Map<String, Object> map = Maps.newHashMap();
      map.put("string", "string");
      map.put("number", 1);
      map.put("boolean", true);
      map.put("map", ImmutableMap.of("key", "value"));
      map.put("list", ImmutableList.of("key", "value"));
      assertEquals(json.toJson(map),
            "{\"string\":\"string\",\"map\":{\"key\":\"value\"},\"list\":[\"key\",\"value\"],\"boolean\":true,\"number\":1}");
      Map<String, Object> map2 = json.fromJson(json.toJson(map), new TypeLiteral<Map<String, Object>>() {
      }.getType());
      assertEquals(map2, map);
      assertEquals(json.toJson(map2), json.toJson(map));
   }

   public void testMapStringObjectWithNumericalKeysConvertToStrings() {
      Map<String, Object> map = ImmutableMap.<String, Object> of("map", ImmutableMap.of(1, "value"));
      assertEquals(json.toJson(map), "{\"map\":{\"1\":\"value\"}}");
      Map<String, Object> map2 = json.fromJson(json.toJson(map), new TypeLiteral<Map<String, Object>>() {
      }.getType());
      // note conversion.. ensures valid
      assertEquals(map2, ImmutableMap.<String, Object> of("map", ImmutableMap.of("1", "value")));
      assertEquals(json.toJson(map2), json.toJson(map));
   }

   public void testMapStringObjectWithBooleanKeysConvertToStrings() {
      Map<String, Object> map = ImmutableMap.<String, Object> of("map", ImmutableMap.of(true, "value"));
      assertEquals(json.toJson(map), "{\"map\":{\"true\":\"value\"}}");
      Map<String, Object> map2 = json.fromJson(json.toJson(map), new TypeLiteral<Map<String, Object>>() {
      }.getType());
      // note conversion.. ensures valid
      assertEquals(map2, ImmutableMap.<String, Object> of("map", ImmutableMap.of("true", "value")));
      assertEquals(json.toJson(map2), json.toJson(map));
   }

   public void testDeserializeEnum() {
      assertEquals(json.fromJson("{enumValue : \"FOO\"}", EnumInside.class).enumValue, EnumInside.Test.FOO);
   }

   @Test(expectedExceptions = JsonParseException.class)
   public void testDeserializeEnumWhenBadValue() {
      assertEquals(json.fromJson("{enumValue : \"s\"}", EnumInside.class).enumValue, EnumInside.Test.FOO);
   }

   private static class EnumInsideWithParser {
      private static enum Test {
         FOO, BAR, UNRECOGNIZED;

         @SuppressWarnings("unused")
         public static Test fromValue(String state) {
            try {
               return valueOf(state);
            } catch (IllegalArgumentException e) {
               return UNRECOGNIZED;
            }
         }
      }

      private Test enumValue;
   }

   public void testDeserializeEnumWithParser() {
      assertEquals(json.fromJson("{enumValue : \"FOO\"}", EnumInsideWithParser.class).enumValue,
            EnumInsideWithParser.Test.FOO);
   }

   public void testDeserializeEnumWithParserAndBadValue() {
      assertEquals(json.fromJson("{enumValue : \"sd\"}", EnumInsideWithParser.class).enumValue,
            EnumInsideWithParser.Test.UNRECOGNIZED);
   }

}
