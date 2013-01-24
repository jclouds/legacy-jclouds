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

import static com.google.common.base.Functions.toStringFunction;
import static com.google.common.base.Throwables.propagate;
import static java.lang.String.format;
import static org.jclouds.reflect.Reflection2.constructors;
import static org.jclouds.reflect.Reflection2.method;
import static org.jclouds.reflect.Reflection2.methods;
import static org.jclouds.reflect.Reflection2.typeToken;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;

import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.Invokable;
import com.google.common.reflect.Parameter;
import com.google.common.reflect.TypeToken;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
@Test
public class Reflection2Test {

   /**
    * useful when converting to and from type literals from other libraries such as guice and gson.
    */
   public void testTypeTokenForType() {
      TypeLiteral<Set<String>> guice = new TypeLiteral<Set<String>>() {
      };

      assertEquals(typeToken(guice.getType()), new TypeToken<Set<String>>() {
         private static final long serialVersionUID = 1L;
      });
   }

   public void testConstructors() {
      Set<String> ctorParams = FluentIterable.from(constructors(TypeToken.of(HashSet.class)))
            .transform(new Function<Invokable<?, ?>, Iterable<Parameter>>() {
               public Iterable<Parameter> apply(Invokable<?, ?> input) {
                  return input.getParameters();
               }
            }).transform(toStringFunction()).toSet();

      assertEquals(ctorParams, ImmutableSet.of("[]", "[java.util.Collection<? extends E> arg0]",
            "[int arg0, float arg1]", "[int arg0]", "[int arg0, float arg1, boolean arg2]"));
   }

   public void testTypeTokenForClass() {
      assertEquals(typeToken(String.class), TypeToken.of(String.class));
   }

   public void testMethodFromJavaMethod() throws SecurityException, NoSuchMethodException {
      assertEquals(method(typeToken(String.class), String.class.getMethod("toString")), TypeToken.of(String.class)
            .method(String.class.getMethod("toString")).returning(String.class));
   }

   public void testMethodFromClassAndNoParams() {
      @SuppressWarnings("rawtypes")
      Invokable<Set, Object> methodInSuper = method(Set.class, "iterator");

      assertEquals(methodInSuper.getOwnerType(), typeToken(Set.class));
   }

   public void testMethodFromClassAndParams() {
      @SuppressWarnings("rawtypes")
      Invokable<Set, Object> methodInSuper = method(Set.class, "equals", Object.class);

      assertEquals(methodInSuper.getOwnerType(), typeToken(Set.class));
      assertEquals(methodInSuper.getParameters().get(0).getType().getRawType(), Object.class);
   }

   ImmutableSet<String> setMethods = ImmutableSet.of("add", "equals", "hashCode", "clear", "isEmpty", "contains",
         "addAll", "size", "toArray", "iterator", "remove", "removeAll", "containsAll", "retainAll");

   public void testMethods() {
      Set<String> methodNames = FluentIterable.from(methods(Set.class)).transform(invokableToName)
            .transform(toStringFunction()).toSet();

      assertEquals(methodNames, setMethods);
   }

   public void testMethodsSubClass() {
      Set<String> methodNames = FluentIterable.from(methods(SortedSet.class)).transform(invokableToName)
            .transform(toStringFunction()).toSet();

      assertEquals(methodNames,
            ImmutableSet.builder().add("comparator", "last", "first", "subSet", "headSet", "tailSet")
                  .addAll(setMethods).build());
   }

   public void testOverriddenMethodWithNarrowedReturnType() throws NoSuchMethodException {
       LoadingCache<TypeToken<?>, Set<Invokable<?, ?>>> methodsForTypeTokenBackup =
           getStaticField(Reflection2.class, "methodsForTypeToken");
       // expecting two methods: the declared "method" and the bridge version with return type Set
       final Method[] methods = ChildOverridesAndNarrowsMethod.class.getDeclaredMethods();
       try {
           /*
            * Force Reflection2.methodsForTypeToken to reflect the fact that the declared methods
            * of a class are not returned in any particular order.
            */
           setStaticField(Reflection2.class, "methodsForTypeToken", CacheBuilder.newBuilder().build(
                   new CacheLoader<TypeToken<?>, Set<Invokable<?, ?>>>() {
                          @Override
                           public Set<Invokable<?, ?>> load(TypeToken<?> key) throws Exception {
                              if (!key.equals(TypeToken.of(ChildOverridesAndNarrowsMethod.class))) {
                                  fail(format("expected only key %s to be requested, but was %s", 
                                          TypeToken.of(ChildOverridesAndNarrowsMethod.class), key));
                              }
                              return ImmutableSet.<Invokable<?, ?>>of(Invokable.from(methods[0]), Invokable.from(methods[1]));
                           }
                   }));
           // getMethod returns the method with the *narrowest* return type if one exists
           assertEquals(Reflection2.method(ChildOverridesAndNarrowsMethod.class, "method"), 
                   Invokable.from(ChildOverridesAndNarrowsMethod.class.getMethod("method")));

           // now the opposite order
           Reflection2Test.<LoadingCache<?, ?>>
               getStaticField(Reflection2.class, "methodForParams").invalidateAll();
           setStaticField(Reflection2.class, "methodsForTypeToken", CacheBuilder.newBuilder().build(
                   new CacheLoader<TypeToken<?>, Set<Invokable<?, ?>>>() {
                          @Override
                           public Set<Invokable<?, ?>> load(TypeToken<?> key) throws Exception {
                              if (!key.equals(TypeToken.of(ChildOverridesAndNarrowsMethod.class))) {
                                  fail(format("expected only key %s to be requested, but was %s", 
                                          TypeToken.of(ChildOverridesAndNarrowsMethod.class), key));
                              }
                              return ImmutableSet.<Invokable<?, ?>>of(Invokable.from(methods[1]), Invokable.from(methods[0]));
                           }
                   }));
           // getMethod returns the method with the *narrowest* return type if one exists
           assertEquals(Reflection2.method(ChildOverridesAndNarrowsMethod.class, "method"), 
                   Invokable.from(ChildOverridesAndNarrowsMethod.class.getMethod("method")));
       } finally {
           setStaticField(Reflection2.class, "methodsForTypeToken", methodsForTypeTokenBackup);
       }
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

   static final Function<Invokable<?, ?>, String> invokableToName = new Function<Invokable<?, ?>, String>() {
      public String apply(Invokable<?, ?> input) {
         return input.getName();
      }
   };

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
}
