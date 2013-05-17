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
package org.jclouds.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;

/**
 * finds all the named string bindings who's name annotation matches the filter.
 * for convenience, the bindings are returned as a map.
 * 
 * @author Adrian Cole
 */
@Singleton
public class FilterStringsBoundToInjectorByName implements Function<Predicate<String>, Map<String, String>> {

   private final Injector injector;

   @Inject
   public FilterStringsBoundToInjectorByName(Injector injector) {
      this.injector = checkNotNull(injector, "injector");
   }

   @Override
   public Map<String, String> apply(Predicate<String> filter) {
      List<Binding<String>> stringBindings = injector.findBindingsByType(TypeLiteral.get(String.class));
      Iterable<Binding<String>> annotatedWithName = Iterables.filter(stringBindings, new Predicate<Binding<String>>() {

         @Override
         public boolean apply(Binding<String> input) {
            Annotation annotation = input.getKey().getAnnotation();
            if (annotation == null)
               return false;
            return (annotation instanceof javax.inject.Named) || (annotation instanceof com.google.inject.name.Named);
         }

      });

      Map<String, Binding<String>> bindingsByName = Maps.uniqueIndex(annotatedWithName,
            new Function<Binding<String>, String>() {

               @Override
               public String apply(Binding<String> input) {
                  Annotation annotation = input.getKey().getAnnotation();
                  return (annotation instanceof javax.inject.Named) ? javax.inject.Named.class.cast(annotation)
                        .value() : com.google.inject.name.Named.class.cast(annotation).value();
               }

            });

      Map<String, Binding<String>> filteredBindingsByName = Maps.filterKeys(bindingsByName, filter);

      Map<String, String> stringBoundByName = Maps.transformValues(filteredBindingsByName,
            new Function<Binding<String>, String>() {

               @Override
               public String apply(Binding<String> input) {
                  return input.getProvider().get();
               }
            });
      return stringBoundByName;
   }

}
