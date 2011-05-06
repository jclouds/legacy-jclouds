/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.providers;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import org.jclouds.util.Preconditions2;

/**
 * Container for provider filters (predicates).
 *
 * @author Jeremy Whitlock <jwhitlock@apache.org>
 */
public class ProviderPredicates {

   /**
    * Returns all providers available to jclouds regardless of type.
    *
    * @return all available providers
    */
   public static Predicate<ProviderMetadata> all() {
      return Predicates.<ProviderMetadata> alwaysTrue();
   }

   /**
    * Returns all providers with the given id.
    *
    * @param id
    *           the id of the provider to return
    *
    * @return the providers with the given id
    */
   public static Predicate<ProviderMetadata> id(final String id) {
      Preconditions2.checkNotEmpty(id, "id must be defined");
      return new Predicate<ProviderMetadata>() {
         /**
          * {@see com.google.common.base.Predicate#apply(T)
          */
         @Override
         public boolean apply(ProviderMetadata providerMetadata) {
            return providerMetadata.getId().equals(id);
         }

         /**
          * {@see java.lang.Object#toString()
          */
         @Override
         public String toString() {
            return "id(" + id + ")";
         }
      };
   }

   /**
    * Returns all providers with the given type.
    *
    * @param type
    *           the type of the provider to return
    *
    * @return the providers with the given type
    */
   public static Predicate<ProviderMetadata> type(final String type) {
      Preconditions2.checkNotEmpty(type, "type must be defined");
      return new Predicate<ProviderMetadata>() {
         /**
          * {@see com.google.common.base.Predicate#apply(T)
          */
         @Override
         public boolean apply(ProviderMetadata providerMetadata) {
            return providerMetadata.getType().equals(type);
         }

         /**
          * {@see java.lang.Object#toString()
          */
         @Override
         public String toString() {
            return "type(" + type + ")";
         }
      };
   }

}