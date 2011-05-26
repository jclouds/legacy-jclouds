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

import com.google.common.base.Preconditions;
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
          * {@inheritDoc}
          */
         @Override
         public boolean apply(ProviderMetadata providerMetadata) {
            return providerMetadata.getId().equals(id);
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
          * {@inheritDoc}
          */
         @Override
         public boolean apply(ProviderMetadata providerMetadata) {
            return providerMetadata.getType().equals(type);
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
    * Returns the providers that are bound to the same location as the given ISO 3166 code.
    * 
    * @param isoCode
    *                the ISO 3166 code to filter providers by
    * 
    * @return the providers with the given ISO 3166 code
    */
   public static Predicate<ProviderMetadata> boundedByIso3166Code(final String iso3166Code) {
      Preconditions.checkNotNull(iso3166Code, "iso3166Code must not be null");

      return new Predicate<ProviderMetadata>() {
         /**
          * {@inheritDoc}
          */
         @Override
         public boolean apply(ProviderMetadata providerMetadata) {
            return providerContainsIso3166Code(providerMetadata, iso3166Code);
         }

         /**
          * {@inheritDoc}
          */
         @Override
         public String toString() {
            return "boundedByIso3166Code(" + iso3166Code + ")";
         }
      };
   }

   /**
    * Return all providers that have at least one ISO 3166 code in common with the given provider metadata.
    * 
    * @param refProviderMetadata
    *                            the provider metadata to use to filter providers by
    * 
    * @return the providers that have at least one ISO 3166 code in common
    */
   public static Predicate<ProviderMetadata> intersectingIso3166Code(final ProviderMetadata refProviderMetadata) {
      Preconditions.checkNotNull(refProviderMetadata, "refProviderMetadata must not be null");

      return new Predicate<ProviderMetadata>() {
         /**
          * {@inheritDoc}
          */
         @Override
         public boolean apply(ProviderMetadata providerMetadata) {
            for (String refIso3166Code : refProviderMetadata.getIso3166Codes()) {
               // Return only if the potential provider contains the same ISO 3166 code and the provider and
               // reference provider are not the same.
               if (providerContainsIso3166Code(providerMetadata, refIso3166Code) &&
                     !refProviderMetadata.equals(providerMetadata)) {
                  return true;
               }
            }
            return false;
         }

         /**
          * {@inheritDoc}
          */
         @Override
         public String toString() {
            return "intersectingIso3166Code(" + refProviderMetadata + ")";
         }
      };
   }

   /**
    * Returns whether or not the provided provider contains the ISO 3166 code provider or is within the same
    * "global" region, like "US" would contain "US-*".
    * 
    * @param providerMetadata
    *                         the provider metadata to search
    * @param iso3166Code
    *                    the ISO 3166 code to search the provider metadata for
    * 
    * @return the result
    */
   private static boolean providerContainsIso3166Code(ProviderMetadata providerMetadata, String iso3166Code) {
      for (String availCode : providerMetadata.getIso3166Codes()) {
         if(iso3166Code.indexOf('-') == -1) {
            if (availCode.startsWith(iso3166Code + "-")) {
               return true;
            }
         } else if (availCode.equals(iso3166Code)) {
            return true;
         }
      }

      return false;
   }
}