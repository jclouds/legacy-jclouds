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
package org.jclouds.cloudstack.options;

import static org.jclouds.cloudstack.options.ListISOsOptions.Builder.bootable;
import static org.jclouds.cloudstack.options.ListISOsOptions.Builder.hypervisor;
import static org.jclouds.cloudstack.options.ListISOsOptions.Builder.id;
import static org.jclouds.cloudstack.options.ListISOsOptions.Builder.isNotReady;
import static org.jclouds.cloudstack.options.ListISOsOptions.Builder.isPrivate;
import static org.jclouds.cloudstack.options.ListISOsOptions.Builder.isPublic;
import static org.jclouds.cloudstack.options.ListISOsOptions.Builder.isReady;
import static org.jclouds.cloudstack.options.ListISOsOptions.Builder.isoFilter;
import static org.jclouds.cloudstack.options.ListISOsOptions.Builder.keyword;
import static org.jclouds.cloudstack.options.ListISOsOptions.Builder.name;
import static org.jclouds.cloudstack.options.ListISOsOptions.Builder.notBootable;
import static org.jclouds.cloudstack.options.ListISOsOptions.Builder.zoneId;
import static org.testng.Assert.assertEquals;

import java.util.Set;

import org.jclouds.cloudstack.domain.ISO;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests behavior of {@link ListISOsOptions}
 * 
 * @author grkvlt@apache.org
 */
@Test(groups = "unit")
public class ListISOsOptionsTest {
   private static final Set<String> TRUE = ImmutableSet.of(Boolean.toString(true));
   private static final Set<String> FALSE = ImmutableSet.of(Boolean.toString(false));

   public void testBootable() {
      ListISOsOptions options = new ListISOsOptions().bootable();
      assertEquals(options.buildQueryParameters().get("bootable"), TRUE);
   }

   public void testBootableStatic() {
      ListISOsOptions options = bootable();
      assertEquals(options.buildQueryParameters().get("bootable"), TRUE);
   }

   public void testNotBootable() {
      ListISOsOptions options = new ListISOsOptions().notBootable();
      assertEquals(options.buildQueryParameters().get("bootable"), FALSE);
   }

   public void testNotBootableStatic() {
      ListISOsOptions options = notBootable();
      assertEquals(options.buildQueryParameters().get("bootable"), FALSE);
   }

   public void testHypervisor() {
      ListISOsOptions options = new ListISOsOptions().hypervisor("KVM");
      assertEquals(options.buildQueryParameters().get("hypervisor"), ImmutableSet.of("KVM"));
   }

   public void testHypervisorStatic() {
      ListISOsOptions options = hypervisor("KVM");
      assertEquals(options.buildQueryParameters().get("hypervisor"), ImmutableSet.of("KVM"));
   }

   public void testId() {
      ListISOsOptions options = new ListISOsOptions().id("6");
      assertEquals(options.buildQueryParameters().get("id"), ImmutableSet.of("6"));
   }

   public void testIdStatic() {
      ListISOsOptions options = id("6");
      assertEquals(options.buildQueryParameters().get("id"), ImmutableSet.of("6"));
   }

   public void testISOFilter() {
      ListISOsOptions options = new ListISOsOptions().isoFilter(ISO.ISOFilter.community);
      assertEquals(options.buildQueryParameters().get("isofilter"), ImmutableSet.of(ISO.ISOFilter.community.name()));
   }

   public void testISOFilterStatic() {
      ListISOsOptions options = isoFilter(ISO.ISOFilter.community);
      assertEquals(options.buildQueryParameters().get("isofilter"), ImmutableSet.of(ISO.ISOFilter.community.name()));
   }

   public void testIsPublic() {
      ListISOsOptions options = new ListISOsOptions().isPublic();
      assertEquals(options.buildQueryParameters().get("ispublic"), TRUE);
   }

   public void testIsPublicStatic() {
      ListISOsOptions options = isPublic();
      assertEquals(options.buildQueryParameters().get("ispublic"), TRUE);
   }

   public void testIsPrivate() {
      ListISOsOptions options = new ListISOsOptions().isPrivate();
      assertEquals(options.buildQueryParameters().get("ispublic"), FALSE);
   }

   public void testIsPrivateStatic() {
      ListISOsOptions options = isPrivate();
      assertEquals(options.buildQueryParameters().get("ispublic"), FALSE);
   }

   public void testIsReady() {
      ListISOsOptions options = new ListISOsOptions().isReady();
      assertEquals(options.buildQueryParameters().get("isready"), TRUE);
   }

   public void testIsReadyStatic() {
      ListISOsOptions options = isReady();
      assertEquals(options.buildQueryParameters().get("isready"), TRUE);
   }

   public void testIsNotReady() {
      ListISOsOptions options = new ListISOsOptions().isNotReady();
      assertEquals(options.buildQueryParameters().get("isready"), FALSE);
   }

   public void testIsNotReadyStatic() {
      ListISOsOptions options = isNotReady();
      assertEquals(options.buildQueryParameters().get("isready"), FALSE);
   }

   public void testKeyword() {
      ListISOsOptions options = new ListISOsOptions().keyword("text");
      assertEquals(options.buildQueryParameters().get("keyword"), ImmutableSet.of("text"));
   }

   public void testKeywordStatic() {
      ListISOsOptions options = keyword("text");
      assertEquals(options.buildQueryParameters().get("keyword"), ImmutableSet.of("text"));
   }

   public void testName() {
      ListISOsOptions options = new ListISOsOptions().name("text");
      assertEquals(options.buildQueryParameters().get("name"), ImmutableSet.of("text"));
   }

   public void testNameStatic() {
      ListISOsOptions options = name("text");
      assertEquals(options.buildQueryParameters().get("name"), ImmutableSet.of("text"));
   }

   public void testZoneId() {
      ListISOsOptions options = new ListISOsOptions().zoneId("6");
      assertEquals(ImmutableSet.of("6"), options.buildQueryParameters().get("zoneid"));
   }

   public void testZoneIdStatic() {
      ListISOsOptions options = zoneId("6");
      assertEquals(ImmutableSet.of("6"), options.buildQueryParameters().get("zoneid"));
   }
}
