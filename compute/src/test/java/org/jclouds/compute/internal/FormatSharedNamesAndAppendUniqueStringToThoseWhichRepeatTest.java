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

import static org.jclouds.compute.config.ComputeServiceProperties.RESOURCENAME_DELIMITER;
import static org.jclouds.compute.config.ComputeServiceProperties.RESOURCENAME_PREFIX;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.predicates.Validator;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

/**
 * 
 * @author Adrian Cole
 */
@Test(testName = "FormatSharedNamesAndAppendUniqueStringToThoseWhichRepeatTest")
public class FormatSharedNamesAndAppendUniqueStringToThoseWhichRepeatTest {
   Validator<String> okValidator = new Validator<String>() {

      @Override
      public void validate(String t) throws IllegalArgumentException {

      }

   };

   public void testSharedName() {
      FormatSharedNamesAndAppendUniqueStringToThoseWhichRepeat fn = new FormatSharedNamesAndAppendUniqueStringToThoseWhichRepeat(
            "jclouds", '_', Suppliers.ofInstance("123"), okValidator);

      assertEquals(fn.sharedNameForGroup("cluster"), "jclouds_cluster");
      assertEquals(fn.groupInSharedNameOrNull("jclouds_cluster"), "cluster");
      assertEquals(fn.groupInUniqueNameOrNull("jclouds_cluster"), null);
      assertTrue(fn.containsGroup("cluster").apply("jclouds_cluster"));
   }

   public void testOkToHaveDelimiterInGroupOnUniqueName() {
      FormatSharedNamesAndAppendUniqueStringToThoseWhichRepeat fn = new FormatSharedNamesAndAppendUniqueStringToThoseWhichRepeat(
            "jclouds", '_', Suppliers.ofInstance("123"), okValidator);

      assertEquals(fn.sharedNameForGroup("cluster_"), "jclouds_cluster_");
      assertEquals(fn.groupInSharedNameOrNull("jclouds_cluster_"), "cluster_");
      assertEquals(fn.groupInUniqueNameOrNull("jclouds_cluster_"), null);
      assertTrue(fn.containsGroup("cluster_").apply("jclouds_cluster_"));
   }

   public void testSharedNameWithHyphenInGroup() {
      FormatSharedNamesAndAppendUniqueStringToThoseWhichRepeat fn = new FormatSharedNamesAndAppendUniqueStringToThoseWhichRepeat(
            "jclouds", '_', Suppliers.ofInstance("123"), okValidator);

      assertEquals(fn.sharedNameForGroup("cluster-"), "jclouds_cluster-");
      assertEquals(fn.groupInSharedNameOrNull("jclouds_cluster-"), "cluster-");
      assertEquals(fn.groupInUniqueNameOrNull("jclouds_cluster-"), null);
      assertTrue(fn.containsGroup("cluster-").apply("jclouds_cluster-"));
   }

   public void testNextName() {
      FormatSharedNamesAndAppendUniqueStringToThoseWhichRepeat fn = new FormatSharedNamesAndAppendUniqueStringToThoseWhichRepeat(
            "jclouds", '_', Suppliers.ofInstance("123"), okValidator);

      assertEquals(fn.uniqueNameForGroup("cluster"), "jclouds_cluster_123");
      // note accidental treatment of a unique node as a shared one can lead to 
      // incorrect group names, as long as we permit delimiter to be in group name
      assertEquals(fn.groupInSharedNameOrNull("jclouds_cluster_123"), "cluster_123");
      assertEquals(fn.groupInUniqueNameOrNull("jclouds_cluster_123"), "cluster");
      assertTrue(fn.containsGroup("cluster").apply("jclouds_cluster_123"));
   }

   public void testCannotFindSharedNameWhenDelimiterWrong() {
      FormatSharedNamesAndAppendUniqueStringToThoseWhichRepeat fn = new FormatSharedNamesAndAppendUniqueStringToThoseWhichRepeat(
            "jclouds", '_', Suppliers.ofInstance("123"), okValidator);
      assertEquals(fn.groupInSharedNameOrNull("jclouds#cluster"), null);
      assertEquals(fn.groupInUniqueNameOrNull("jclouds#cluster"), null);
      assertFalse(fn.containsGroup("cluster").apply("jclouds#cluster"));
   }

   public void testCannotFindNextNameWhenDelimiterWrong() {
      FormatSharedNamesAndAppendUniqueStringToThoseWhichRepeat fn = new FormatSharedNamesAndAppendUniqueStringToThoseWhichRepeat(
            "jclouds", '_', Suppliers.ofInstance("123"), okValidator);
      assertEquals(fn.groupInSharedNameOrNull("jclouds#cluster#123"), null);
      assertEquals(fn.groupInUniqueNameOrNull("jclouds#cluster#123"), null);
      assertFalse(fn.containsGroup("cluster").apply("jclouds#cluster#123"));
   }

   public void testPropertyChangesDelimiter() {
      GroupNamingConvention fn = Guice.createInjector(new AbstractModule() {

         @Override
         protected void configure() {
            bindConstant().annotatedWith(Names.named(RESOURCENAME_DELIMITER)).to('#');
         }
      }).getInstance(GroupNamingConvention.Factory.class).create();

      assertEquals(fn.sharedNameForGroup("cluster"), "jclouds#cluster");
      assertEquals(fn.groupInSharedNameOrNull("jclouds#cluster"), "cluster");
      assertEquals(fn.groupInUniqueNameOrNull("jclouds#cluster"), null);
      assertTrue(fn.containsGroup("cluster").apply("jclouds#cluster"));
   }

   public void testPropertyChangesPrefix() {
      GroupNamingConvention fn = Guice.createInjector(new AbstractModule() {

         @Override
         protected void configure() {
            bindConstant().annotatedWith(Names.named(RESOURCENAME_PREFIX)).to("kclouds");
         }
      }).getInstance(GroupNamingConvention.Factory.class).create();

      assertEquals(fn.sharedNameForGroup("cluster"), "kclouds-cluster");
      assertEquals(fn.groupInSharedNameOrNull("kclouds-cluster"), "cluster");
      assertEquals(fn.groupInUniqueNameOrNull("kclouds-cluster"), null);
      assertTrue(fn.containsGroup("cluster").apply("kclouds-cluster"));

   }

   public void testCanChangeSuffixSupplier() {
      GroupNamingConvention fn = Guice.createInjector(new AbstractModule() {

         @Override
         protected void configure() {
            bind(new TypeLiteral<Supplier<String>>() {
            }).toInstance(Suppliers.ofInstance("foo"));
         }
      }).getInstance(GroupNamingConvention.Factory.class).create();

      assertEquals(fn.uniqueNameForGroup("cluster"), "jclouds-cluster-foo");
      // note accidental treatment of a unique node as a shared one can lead to 
      // incorrect group names, as long as we permit delimiter to be in group name
      assertEquals(fn.groupInSharedNameOrNull("jclouds-cluster-foo"), "cluster-foo");
      assertEquals(fn.groupInUniqueNameOrNull("jclouds-cluster-foo"), "cluster");
      assertTrue(fn.containsGroup("cluster").apply("jclouds-cluster-foo"));

   }

   // ///

   public void testSharedNameNoPrefix() {
      FormatSharedNamesAndAppendUniqueStringToThoseWhichRepeat fn = new FormatSharedNamesAndAppendUniqueStringToThoseWhichRepeat(
            "", '_', Suppliers.ofInstance("123"), okValidator);

      assertEquals(fn.sharedNameForGroup("cluster"), "cluster");
      assertEquals(fn.groupInSharedNameOrNull("cluster"), "cluster");
      assertEquals(fn.groupInUniqueNameOrNull("cluster"), null);
      assertTrue(fn.containsGroup("cluster").apply("cluster"));
   }

   public void testNextNameNoPrefix() {
      FormatSharedNamesAndAppendUniqueStringToThoseWhichRepeat fn = new FormatSharedNamesAndAppendUniqueStringToThoseWhichRepeat(
            "", '_', Suppliers.ofInstance("123"), okValidator);

      assertEquals(fn.uniqueNameForGroup("cluster"), "cluster_123");
      assertEquals(fn.groupInSharedNameOrNull("cluster_123"), "cluster_123");
      assertEquals(fn.groupInUniqueNameOrNull("cluster_123"), "cluster");
      assertTrue(fn.containsGroup("cluster").apply("cluster_123"));
   }
}
