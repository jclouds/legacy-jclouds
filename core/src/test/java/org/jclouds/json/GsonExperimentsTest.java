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

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
@Test
public class GsonExperimentsTest {
   public static final String json = "['hello',5,{name:'GREETINGS',source:'guest'}]";

   static class Event {
      private String name;
      private String source;

      private Event(String name, String source) {
         this.name = name;
         this.source = source;
      }

      @Override
      public String toString() {
         return String.format("(name=%s, source=%s)", name, source);
      }
   }

   private Gson gson;
   private String json2;

   @BeforeTest
   void setupSource() {
      gson = new Gson();
      Collection<Object> collection = new ArrayList<Object>();
      collection.add("hello");
      collection.add(5);
      collection.add(new Event("GREETINGS", "guest"));
      json2 = gson.toJson(collection);
      assertEquals(json2, "[\"hello\",5,{\"name\":\"GREETINGS\",\"source\":\"guest\"}]");
   }

   // inspired by
   // http://code.google.com/p/google-gson/source/browse/trunk/extras/src/main/java/com/google/gson/extras/examples/rawcollections/RawCollectionsExample.java
   public void testRawCollectionsWithParser() {
      JsonParser parser = new JsonParser();
      JsonArray array = parser.parse(json).getAsJsonArray();
      String message = gson.fromJson(array.get(0), String.class);
      int number = gson.fromJson(array.get(1), int.class);
      Event event = gson.fromJson(array.get(2), Event.class);
      assertEquals(message, "hello");
      assertEquals(number, 5);
      assertEquals(event.toString(), new Event("GREETINGS", "guest").toString());
   }

   private final String nested = "{ \"count\":1 ,\"event\" : [  {name:'GREETINGS',source:'guest'} ] }";
   private final String nestedFurther = "{ \"listaccountsresponse\" : { \"count\":1 ,\"event\" : [  {name:'GREETINGS',source:'guest'} ] } }";

   // inspired by http://sites.google.com/site/gson/streaming
   public void testParseNestedElements() throws IOException {
      JsonReader reader = new JsonReader(new StringReader(nested));
      List<Event> val = parseThingFromReaderOrNull("event", reader, new TypeLiteral<List<Event>>() {
      }.getType());
      assertEquals(val.toString(), "[(name=GREETINGS, source=guest)]");
   }

   public void testParseNestedFurtherElements() throws IOException {
      JsonReader reader = new JsonReader(new StringReader(nestedFurther));
      List<Event> val = parseThingFromReaderOrNull("event", reader, new TypeLiteral<List<Event>>() {
      }.getType());
      assertEquals(val.toString(), "[(name=GREETINGS, source=guest)]");
   }

   protected <T> T parseThingFromReaderOrNull(String toFind, JsonReader reader, Type type) throws IOException {
      AtomicReference<String> name = new AtomicReference<String>();
      JsonToken token = reader.peek();
      for (; token != JsonToken.END_DOCUMENT && nnn(toFind, reader, token, name); token = skipAndPeek(token, reader))
         ;
      T val = gson.<T>fromJson(reader, type);
      reader.close();
      return val;
   }

   protected boolean nnn(String toFind, JsonReader reader, JsonToken token, AtomicReference<String> name)
         throws IOException {
      if (token == JsonToken.NAME) {
         String name2 = reader.nextName();
         if (toFind.equals(name2)) {
            name.set(name2);
            return false;
         }
      }
      return true;

   }

   public JsonToken skipAndPeek(JsonToken token, JsonReader reader) throws IOException {
      switch (token) {
      case BEGIN_ARRAY:
         reader.beginArray();
         break;
      case END_ARRAY:
         reader.endArray();
         break;
      case BEGIN_OBJECT:
         reader.beginObject();
         break;
      case END_OBJECT:
         reader.endObject();
         break;
      case NAME:
         // NOTE that we have already advanced on NAME in the eval block;
         break;
      case STRING:
         reader.nextString();
         break;
      case NUMBER:
         reader.nextString();
         break;
      case BOOLEAN:
         reader.nextBoolean();
         break;
      case NULL:
         reader.nextNull();
         break;
      case END_DOCUMENT:
         break;
      }
      return reader.peek();
   }
}
