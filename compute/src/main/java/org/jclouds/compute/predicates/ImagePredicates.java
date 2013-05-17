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
package org.jclouds.compute.predicates;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import org.jclouds.compute.domain.Image;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Sets;

/**
 * Container for image filters (predicates).
 * 
 * This class has static methods that create customized predicates to use with
 * {@link org.jclouds.compute.ComputeService}.
 * 
 * @author Adrian Cole
 */
public class ImagePredicates {
   private static final class Is64BitPredicate implements Predicate<Image> {
      @Override
      public boolean apply(Image image) {
         return image.getOperatingSystem().is64Bit();
      }

      @Override
      public String toString() {
         return "is64Bit()";
      }

      @Override
      public boolean equals(@Nullable Object obj) {
         return obj instanceof Is64BitPredicate;
      }

   }

   /**
    * evaluates true if the Image id is in the supplied set
    * 
    * @param ids
    *           ids of the images
    * @return predicate
    */
   public static Predicate<Image> idIn(Iterable<String> ids) {
      checkNotNull(ids, "ids must be defined");
      final Set<String> search = Sets.newHashSet(ids);
      return new Predicate<Image>() {
         @Override
         public boolean apply(Image image) {
            return search.contains(image.getId());
         }

         @Override
         public String toString() {
            return "idIn(" + search + ")";
         }
      };
   }
   
   /**
    * evaluates true if the Image metadata contains the following values
    * 
    * @param key
    *           key in Image#getUserMetadata
    * @param value
    *           value in Image#getUserMetadata
     * @return predicate
    */
   public static Predicate<Image> userMetadataContains(final String key, final String value) {
      checkNotNull(key, "key must be defined");
      checkNotNull(value, "value must be defined");
      return new Predicate<Image>() {
         @Override
         public boolean apply(Image image) {
            return value.equals(image.getUserMetadata().get(key));
         }

         @Override
         public String toString() {
            return "metadataContains(" + key +", "+value + ")";
         }
      };
   }

   /**
    * evaluates true if the Image
    * 
    * @param ids
    *           ids of the images
    * @return predicate
    */
   public static Predicate<Image> idEquals(final String id) {
      checkNotNull(id, "id must be defined");
      return new Predicate<Image>() {
         @Override
         public boolean apply(Image image) {
            return id.equals(image.getId());
         }

         @Override
         public String toString() {
            return "idEquals(" + id + ")";
         }
      };
   }

   /**
    * return true if this is a 64bit image.
    */
   public static Predicate<Image> is64Bit() {
      return new Is64BitPredicate();
   }

   /**
    * return everything.
    */
   public static Predicate<Image> any() {
      return Predicates.<Image> alwaysTrue();
   }

}
