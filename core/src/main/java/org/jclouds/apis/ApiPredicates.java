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
package org.jclouds.apis;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.util.Preconditions2;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.reflect.TypeToken;

/**
 * Container for api filters (predicates).
 *
 * @author Jeremy Whitlock <jwhitlock@apache.org>
 */
public class ApiPredicates {

   /**
    * Returns all apis available to jclouds regardless of type.
    *
    * @return all available apis
    */
   public static Predicate<ApiMetadata<?, ?, ?, ?>> all() {
      return Predicates.<ApiMetadata<?, ?, ?, ?>> alwaysTrue();
   }

   /**
    * Returns all apis with the given id.
    *
    * @param id
    *           the id of the api to return
    *
    * @return the apis with the given id
    */
   public static Predicate<ApiMetadata<?, ?, ?, ?>> id(final String id) {
      Preconditions2.checkNotEmpty(id, "id must be defined");
      return new Predicate<ApiMetadata<?, ?, ?, ?>>() {
         /**
          * {@inheritDoc}
          */
         @Override
         public boolean apply(ApiMetadata<?, ?, ?, ?> apiMetadata) {
            return apiMetadata.getId().equals(id);
         }

         /**
          * {@inheritDoc}
          */
         @Override
         public String toString() {
            return "id(" + id + ")";
         }
      };
   }

   /**
    * Returns all apis with the given type.
    *
    * @param type
    *           the type of the api to return
    *
    * @return the apis with the given type
    */
   public static Predicate<ApiMetadata<?, ?, ?, ?>> type(final ApiType type) {
      checkNotNull(type, "type must be defined");
      return new Predicate<ApiMetadata<?, ?, ?, ?>>() {
         /**
          * {@inheritDoc}
          */
         @Override
         public boolean apply(ApiMetadata<?, ?, ?, ?> apiMetadata) {
            return apiMetadata.getType().equals(type);
         }

         /**
          * {@inheritDoc}
          */
         @Override
         public String toString() {
            return "type(" + type + ")";
         }
      };
   }

   /**
    * Returns all apis with the given type.
    *
    * @param type
    *           the type of the api to return
    *
    * @return the apis with the given type
    */
   public static <S> Predicate<ApiMetadata<S, ?, ?, ?>> apiAssignableFrom(final TypeToken<S> type) {
      checkNotNull(type, "type must be defined");
      return new Predicate<ApiMetadata<S, ?, ?, ?>>() {
         /**
          * {@inheritDoc}
          */
         @Override
         public boolean apply(ApiMetadata<S, ?, ?, ?> apiMetadata) {
            return type.isAssignableFrom(apiMetadata.getApi());
         }

         /**
          * {@inheritDoc}
          */
         @Override
         public String toString() {
            return "contextAssignableFrom(" + type + ")";
         }
      };
   }

   /**
    * Returns all apis who's contexts are assignable from the parameter
    *
    * @param type
    *           the type of the context to search for
    *           
    * @return the apis with contexts assignable from given type
    */
   public static Predicate<ApiMetadata<?, ?, ?, ?>> contextAssignableFrom(final TypeToken<?> contextType) {
      checkNotNull(contextType, "context must be defined");
      return new Predicate<ApiMetadata<?, ?, ?, ?>>() {
         /**
          * {@inheritDoc}
          */
         @Override
         public boolean apply(ApiMetadata<?, ?, ?, ?> apiMetadata) {
            return contextType.isAssignableFrom(apiMetadata.getContext().getRawType());
         }

         /**
          * {@inheritDoc}
          */
         @Override
         public String toString() {
            return "contextAssignableFrom(" + contextType + ")";
         }
      };
   }
}