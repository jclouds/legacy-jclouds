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
import static org.jclouds.reflect.Reflection2.method;
import static org.jclouds.reflect.Reflection2.methods;
import static org.jclouds.reflect.Reflection2.typeToken;
import static org.testng.Assert.assertEquals;

import java.util.Set;

import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.Invokable;
import com.google.common.reflect.TypeToken;

/**
 * 
 * @author Adrian Cole
 */
@Test
public class Reflection2Test {

   public void testTypeToken() {
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

   public void testMethods() {
      Set<String> methodNames = FluentIterable.from(methods(Set.class))
            .transform(new Function<Invokable<?, ?>, String>() {
               public String apply(Invokable<?, ?> input) {
                  return input.getName();
               }
            }).transform(toStringFunction()).toSet();

      assertEquals(methodNames, ImmutableSet.of("add", "equals", "hashCode", "clear", "isEmpty", "contains", "addAll",
            "size", "toArray", "iterator", "remove", "removeAll", "containsAll", "retainAll"));
   }
}
