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
package org.jclouds.reflect;

import static com.google.common.base.Throwables.propagate;
import static org.testng.Assert.assertEquals;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ExecutionException;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.cache.ForwardingLoadingCache.SimpleForwardingLoadingCache;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.Invokable;
import com.google.common.reflect.TypeToken;

/**
 * Tests {@link Reflection2#method(Class, String, Class...)} where
 * the method overrides one in parent classes and so bridge methods
 * need to be ignored.
 * 
 * This test has been separated out into a separate class as it requires
 * reflection to modify the internal static caches of {@code Reflection2}
 * and needs to perform cleanup to avoid affecting other tests.
 * 
 * @author Andrew Phillips
 */
@Test
public class Reflection2OverriddenMethodTest {
   private LoadingCache<TypeToken<?>, Set<Invokable<?, ?>>> originalMethodsForTypeToken;

   @BeforeClass
   public void backupMethodsForTypeToken() {
      originalMethodsForTypeToken = getStaticField(Reflection2.class, "methodsForTypeToken");
   }
   
   private static class ParentWithMethod {
      @SuppressWarnings("unused")
      public Set<Object> method() {
         return null;
      }
   }

   private static class ChildOverridesAndNarrowsMethod extends ParentWithMethod {
      @Override
      public SortedSet<Object> method() {
         return null;
      }
   }

   public void testOverriddenMethodWithNarrowedReturnType() throws NoSuchMethodException {
      // expecting two methods: the declared "method" and the bridge version with return type Set
      final Method[] methods = ChildOverridesAndNarrowsMethod.class.getDeclaredMethods();
      /*
       * Force Reflection2.methodsForTypeToken to reflect the fact that the declared methods
       * of a class are not returned in any particular order.
       */
      setStaticField(Reflection2.class, "methodsForTypeToken", keyOverridingCache(
            TypeToken.of(ChildOverridesAndNarrowsMethod.class),
            ImmutableSet.<Invokable<?, ?>>of(Invokable.from(methods[0]), Invokable.from(methods[1]))));
      // getMethod returns the method with the *narrowest* return type if one exists
      Invokable<?, Object> mostSpecificMethod = Invokable.from(ChildOverridesAndNarrowsMethod.class.getMethod("method"));
      assertEquals(Reflection2.method(ChildOverridesAndNarrowsMethod.class, "method"), 
            mostSpecificMethod);
      
      // now the opposite order
      Reflection2OverriddenMethodTest.<LoadingCache<?, ?>>
      getStaticField(Reflection2.class, "methodForParams").invalidateAll();
      setStaticField(Reflection2.class, "methodsForTypeToken", keyOverridingCache(
            TypeToken.of(ChildOverridesAndNarrowsMethod.class),
            ImmutableSet.<Invokable<?, ?>>of(Invokable.from(methods[1]), Invokable.from(methods[0]))));
      assertEquals(Reflection2.method(ChildOverridesAndNarrowsMethod.class, "method"), 
            mostSpecificMethod);
   }

   private LoadingCache<TypeToken<?>, Set<Invokable<?, ?>>> keyOverridingCache(
         final TypeToken<?> overriddenKey, final Set<Invokable<?, ?>> value) {
      return new SimpleForwardingLoadingCache<TypeToken<?>, Set<Invokable<?, ?>>>(originalMethodsForTypeToken) {
         @Override
         public Set<Invokable<?, ?>> get(TypeToken<?> key) throws ExecutionException {
            return (key.equals(overriddenKey) ? value : super.get(key));
         }
      };
   }

   @SuppressWarnings("unchecked")
   private static <T> T getStaticField(Class<?> declaringClass, String fieldName) {
      try {
         Field field = declaringClass.getDeclaredField(fieldName);
         field.setAccessible(true);
         // static field
         return (T) field.get(null);
      } catch (NoSuchFieldException exception) {
         throw propagate(exception);
      } catch (IllegalAccessException exception) {
         throw propagate(exception);
      }
   }

   private static void setStaticField(Class<?> declaringClass, String fieldName, Object value) {
      try {
         Field field = declaringClass.getDeclaredField(fieldName);
         field.setAccessible(true);
         // static field
         field.set(null, value);
      } catch (NoSuchFieldException exception) {
         throw propagate(exception);
      } catch (IllegalAccessException exception) {
         throw propagate(exception);
      }
   }

   @AfterClass(alwaysRun = true)
   public void restoreMethodsForTypeToken() {
      setStaticField(Reflection2.class, "methodsForTypeToken", originalMethodsForTypeToken);
   }
}
