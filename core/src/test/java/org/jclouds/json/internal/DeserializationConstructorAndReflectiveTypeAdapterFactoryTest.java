package org.jclouds.json.internal;
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

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.fail;

import java.beans.ConstructorProperties;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.json.internal.NamingStrategies.AnnotationOrNameFieldNamingStrategy;
import org.jclouds.json.internal.NamingStrategies.ExtractNamed;
import org.jclouds.json.internal.NamingStrategies.ExtractSerializedName;
import org.testng.annotations.Test;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.Excluder;
import com.google.gson.reflect.TypeToken;

/**
 * @author Adrian Cole
 * @author Adam Lowe
 */
@Test(testName = "DeserializationConstructorTypeAdapterFactoryTest")
public final class DeserializationConstructorAndReflectiveTypeAdapterFactoryTest {

   Gson gson = new Gson();

   DeserializationConstructorAndReflectiveTypeAdapterFactory parameterizedCtorFactory = parameterizedCtorFactory();

   static DeserializationConstructorAndReflectiveTypeAdapterFactory parameterizedCtorFactory() {
      FieldNamingStrategy serializationPolicy = new AnnotationOrNameFieldNamingStrategy(
            ImmutableSet.of(new ExtractSerializedName(), new ExtractNamed())
      );
      NamingStrategies.AnnotationConstructorNamingStrategy deserializationPolicy =
            new NamingStrategies.AnnotationConstructorNamingStrategy(
                  ImmutableSet.of(ConstructorProperties.class, Inject.class),
                  ImmutableSet.of(new ExtractNamed()));

      return new DeserializationConstructorAndReflectiveTypeAdapterFactory(new ConstructorConstructor(),
            serializationPolicy, Excluder.DEFAULT, deserializationPolicy);
   }

   public void testNullWhenPrimitive() {
      assertNull(parameterizedCtorFactory.create(gson, TypeToken.get(int.class)));
   }

   private static class DefaultConstructor {
      int foo;
      int bar;

      private DefaultConstructor() {
      }

      @Override
      public boolean equals(Object obj) {
         DefaultConstructor other = DefaultConstructor.class.cast(obj);
         if (bar != other.bar)
            return false;
         if (foo != other.foo)
            return false;
         return true;
      }

   }

   public void testRejectsIfNoConstuctorMarked() throws IOException {
      TypeAdapter<DefaultConstructor> adapter = parameterizedCtorFactory.create(gson, TypeToken.get(DefaultConstructor.class));
      assertNull(adapter);
   }

   private static class WithDeserializationConstructorButWithoutSerializedName {
      final int foo;

      @Inject
      WithDeserializationConstructorButWithoutSerializedName(int foo) {
         this.foo = foo;
      }
   }

   public void testSerializedNameRequiredOnAllParameters() {
      try {
         parameterizedCtorFactory.create(gson, TypeToken
               .get(WithDeserializationConstructorButWithoutSerializedName.class));
         fail();
      } catch (IllegalArgumentException actual) {
         assertEquals(actual.getMessage(),
               "org.jclouds.json.internal.DeserializationConstructorAndReflectiveTypeAdapterFactoryTest$WithDeserializationConstructorButWithoutSerializedName(int)" +
                     " parameter 0 failed to be named by AnnotationBasedNamingStrategy requiring one of javax.inject.Named");
      }
   }

   private static class DuplicateSerializedNames {
      final int foo;
      final int bar;

      @Inject
      DuplicateSerializedNames(@Named("foo") int foo, @Named("foo") int bar) {
         this.foo = foo;
         this.bar = bar;
      }
   }

   public void testNoDuplicateSerializedNamesRequiredOnAllParameters() {
      try {
         parameterizedCtorFactory.create(gson, TypeToken.get(DuplicateSerializedNames.class));
         fail();
      } catch (IllegalArgumentException actual) {
         assertEquals(actual.getMessage(),
               "org.jclouds.json.internal.DeserializationConstructorAndReflectiveTypeAdapterFactoryTest$DuplicateSerializedNames(int,int)" +
                     " declares multiple JSON parameters named foo");
      }
   }

   private static class ValidatedConstructor {
      final int foo;
      final int bar;

      @Inject
      ValidatedConstructor(@Named("foo") int foo, @Named("bar") int bar) {
         if (foo < 0)
            throw new IllegalArgumentException("negative!");
         this.foo = foo;
         this.bar = bar;
      }

      @Override
      public boolean equals(Object obj) {
         ValidatedConstructor other = ValidatedConstructor.class.cast(obj);
         if (bar != other.bar)
            return false;
         if (foo != other.foo)
            return false;
         return true;
      }

      @Override
      public String toString() { return "ValidatedConstructor[foo=" + foo + ",bar=" + bar + "]"; }
   }

   public void testValidatedConstructor() throws IOException {
      TypeAdapter<ValidatedConstructor> adapter = parameterizedCtorFactory.create(gson, TypeToken
            .get(ValidatedConstructor.class));
      assertEquals(new ValidatedConstructor(0, 1), adapter.fromJson("{\"foo\":0,\"bar\":1}"));
      try {
         adapter.fromJson("{\"foo\":-1,\"bar\":1}");
         fail();
      } catch (IllegalArgumentException expected) {
         assertEquals("negative!", expected.getMessage());
      }
   }

   private static class GenericParamsCopiedIn {
      final List<String> foo;
      final Map<String, String> bar;

      @Inject
      GenericParamsCopiedIn(@Named("foo") List<String> foo, @Named("bar") Map<String, String> bar) {
         this.foo = Lists.newArrayList(foo);
         this.bar = Maps.newHashMap(bar);
      }

   }

   public void testGenericParamsCopiedIn() throws IOException {
      TypeAdapter<GenericParamsCopiedIn> adapter = parameterizedCtorFactory.create(gson, TypeToken
            .get(GenericParamsCopiedIn.class));
      List<String> inputFoo = Lists.newArrayList();
      inputFoo.add("one");
      Map<String, String> inputBar = Maps.newHashMap();
      inputBar.put("2", "two");

      GenericParamsCopiedIn toTest = adapter.fromJson("{ \"foo\":[\"one\"], \"bar\":{ \"2\":\"two\"}}");
      assertEquals(inputFoo, toTest.foo);
      assertNotSame(inputFoo, toTest.foo);
      assertEquals(inputBar, toTest.bar);

   }

   private static class RenamedFields {
      final int foo;
      @Named("_bar")
      final int bar;

      @ConstructorProperties({"foo", "_bar"})
      RenamedFields(int foo, int bar) {
         if (foo < 0)
            throw new IllegalArgumentException("negative!");
         this.foo = foo;
         this.bar = bar;
      }

      @Override
      public boolean equals(Object obj) {
         RenamedFields other = RenamedFields.class.cast(obj);
         if (bar != other.bar)
            return false;
         if (foo != other.foo)
            return false;
         return true;
      }
   }

   public void testRenamedFields() throws IOException {
      TypeAdapter<RenamedFields> adapter = parameterizedCtorFactory.create(gson, TypeToken.get(RenamedFields.class));
      assertEquals(new RenamedFields(0, 1), adapter.fromJson("{\"foo\":0,\"_bar\":1}"));
      assertEquals(adapter.toJson(new RenamedFields(0, 1)), "{\"foo\":0,\"_bar\":1}");
   }

   private static class ComposedObjects {
      final ValidatedConstructor x;
      final ValidatedConstructor y;

      @ConstructorProperties({"x", "y"})
      ComposedObjects(ValidatedConstructor x, ValidatedConstructor y) {
         this.x = checkNotNull(x);
         this.y = checkNotNull(y);
      }

      @Override
      public boolean equals(Object obj) {
         ComposedObjects other = ComposedObjects.class.cast(obj);
         return other != null && Objects.equal(x, other.x) && Objects.equal(y, other.y);
      }
      
      @Override
      public String toString() { return "ComposedObjects[x=" + x.toString() + ";y=" + y.toString() + "]"; }
   }
   
   public void checkSimpleComposedObject() throws IOException  {
      ValidatedConstructor x = new ValidatedConstructor(0,1);
      ValidatedConstructor y = new ValidatedConstructor(1,2);
      TypeAdapter<ComposedObjects> adapter = parameterizedCtorFactory.create(gson, TypeToken.get(ComposedObjects.class));
      assertEquals(new ComposedObjects(x, y), adapter.fromJson("{\"x\":{\"foo\":0,\"bar\":1},\"y\":{\"foo\":1,\"bar\":2}}"));
   }

   public void testEmptyObjectIsNull() throws IOException {
      TypeAdapter<ComposedObjects> adapter = parameterizedCtorFactory.create(gson, TypeToken.get(ComposedObjects.class));
      assertNull(adapter.fromJson("{}"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testPartialObjectStillThrows() throws IOException {
      TypeAdapter<ComposedObjects> adapter = parameterizedCtorFactory.create(gson, TypeToken.get(ComposedObjects.class));
      assertNull(adapter.fromJson("{\"x\":{\"foo\":0,\"bar\":1}}"));
   }

   public void testCanOverrideDefault() throws IOException {
      Gson gson = new GsonBuilder().registerTypeAdapterFactory(parameterizedCtorFactory).create();

      assertEquals(new RenamedFields(0, 1), gson.fromJson("{\"foo\":0,\"_bar\":1}", RenamedFields.class));
      assertEquals(gson.toJson(new RenamedFields(0, 1)), "{\"foo\":0,\"_bar\":1}");
   }
}
