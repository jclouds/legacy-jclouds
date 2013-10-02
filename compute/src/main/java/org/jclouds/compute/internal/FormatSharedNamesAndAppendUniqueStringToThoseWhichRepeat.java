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
package org.jclouds.compute.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.config.ComputeServiceProperties.RESOURCENAME_DELIMITER;
import static org.jclouds.compute.config.ComputeServiceProperties.RESOURCENAME_PREFIX;

import java.security.SecureRandom;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.predicates.Validator;
import org.jclouds.predicates.validators.DnsNameValidator;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.Inject;

/**
 * Get a name using a random mechanism that still ties all nodes in a group
 * together.
 * 
 * This implementation will pass the group and a hex formatted random number to
 * the configured naming convention.
 * 
 */
public class FormatSharedNamesAndAppendUniqueStringToThoseWhichRepeat implements GroupNamingConvention {

   protected final String prefix;
   protected final char delimiter;
   protected final Supplier<String> suffixSupplier;
   protected final String sharedFormat;
   protected final String uniqueFormat;
   protected final Pattern uniqueGroupPattern;
   protected final Pattern sharedGroupPattern;
   protected final Validator<String> groupValidator;

   @Singleton
   public static class Factory implements GroupNamingConvention.Factory {
      @Inject(optional = true)
      @Named(RESOURCENAME_PREFIX)
      private String prefix = "jclouds";
      @Inject(optional = true)
      @Named(RESOURCENAME_DELIMITER)
      private char delimiter = '-';
      @Inject(optional = true)
      private Supplier<String> suffixSupplier = new Supplier<String>() {
         final SecureRandom random = new SecureRandom();

         @Override
         public String get() {
            return Integer.toHexString(random.nextInt(4095));
         }
      };

      @Inject(optional = true)
      private Validator<String> groupValidator = new DnsNameValidator(3, 63);

      // lazy init, so that @Inject stuff can work, and avoid calling the
      // constructor
      // each time, as it compiles new regexes
      LoadingCache<String, GroupNamingConvention> cache = CacheBuilder.newBuilder().build(
            new CacheLoader<String, GroupNamingConvention>() {

               @Override
               public GroupNamingConvention load(String key) throws Exception {
                  return new FormatSharedNamesAndAppendUniqueStringToThoseWhichRepeat(key, delimiter, suffixSupplier, groupValidator);
               }

            });

      @Override
      public GroupNamingConvention create() {
         return cache.getUnchecked(prefix);
      }

      @Override
      public GroupNamingConvention createWithoutPrefix() {
         return cache.getUnchecked("");
      }
   }

   public FormatSharedNamesAndAppendUniqueStringToThoseWhichRepeat(String prefix, char delimiter,
         Supplier<String> suffixSupplier, Validator<String> groupValidator) {
      this.prefix = checkNotNull(prefix, "prefix");
      this.delimiter = delimiter;
      this.suffixSupplier = checkNotNull(suffixSupplier, "suffixSupplier");
      this.groupValidator = checkNotNull(groupValidator, "groupValidator");
      this.sharedFormat = "".equals(prefix) ? "%s" : prefix + delimiter + "%s";
      this.uniqueFormat = sharedFormat + delimiter + "%s";
      this.uniqueGroupPattern = Pattern.compile("^" + ("".equals(prefix) ? "" : (prefix + delimiter)) + "(.+)"
            + delimiter + "[^" + delimiter + "]+");
      this.sharedGroupPattern = Pattern.compile("^" + ("".equals(prefix) ? "" : (prefix + delimiter)) + "(.+)$");
   }

   @Override
   public String sharedNameForGroup(String group) {
      return String.format(sharedFormat, checkGroup(group));
   }

   protected String checkGroup(String group) {
      groupValidator.validate(checkNotNull(group, "group"));
      return group;
   }

   @Override
   public String uniqueNameForGroup(String group) {
      return String.format(uniqueFormat, checkGroup(group), suffixSupplier.get());
   }

   @Override
   public String groupInUniqueNameOrNull(String encoded) {
      return firstGroupInPatternOrNull(uniqueGroupPattern, encoded);
   }

   protected String firstGroupInPatternOrNull(Pattern pattern, String encoded) {
      Matcher matcher = pattern.matcher(checkNotNull(encoded, "encoded"));
      if (!matcher.matches())
         return null;
      return matcher.group(1);
   }

   @Override
   public String groupInSharedNameOrNull(String encoded) {
      return firstGroupInPatternOrNull(sharedGroupPattern, encoded);
   }

   @Override
   public Predicate<String> containsGroup(final String group) {
      checkGroup(group);
      return new Predicate<String>() {

         @Override
         public boolean apply(String input) {
            try {
               return group.equals(groupInUniqueNameOrNull(input)) || group.equals(groupInSharedNameOrNull(input));
            } catch (NoSuchElementException e) {
               return false;
            }
         }

         @Override
         public String toString() {
            return "containsGroup(" + group + ")";
         }
      };
   }

   @Override
   public Predicate<String> containsAnyGroup() {
      return new Predicate<String>() {

         @Override
         public boolean apply(String input) {
            try {
               return groupInUniqueNameOrNull(input) != null || groupInSharedNameOrNull(input) != null;
            } catch (NoSuchElementException e) {
               return false;
            }
         }

         @Override
         public String toString() {
            return "containsAnyGroup()";
         }
      };
   }

   @Override
   public String extractGroup(String encoded) {
      String result = groupInUniqueNameOrNull(encoded);
      if (result != null) return result;
      
      result = groupInSharedNameOrNull(encoded);
      return result;
   }
}
