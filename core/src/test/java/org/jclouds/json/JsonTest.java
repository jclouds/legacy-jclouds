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
package org.jclouds.json;

import static com.google.common.io.BaseEncoding.base16;
import static com.google.common.primitives.Bytes.asList;
import static org.testng.Assert.assertEquals;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.jclouds.json.config.GsonModule;
import org.jclouds.json.config.GsonModule.DefaultExclusionStrategy;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.FieldAttributes;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.TypeLiteral;

@Test
public class JsonTest {
   private Json json = Guice.createInjector(new GsonModule()).getInstance(Json.class);

   private static class ObjectNoDefaultConstructor {
      private final String stringValue;
      private final int intValue;

      public ObjectNoDefaultConstructor(String stringValue, int intValue) {
         this.stringValue = stringValue;
         this.intValue = intValue;
      }

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + intValue;
         result = prime * result + ((stringValue == null) ? 0 : stringValue.hashCode());
         return result;
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj)
            return true;
         if (obj == null)
            return false;
         if (getClass() != obj.getClass())
            return false;
         ObjectNoDefaultConstructor other = (ObjectNoDefaultConstructor) obj;
         if (intValue != other.intValue)
            return false;
         if (stringValue == null) {
            if (other.stringValue != null)
               return false;
         } else if (!stringValue.equals(other.stringValue))
            return false;
         return true;
      }
   }

   public void testObjectNoDefaultConstructor() {
      ObjectNoDefaultConstructor obj = new ObjectNoDefaultConstructor("foo", 1);
      assertEquals(json.toJson(obj), "{\"stringValue\":\"foo\",\"intValue\":1}");
      ObjectNoDefaultConstructor obj2 = json.fromJson(json.toJson(obj), ObjectNoDefaultConstructor.class);
      assertEquals(obj2, obj);
      assertEquals(json.toJson(obj2), json.toJson(obj));
   }
   
   static class ExcludeStringValue implements DefaultExclusionStrategy {
      public boolean shouldSkipClass(Class<?> clazz) {
        return false;
      }

      public boolean shouldSkipField(FieldAttributes f) {
        return f.getName().equals("stringValue");
      }
   }

   public void testExcluder() {
      Json excluder = Guice.createInjector(new GsonModule(), new AbstractModule() {
         protected void configure() {
            bind(DefaultExclusionStrategy.class).to(ExcludeStringValue.class);
         }
      }).getInstance(Json.class);
      ObjectNoDefaultConstructor obj = new ObjectNoDefaultConstructor("foo", 1);
      assertEquals(excluder.toJson(obj), "{\"intValue\":1}");
   }

   private static class EnumInside {
      private static enum Test {
         FOO, BAR;
      }

      private Test enumValue;
   }

   private static class ByteList {
      List<Byte> checksum;
   }

   public void testByteList() {
      ByteList bl = new ByteList();
      bl.checksum = asList(base16().lowerCase().decode("1dda05ed139664f1f89b9dec482b77c0"));
      assertEquals(json.toJson(bl), "{\"checksum\":\"1dda05ed139664f1f89b9dec482b77c0\"}");
      assertEquals(json.fromJson(json.toJson(bl), ByteList.class).checksum, bl.checksum);
   }

   public void testPropertiesSerializesDefaults() {
      Properties props = new Properties();
      props.put("string", "string");
      props.put("number", "1");
      props.put("boolean", "true");
      assertEquals(json.toJson(props), "{\"string\":\"string\",\"boolean\":\"true\",\"number\":\"1\"}");
      Properties props3 = new Properties(props);
      assertEquals(json.toJson(props3), "{\"string\":\"string\",\"boolean\":\"true\",\"number\":\"1\"}");
      Properties props2 = json.fromJson(json.toJson(props), Properties.class);
      assertEquals(props2, props);
      assertEquals(json.toJson(props2), json.toJson(props));
   }

   public void testMapStringObjectWithAllValidValuesOneDeep() {
      Map<String, Object> map = Maps.newHashMap();
      map.put("string", "string");
      map.put("number", 1.0);
      map.put("boolean", true);
      map.put("map", ImmutableMap.of("key", "value"));
      map.put("list", ImmutableList.of("key", "value"));
      assertEquals(json.toJson(map),
               "{\"string\":\"string\",\"map\":{\"key\":\"value\"},\"list\":[\"key\",\"value\"],\"boolean\":true,\"number\":1.0}");
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

   @Test(expectedExceptions = IllegalArgumentException.class)
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
