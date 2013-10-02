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
package org.jclouds.json.internal;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Tests to verify and illustrate the behavior of the OptionalTypeAdaptorFactory.
 * 
 * @author Adam Lowe
 */
@Test(testName = "OptionalTypeAdapterFactoryTest")
public class OptionalTypeAdapterFactoryTest {

   /**
    * Simple type with an Optional field
    */
   static class SimpleBean {
      private final Optional<String> value;
      private final String someOtherValue;

      public SimpleBean(Optional<String> value, String someOtherValue) {
         this.value = value;
         this.someOtherValue = someOtherValue;
      }

      // Required to ensure GSON doesn't initialize our Optional to null!
      @SuppressWarnings("unused")
      private SimpleBean() {
         this.value = Optional.absent();
         this.someOtherValue = null;
      }

      public Optional<String> getValue() {
         return value;
      }

      public String getSomeOtherValue() {
         return someOtherValue;
      }

      public boolean equals(Object other) {
         SimpleBean that = SimpleBean.class.cast(other);
         return Objects.equal(value, that.value) && Objects.equal(someOtherValue, that.someOtherValue);
      }
   }

   // register the type adapter so that gson can serialize/deserialize to it
   private Gson gsonAdapter = new GsonBuilder().registerTypeAdapterFactory(new OptionalTypeAdapterFactory()).create();

   public void testValuePresent() {
      String json = "{\"value\":\"a test string!\"}";
      SimpleBean expected = new SimpleBean(Optional.of("a test string!"), null);

      SimpleBean actual = gsonAdapter.fromJson(json, SimpleBean.class);
      
      assertTrue(actual.value.isPresent());
      assertEquals(actual.getValue().get(), "a test string!");
      assertNull(actual.getSomeOtherValue());
      
      assertEquals(actual, expected);
      assertEquals(gsonAdapter.toJson(actual), json);
  }

   public void testValueAbsent() {
      String json = "{\"someOtherValue\":\"testvalue\"}";
      SimpleBean expected = new SimpleBean(Optional.<String>absent(), "testvalue");

      SimpleBean actual = gsonAdapter.fromJson(json, SimpleBean.class);

      assertFalse(actual.value.isPresent());
      assertEquals(actual.getSomeOtherValue(), "testvalue");

      assertEquals(actual, expected);
      assertEquals(gsonAdapter.toJson(actual), json);
   }

   public void testValueNull() {
      String json = "{\"value\":null}";
      SimpleBean expected = new SimpleBean(Optional.<String>absent(), null);

      SimpleBean actual = gsonAdapter.fromJson(json, SimpleBean.class);

      assertFalse(actual.value.isPresent());
      assertNull(actual.getSomeOtherValue());

      assertEquals(actual, expected);
      // Note: the field is omitted from serialized form!
      assertEquals(gsonAdapter.toJson(actual), "{}");
   }

}
