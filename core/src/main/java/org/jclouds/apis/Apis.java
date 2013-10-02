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
package org.jclouds.apis;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.find;
import static org.jclouds.reflect.Reflection2.typeToken;

import java.util.NoSuchElementException;
import java.util.ServiceLoader;

import org.jclouds.View;
import org.jclouds.osgi.ApiRegistry;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.reflect.TypeToken;

/**
 * The Apis class provides static methods for accessing apis.
 * 
 * @author Jeremy Whitlock <jwhitlock@apache.org>
 */
public class Apis {

   private static enum IdFunction implements Function<ApiMetadata, String> {
      INSTANCE;

      @Override
      public String apply(ApiMetadata input) {
         return input.getId();
      }

   }

   public static Function<ApiMetadata, String> idFunction() {
      return IdFunction.INSTANCE;
   }

   /**
    * Returns the apis located on the classpath via {@link java.util.ServiceLoader}.
    * 
    * @return all available apis loaded from classpath via ServiceLoader
    */
   private static Iterable<ApiMetadata> fromServiceLoader() {
      return ServiceLoader.load(ApiMetadata.class);
   }

   /**
    * Returns all available apis.
    * 
    * @return all available apis
    */
   public static Iterable<ApiMetadata> all() {
      return ImmutableSet.<ApiMetadata>builder()
                         .addAll(fromServiceLoader())
                         .addAll(ApiRegistry.fromRegistry()).build();
   }

   /**
    * Returns the first api with the provided id
    * 
    * @param id
    *           the id of the api to return
    * 
    * @return the api with the given id
    * 
    * @throws NoSuchElementException
    *            whenever there are no apis with the provided id
    */
   public static ApiMetadata withId(String id) throws NoSuchElementException {
      return find(all(), ApiPredicates.id(id));
   }
   
   /**
    * Returns all apis who's contexts are assignable from the parameter
    * 
    * @param type
    *           the type of the context to search for
    * 
    * @return the apis with contexts assignable from given type
    */
   public static Iterable<ApiMetadata> contextAssignableFrom(TypeToken<?> type) {
      return filter(all(), ApiPredicates.contextAssignableFrom(type));
   }
   
   /**
    * Returns all apis who's contexts are assignable from the parameter
    * 
    * @param type
    *           the type of the context to search for
    * 
    * @return the apis with contexts assignable from given type
    */
   public static Iterable<ApiMetadata> viewableAs(TypeToken<? extends View> type) {
      return filter(all(), ApiPredicates.viewableAs(type));
   }
   
   public static Iterable<ApiMetadata> viewableAs(Class<? extends View> type) {
      return filter(all(), ApiPredicates.viewableAs(typeToken(type)));
   }

   /**
    * Returns the type of context
    * 
    * @param type
    *           the type of the context to search for
    * 
    * @return the apis with contexts transformable to the given type
    */
   public static TypeToken<?> findView(final ApiMetadata apiMetadata, final TypeToken<?> view)
            throws NoSuchElementException {
      checkNotNull(apiMetadata, "apiMetadata must be defined");
      checkNotNull(view, "context must be defined");
      return Iterables.find(apiMetadata.getViews(), new Predicate<TypeToken<?>>() {

         @Override
         public boolean apply(TypeToken<?> input) {
            return view.isAssignableFrom(input);
         }

      });
   }
}
