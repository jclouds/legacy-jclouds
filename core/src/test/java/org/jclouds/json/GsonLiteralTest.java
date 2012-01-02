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
package org.jclouds.json;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertEquals;

import java.lang.reflect.Type;
import java.util.Map;

import org.jclouds.json.internal.JsonLiteral;
import org.jclouds.util.Patterns;
import org.testng.annotations.Test;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonWriter;

/**
 * Shows how we currently allow users to specify json literal types. Note this requires patches to
 * {@link Streams} and {@link JsonWriter} in order to register and render JsonLiteral elements.
 * 
 * @author Adrian Cole
 * @see <a href="http://code.google.com/p/google-gson/issues/detail?id=326"/>
 * @see JsonLiteral
 */
@Test(testName = "GsonLiteralTest")
public class GsonLiteralTest {

   /**
    * User supplied type that holds json literally. Ex. number as {@code 8}, boolean as {@code true}
    * , string as {@code "value"}, object as {@code , list {@code []}.
    */
   static class RawJson implements CharSequence {

      private final String value;

      public RawJson(double value) {
         this.value = value + "";
      }

      public RawJson(int value) {
         this.value = value + "";
      }

      public RawJson(long value) {
         this.value = value + "";
      }

      public RawJson(boolean value) {
         this.value = value + "";
      }

      public RawJson(String value) {
         this.value = quoteStringIfNotNumberOrBoolean(checkNotNull(value, "value"));
      }

      @Override
      public int hashCode() {
         return Objects.hashCode(value);
      }

      @Override
      public boolean equals(Object that) {
         if (that == null)
            return false;
         return Objects.equal(this.toString(), that.toString());
      }

      @Override
      public String toString() {
         return value;
      }

      static String quoteStringIfNotNumberOrBoolean(String in) {
         if (Patterns.JSON_STRING_PATTERN.matcher(in).find() && !Patterns.JSON_NUMBER_PATTERN.matcher(in).find()
                  && !Patterns.JSON_BOOLEAN_PATTERN.matcher(in).find()) {
            return "\"" + in + "\"";
         }
         return in;
      }

      @Override
      public char charAt(int index) {
         return value.charAt(index);
      }

      @Override
      public int length() {
         return value.length();
      }

      @Override
      public CharSequence subSequence(int start, int end) {
         return value.subSequence(start, end);
      }

   }

   /**
    * writes or reads the literal directly and without formatting it in any way. Note this only
    * works as {@link Streams} and {@link JsonWriter} are patched to register and render JsonLiteral
    * elements.
    */
   public static class RawJsonAdapter implements JsonSerializer<RawJson>, JsonDeserializer<RawJson> {

      public JsonElement serialize(RawJson src, Type typeOfSrc, JsonSerializationContext context) {
         return new JsonLiteral(src);
      }

      public RawJson deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
               throws JsonParseException {
         return new RawJson(json.toString());
      }

   }

   // register the type adapter so that gson can serialize/deserialize to it
   private Gson gson = new GsonBuilder().registerTypeAdapter(RawJson.class, new RawJsonAdapter()).create();
   Type type = new TypeToken<Map<String, RawJson>>() {
   }.getType();

   public void testObject() {
      String json = "{\"tomcat6\":{\"ssl_port\":8433}}";

      Map<String, RawJson> map = ImmutableMap.<String, RawJson> of("tomcat6", new RawJson("{\"ssl_port\":8433}"));

      assertEquals(gson.fromJson(json, type), map);
      assertEquals(gson.toJson(map), json);
   }

   public void testList() {
      String json = "{\"list\":[8431,8433]}";

      Map<String, RawJson> map = ImmutableMap.<String, RawJson> of("list", new RawJson("[8431,8433]"));

      assertEquals(gson.fromJson(json, type), map);
      assertEquals(gson.toJson(map), json);
   }

   public void testString() {
      String json = "{\"name\":\"fooy\"}";

      Map<String, RawJson> map = ImmutableMap.<String, RawJson> of("name", new RawJson("fooy"));

      assertEquals(gson.fromJson(json, type), map);
      assertEquals(gson.toJson(map), json);
   }

   public void testNumber() {
      String json = "{\"number\":1.0}";

      Map<String, RawJson> map = ImmutableMap.<String, RawJson> of("number", new RawJson(1.0));

      assertEquals(gson.fromJson(json, type), map);
      assertEquals(gson.toJson(map), json);
   }

   public void testBoolean() {
      String json = "{\"boolean\":false}";

      Map<String, RawJson> map = ImmutableMap.<String, RawJson> of("boolean", new RawJson(false));

      assertEquals(gson.fromJson(json, type), map);
      assertEquals(gson.toJson(map), json);
   }
}
