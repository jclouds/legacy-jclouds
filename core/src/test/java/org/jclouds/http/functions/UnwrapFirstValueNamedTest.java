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
package org.jclouds.http.functions;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jclouds.http.HttpResponse;
import org.jclouds.io.Payloads;
import org.jclouds.json.config.GsonModule;
import org.jclouds.json.internal.GsonWrapper;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
@Test
public class UnwrapFirstValueNamedTest {

   GsonWrapper json = Guice.createInjector(new GsonModule()).getInstance(GsonWrapper.class);

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

   public void testParseNestedElements() throws IOException {
      String nested = "{ \"count\":1 ,\"event\" : [  {name:'GREETINGS',source:'guest'} ] }";
      HttpResponse response = HttpResponse.builder().statusCode(200).message("goodie")
            .payload(Payloads.newPayload(nested)).build();

      List<Event> val = new ParseFirstJsonValueNamed<List<Event>>(json, new TypeLiteral<List<Event>>() {
      }, "event").apply(response);
      assertEquals(val.toString(), "[(name=GREETINGS, source=guest)]");
   }

   public void testParseNestedElementsButNothing() throws IOException {
      String nested = "{ \"count\":1 ,\"event\" : [  ] }";
      HttpResponse response = HttpResponse.builder().statusCode(200).message("goodie")
            .payload(Payloads.newPayload(nested)).build();

      List<Event> val = new ParseFirstJsonValueNamed<List<Event>>(json, new TypeLiteral<List<Event>>() {
      }, "event").apply(response);
      assertEquals(val.toString(), "[]");
   }

   public void testParseNestedFurtherElements() throws IOException {
      String nestedFurther = "{ \"listaccountsresponse\" : { \"count\":1 ,\"event\" : [  {name:'GREETINGS',source:'guest'} ] } }";
      HttpResponse response = HttpResponse.builder().statusCode(200).message("goodie")
            .payload(Payloads.newPayload(nestedFurther)).build();

      List<Event> val = new ParseFirstJsonValueNamed<List<Event>>(json, new TypeLiteral<List<Event>>() {
      }, "event").apply(response);
      assertEquals(val.toString(), "[(name=GREETINGS, source=guest)]");
   }

   public void testParseNestedFurtherElementsButNothing() throws IOException {
      String nestedFurther = "{ \"listaccountsresponse\" : { \"count\":1 ,\"event\" : [ ] } }";
      HttpResponse response = HttpResponse.builder().statusCode(200).message("goodie")
            .payload(Payloads.newPayload(nestedFurther)).build();

      List<Event> val = new ParseFirstJsonValueNamed<List<Event>>(json, new TypeLiteral<List<Event>>() {
      }, "event").apply(response);
      assertEquals(val.toString(), "[]");
   }

   public void testParseNoPayloadEmptyList() throws IOException {
      HttpResponse response = HttpResponse.builder().statusCode(200).message("goodie").build();

      List<Event> val = new ParseFirstJsonValueNamed<List<Event>>(json, new TypeLiteral<List<Event>>() {
      }, "event").apply(response);
      assertEquals(val, ImmutableList.<Event> of());
   }

   public void testParseNoPayloadEmptyMap() throws IOException {
      HttpResponse response = HttpResponse.builder().statusCode(200).message("goodie").build();

      Map<String, String> val = new ParseFirstJsonValueNamed<Map<String, String>>(json,
            new TypeLiteral<Map<String, String>>() {
            }, "event").apply(response);
      assertEquals(val, ImmutableMap.<String, String> of());
   }

   public void testParseNoPayloadEmptySet() throws IOException {
      HttpResponse response = HttpResponse.builder().statusCode(200).message("goodie").build();

      Set<Event> val = new ParseFirstJsonValueNamed<Set<Event>>(json, new TypeLiteral<Set<Event>>() {
      }, "event").apply(response);
      assertEquals(val, ImmutableSet.<Event> of());
   }
}
