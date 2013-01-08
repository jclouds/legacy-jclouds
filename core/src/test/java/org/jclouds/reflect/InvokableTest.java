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
package org.jclouds.reflect;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.util.Collections;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.reflect.TypeToken;

/**
 * ported from {@link com.google.common.reflect.InvokableTest}
 * 
 */
@Test
@SuppressWarnings("serial")
public class InvokableTest {

   public void testConstructor_returnType() throws Exception {
      assertEquals(Prepender.class, Prepender.constructor().getReturnType().getType());
   }

   public void testConstructor_exceptionTypes() throws Exception {
      assertEquals(ImmutableList.of(TypeToken.of(NullPointerException.class)),
            Prepender.constructor(String.class, int.class).getExceptionTypes());
   }

   public void testConstructor_typeParameters() throws Exception {
      TypeVariable<?>[] variables = Prepender.constructor().getTypeParameters();
      assertEquals(1, variables.length);
      assertEquals("A", variables[0].getName());
   }

   public void testConstructor_parameters() throws Exception {
      Invokable<?, Prepender> delegate = Prepender.constructor(String.class, int.class);
      ImmutableList<Parameter> parameters = delegate.getParameters();
      assertEquals(2, parameters.size());
      assertEquals(String.class, parameters.get(0).getType().getType());
      assertTrue(parameters.get(0).isAnnotationPresent(NotBlank.class));
      assertEquals(int.class, parameters.get(1).getType().getType());
      assertFalse(parameters.get(1).isAnnotationPresent(NotBlank.class));
   }

   public void testConstructor_call() throws Exception {
      Invokable<?, Prepender> delegate = Prepender.constructor(String.class, int.class);
      Prepender prepender = delegate.invoke(null, "a", 1);
      assertEquals("a", prepender.prefix);
      assertEquals(1, prepender.times);
   }

   public void testConstructor_returning() throws Exception {
      Invokable<?, Prepender> delegate = Prepender.constructor(String.class, int.class).returning(Prepender.class);
      Prepender prepender = delegate.invoke(null, "a", 1);
      assertEquals("a", prepender.prefix);
      assertEquals(1, prepender.times);
   }

   public void testConstructor_invalidReturning() throws Exception {
      Invokable<?, Prepender> delegate = Prepender.constructor(String.class, int.class);
      try {
         delegate.returning(SubPrepender.class);
         fail();
      } catch (IllegalArgumentException expected) {
      }
   }

   public void testStaticMethod_returnType() throws Exception {
      Invokable<?, ?> delegate = Prepender.method("prepend", String.class, Iterable.class);
      assertEquals(new TypeToken<Iterable<String>>() {
      }, delegate.getReturnType());
   }

   public void testStaticMethod_exceptionTypes() throws Exception {
      Invokable<?, ?> delegate = Prepender.method("prepend", String.class, Iterable.class);
      assertEquals(ImmutableList.of(), delegate.getExceptionTypes());
   }

   public void testStaticMethod_typeParameters() throws Exception {
      Invokable<?, ?> delegate = Prepender.method("prepend", String.class, Iterable.class);
      TypeVariable<?>[] variables = delegate.getTypeParameters();
      assertEquals(1, variables.length);
      assertEquals("T", variables[0].getName());
   }

   public void testStaticMethod_parameters() throws Exception {
      Invokable<?, ?> delegate = Prepender.method("prepend", String.class, Iterable.class);
      ImmutableList<Parameter> parameters = delegate.getParameters();
      assertEquals(2, parameters.size());
      assertEquals(String.class, parameters.get(0).getType().getType());
      assertTrue(parameters.get(0).isAnnotationPresent(NotBlank.class));
      assertEquals(new TypeToken<Iterable<String>>() {
      }, parameters.get(1).getType());
      assertFalse(parameters.get(1).isAnnotationPresent(NotBlank.class));
   }

   public void testStaticMethod_call() throws Exception {
      Invokable<?, ?> delegate = Prepender.method("prepend", String.class, Iterable.class);
      @SuppressWarnings("unchecked")
      // prepend() returns Iterable<String>
      Iterable<String> result = (Iterable<String>) delegate.invoke(null, "a", ImmutableList.of("b", "c"));
      assertEquals(ImmutableList.of("a", "b", "c"), ImmutableList.copyOf(result));
   }

   public void testStaticMethod_returning() throws Exception {
      Invokable<?, Iterable<String>> delegate = Prepender.method("prepend", String.class, Iterable.class).returning(
            new TypeToken<Iterable<String>>() {
            });
      assertEquals(new TypeToken<Iterable<String>>() {
      }, delegate.getReturnType());
      Iterable<String> result = delegate.invoke(null, "a", ImmutableList.of("b", "c"));
      assertEquals(ImmutableList.of("a", "b", "c"), ImmutableList.copyOf(result));
   }

   public void testStaticMethod_returningRawType() throws Exception {
      @SuppressWarnings("rawtypes")
      // the purpose is to test raw type
      Invokable<?, Iterable> delegate = Prepender.method("prepend", String.class, Iterable.class).returning(
            Iterable.class);
      assertEquals(new TypeToken<Iterable<String>>() {
      }, delegate.getReturnType());
      @SuppressWarnings("unchecked")
      // prepend() returns Iterable<String>
      Iterable<String> result = delegate.invoke(null, "a", ImmutableList.of("b", "c"));
      assertEquals(ImmutableList.of("a", "b", "c"), ImmutableList.copyOf(result));
   }

   public void testStaticMethod_invalidReturning() throws Exception {
      Invokable<?, Object> delegate = Prepender.method("prepend", String.class, Iterable.class);
      try {
         delegate.returning(new TypeToken<Iterable<Integer>>() {
         });
         fail();
      } catch (IllegalArgumentException expected) {
      }
   }

   public void testInstanceMethod_returnType() throws Exception {
      Invokable<?, ?> delegate = Prepender.method("prepend", Iterable.class);
      assertEquals(new TypeToken<Iterable<String>>() {
      }, delegate.getReturnType());
   }

   public void testInstanceMethod_exceptionTypes() throws Exception {
      Invokable<?, ?> delegate = Prepender.method("prepend", Iterable.class);
      assertEquals(
            ImmutableList.of(TypeToken.of(IllegalArgumentException.class), TypeToken.of(NullPointerException.class)),
            delegate.getExceptionTypes());
   }

   public void testInstanceMethod_typeParameters() throws Exception {
      Invokable<?, ?> delegate = Prepender.method("prepend", Iterable.class);
      assertEquals(0, delegate.getTypeParameters().length);
   }

   public void testInstanceMethod_parameters() throws Exception {
      Invokable<?, ?> delegate = Prepender.method("prepend", Iterable.class);
      ImmutableList<Parameter> parameters = delegate.getParameters();
      assertEquals(1, parameters.size());
      assertEquals(new TypeToken<Iterable<String>>() {
      }, parameters.get(0).getType());
      assertEquals(0, parameters.get(0).getAnnotations().length);
   }

   public void testInstanceMethod_call() throws Exception {
      Invokable<Prepender, ?> delegate = Prepender.method("prepend", Iterable.class);
      @SuppressWarnings("unchecked")
      // prepend() returns Iterable<String>
      Iterable<String> result = (Iterable<String>) delegate.invoke(new Prepender("a", 2), ImmutableList.of("b", "c"));
      assertEquals(ImmutableList.of("a", "a", "b", "c"), ImmutableList.copyOf(result));
   }

   public void testInstanceMethod_returning() throws Exception {
      Invokable<Prepender, Iterable<String>> delegate = Prepender.method("prepend", Iterable.class).returning(
            new TypeToken<Iterable<String>>() {
            });
      assertEquals(new TypeToken<Iterable<String>>() {
      }, delegate.getReturnType());
      Iterable<String> result = delegate.invoke(new Prepender("a", 2), ImmutableList.of("b", "c"));
      assertEquals(ImmutableList.of("a", "a", "b", "c"), ImmutableList.copyOf(result));
   }

   public void testInstanceMethod_returningRawType() throws Exception {
      @SuppressWarnings("rawtypes")
      // the purpose is to test raw type
      Invokable<Prepender, Iterable> delegate = Prepender.method("prepend", Iterable.class).returning(Iterable.class);
      assertEquals(new TypeToken<Iterable<String>>() {
      }, delegate.getReturnType());
      @SuppressWarnings("unchecked")
      // prepend() returns Iterable<String>
      Iterable<String> result = delegate.invoke(new Prepender("a", 2), ImmutableList.of("b", "c"));
      assertEquals(ImmutableList.of("a", "a", "b", "c"), ImmutableList.copyOf(result));
   }

   public void testInstanceMethod_invalidReturning() throws Exception {
      Invokable<?, Object> delegate = Prepender.method("prepend", Iterable.class);
      try {
         delegate.returning(new TypeToken<Iterable<Integer>>() {
         });
         fail();
      } catch (IllegalArgumentException expected) {
      }
   }

   public void testPrivateInstanceMethod_isOverridable() throws Exception {
      Invokable<?, ?> delegate = Prepender.method("privateMethod");
      assertTrue(delegate.isPrivate());
      assertFalse(delegate.isOverridable());
   }

   public void testPrivateFinalInstanceMethod_isOverridable() throws Exception {
      Invokable<?, ?> delegate = Prepender.method("privateFinalMethod");
      assertTrue(delegate.isPrivate());
      assertTrue(delegate.isFinal());
      assertFalse(delegate.isOverridable());
   }

   public void testStaticMethod_isOverridable() throws Exception {
      Invokable<?, ?> delegate = Prepender.method("staticMethod");
      assertTrue(delegate.isStatic());
      assertFalse(delegate.isOverridable());
   }

   public void testStaticFinalMethod_isFinal() throws Exception {
      Invokable<?, ?> delegate = Prepender.method("staticFinalMethod");
      assertTrue(delegate.isStatic());
      assertTrue(delegate.isFinal());
      assertFalse(delegate.isOverridable());
   }

   static class Foo {
   }

   public void testConstructor_isOverridablel() throws Exception {
      Invokable<?, ?> delegate = Invokable.from(Foo.class.getDeclaredConstructor());
      assertFalse(delegate.isOverridable());
   }

   private static final class FinalClass {
      @SuppressWarnings("unused")
      // used by reflection
      void notFinalMethod() {
      }
   }

   public void testNonFinalMethodInFinalClass_isOverridable() throws Exception {
      Invokable<?, ?> delegate = Invokable.from(FinalClass.class.getDeclaredMethod("notFinalMethod"));
      assertFalse(delegate.isOverridable());
   }

   @Retention(RetentionPolicy.RUNTIME)
   private @interface NotBlank {
   }

   /** Class for testing construcrtor, static method and instance method. */
   @SuppressWarnings("unused")
   // most are called by reflection
   private static class Prepender {

      private final String prefix;
      private final int times;

      Prepender(@NotBlank String prefix, int times) throws NullPointerException {
         this.prefix = prefix;
         this.times = times;
      }

      // just for testing
      private <A> Prepender() {
         this(null, 0);
      }

      static <T> Iterable<String> prepend(@NotBlank String first, Iterable<String> tail) {
         return Iterables.concat(ImmutableList.of(first), tail);
      }

      Iterable<String> prepend(Iterable<String> tail) throws IllegalArgumentException, NullPointerException {
         return Iterables.concat(Collections.nCopies(times, prefix), tail);
      }

      static Invokable<?, Prepender> constructor(Class<?>... parameterTypes) throws Exception {
         Constructor<Prepender> constructor = Prepender.class.getDeclaredConstructor(parameterTypes);
         return Invokable.from(constructor);
      }

      static Invokable<Prepender, Object> method(String name, Class<?>... parameterTypes) {
         try {
            Method method = Prepender.class.getDeclaredMethod(name, parameterTypes);
            @SuppressWarnings("unchecked")
            // The method is from Prepender.
            Invokable<Prepender, Object> invokable = (Invokable<Prepender, Object>) Invokable.from(method);
            return invokable;
         } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
         }
      }

      private void privateMethod() {
      }

      private final void privateFinalMethod() {
      }

      static void staticMethod() {
      }

      static final void staticFinalMethod() {
      }
   }

   private static class SubPrepender extends Prepender {
      @SuppressWarnings("unused")
      // needed to satisfy compiler, never called
      public SubPrepender() throws NullPointerException {
         throw new AssertionError();
      }
   }
}
