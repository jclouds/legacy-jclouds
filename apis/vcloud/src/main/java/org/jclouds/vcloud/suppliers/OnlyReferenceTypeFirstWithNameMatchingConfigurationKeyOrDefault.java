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
package org.jclouds.vcloud.suppliers;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.NoSuchElementException;

import org.jclouds.config.ValueOfConfigurationKeyOrNull;
import org.jclouds.vcloud.domain.ReferenceType;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
public class OnlyReferenceTypeFirstWithNameMatchingConfigurationKeyOrDefault implements
      Function<Iterable<ReferenceType>, ReferenceType> {

   protected final ValueOfConfigurationKeyOrNull valueOfConfigurationKeyOrNull;
   protected final String configurationKey;
   protected final Predicate<ReferenceType> defaultSelector;

   public OnlyReferenceTypeFirstWithNameMatchingConfigurationKeyOrDefault(
         ValueOfConfigurationKeyOrNull valueOfConfigurationKeyOrNull, String configurationKey,
         Predicate<ReferenceType> defaultSelector) {
      this.configurationKey = checkNotNull(configurationKey, "configurationKey");
      this.valueOfConfigurationKeyOrNull = checkNotNull(valueOfConfigurationKeyOrNull, "valueOfConfigurationKeyOrNull");
      this.defaultSelector = checkNotNull(defaultSelector, "defaultSelector");
   }

   @Override
   public ReferenceType apply(Iterable<ReferenceType> referenceTypes) {
      checkNotNull(referenceTypes, "referenceTypes");
      checkArgument(Iterables.size(referenceTypes) > 0,
            "No referenceTypes corresponding to configuration key %s present", configurationKey);
      if (Iterables.size(referenceTypes) == 1)
         return Iterables.getLast(referenceTypes);
      String namingPattern = valueOfConfigurationKeyOrNull.apply(configurationKey);
      if (namingPattern != null) {
         return findReferenceTypeWithNameMatchingPattern(referenceTypes, namingPattern);
      } else {
         return defaultReferenceType(referenceTypes);
      }
   }

   public ReferenceType defaultReferenceType(Iterable<ReferenceType> referenceTypes) {
      return Iterables.find(referenceTypes, defaultSelector);
   }

   public ReferenceType findReferenceTypeWithNameMatchingPattern(Iterable<ReferenceType> referenceTypes,
         String namingPattern) {
      try {
         return Iterables.find(referenceTypes, new ReferenceTypeNameMatchesPattern(namingPattern));
      } catch (NoSuchElementException e) {
         throw new NoSuchElementException(String.format(
               "referenceType matching pattern [%s], corresponding to configuration key %s, not in %s", namingPattern,
               configurationKey, referenceTypes));
      }
   }

   static class ReferenceTypeNameMatchesPattern implements Predicate<ReferenceType> {

      private final String namingPattern;

      public ReferenceTypeNameMatchesPattern(String namingPattern) {
         this.namingPattern = checkNotNull(namingPattern, "namingPattern");
      }

      @Override
      public boolean apply(ReferenceType arg0) {
         return arg0.getName().matches(namingPattern);
      }

      @Override
      public String toString() {
         return "nameMatchesPattern(" + namingPattern + ")";

      }
   }
}
