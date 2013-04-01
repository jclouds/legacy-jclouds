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
import static org.jclouds.reflect.Reflection2.constructors;
import static org.jclouds.reflect.Reflection2.isGetter;
import static org.jclouds.reflect.Reflection2.method;
import static org.jclouds.reflect.Reflection2.methods;
import static org.jclouds.reflect.Reflection2.methodsAnnotatedWith;
import static org.jclouds.reflect.Reflection2.typeParameterOf;
import static org.jclouds.reflect.Reflection2.typeToken;
import static org.jclouds.reflect.Reflection2.getPropertyGetter;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import org.jclouds.domain.Location;
import org.jclouds.management.annotations.ManagedAttribute;
import org.testng.annotations.Test;

import com.google.common.base.Function;
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

   public void testIsGetter() {
      assertTrue(isGetter(method(Location.class, "getId")));
      assertTrue(isGetter(method(Location.class, "getScope")));
      assertTrue(isGetter(method(Location.class, "getParent")));
   }

   public void testPropertyGetter() {
      assertTrue(getPropertyGetter(Location.class, "id").isPresent());
      assertTrue(getPropertyGetter(Location.class, "scope").isPresent());
      assertTrue(getPropertyGetter(Location.class, "parent").isPresent());
      assertFalse(getPropertyGetter(Location.class,"notfound").isPresent());
   }

   public void testMethodsAnnotatedWith() {
      assertFalse(Iterables.isEmpty(methodsAnnotatedWith(Location.class, ManagedAttribute.class)));
   }

   public void testTypeParameterOf() {
      assertEquals(String.class, typeParameterOf(new TypeToken<Iterable<String>>(){}.getType()));
      assertEquals(String.class, typeParameterOf(new TypeToken<List<String>>(){}.getType()));
      assertEquals(String.class, typeParameterOf(new TypeToken<Optional<String>>(){}.getType()));
   }

   static final Function<Invokable<?, ?>, String> invokableToName = new Function<Invokable<?, ?>, String>() {
      public String apply(Invokable<?, ?> input) {
         return input.getName();
      }
   };
}
