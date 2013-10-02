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

import static org.jclouds.cloudstack.options.CreateDiskOfferingOptions.Builder.customized;
import static org.jclouds.cloudstack.options.CreateDiskOfferingOptions.Builder.diskSizeInGB;
import static org.jclouds.cloudstack.options.CreateDiskOfferingOptions.Builder.tags;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests behavior of {@code CreateDiskOfferingOptions}
 *
 * @author Andrei Savu
 */
@Test(groups = "unit")
public class CreateDiskOfferingOptionsTest {

   public void testTags() {
      CreateDiskOfferingOptions options =
         new CreateDiskOfferingOptions().tags(ImmutableSet.<String>of("tag1", "tag2"));
      assertEquals(ImmutableSet.of("tag1", "tag2"), options.buildQueryParameters().get("tags"));
   }

   public void testTagsStatic() {
      CreateDiskOfferingOptions options = tags(ImmutableSet.<String>of("tag1", "tag2"));
      assertEquals(ImmutableSet.of("tag1", "tag2"), options.buildQueryParameters().get("tags"));
   }

   public void testCustomized() {
      CreateDiskOfferingOptions options =
         new CreateDiskOfferingOptions().customized(true);
      assertEquals(ImmutableSet.of("true"), options.buildQueryParameters().get("customized"));
   }

   public void testCustomizedStatic() {
      CreateDiskOfferingOptions options = customized(true);
      assertEquals(ImmutableSet.of("true"), options.buildQueryParameters().get("customized"));
   }

   public void testDiskSizeInGB() {
      CreateDiskOfferingOptions options =
         new CreateDiskOfferingOptions().diskSizeInGB(100);
      assertEquals(ImmutableSet.of("100"), options.buildQueryParameters().get("disksize"));
   }

   public void testDiskSizeInGBStatic() {
      CreateDiskOfferingOptions options = diskSizeInGB(100);
      assertEquals(ImmutableSet.of("100"), options.buildQueryParameters().get("disksize"));
   }
}
