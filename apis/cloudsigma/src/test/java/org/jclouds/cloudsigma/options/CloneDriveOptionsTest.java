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
package org.jclouds.cloudsigma.options;

import static org.jclouds.cloudsigma.options.CloneDriveOptions.Builder.affinity;
import static org.jclouds.cloudsigma.options.CloneDriveOptions.Builder.size;
import static org.jclouds.cloudsigma.options.CloneDriveOptions.Builder.tags;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import org.jclouds.cloudsigma.domain.AffinityType;
import org.testng.annotations.Test;

/**
 * Tests possible uses of CloneDriveOptions and CloneDriveOptions.Builder.*
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class CloneDriveOptionsTest {

   @Test
   public void testNullSize() {
      CloneDriveOptions options = new CloneDriveOptions();
      assertNull(options.getOptions().get("size"));
   }

   @Test
   public void testSize() {
      CloneDriveOptions options = new CloneDriveOptions().size(1024);
      assertEquals(options.getOptions().get("size"), "1024");
   }

   @Test
   public void testSizeStatic() {
      CloneDriveOptions options = size(1024);
      assertEquals(options.getOptions().get("size"), "1024");
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testSizeNegative() {
      size(-1);
   }
   
   @Test
   public void testNullTags() {
      CloneDriveOptions options = new CloneDriveOptions();
      assertNull(options.getOptions().get("tags"));
   }

   @Test
   public void testTags() {
       CloneDriveOptions options = new CloneDriveOptions().tags("foo", "bar", "baz");
       assertEquals(options.getOptions().get("tags"), "foo bar baz");
   }
   
   @Test
   public void testTagsStatic() {
       CloneDriveOptions options = tags("foo", "bar", "baz");
       assertEquals(options.getOptions().get("tags"), "foo bar baz");
   }
   
   @Test
   public void testHddAffinity() {
       CloneDriveOptions options = new CloneDriveOptions().affinity(AffinityType.HDD);
       assertNull(options.getOptions().get("tags"));
   }
   
   @Test
   public void testHddAffinityStatic() {
       CloneDriveOptions options = affinity(AffinityType.HDD);
       assertNull(options.getOptions().get("tags"));
   }
   
   @Test
   public void testSsdAffinity() {
       CloneDriveOptions options = new CloneDriveOptions().affinity(AffinityType.SSD);
       assertEquals(options.getOptions().get("tags"), "affinity:ssd");
   }
   
   @Test
   public void testSsdAffinityStatic() {
       CloneDriveOptions options = affinity(AffinityType.SSD);
       assertEquals(options.getOptions().get("tags"), "affinity:ssd");
   }
   
   @Test
   public void testHddAffinityBeforeTags() {
       CloneDriveOptions options = new CloneDriveOptions().affinity(AffinityType.HDD);
       options.tags("foo", "bar", "baz");
       assertEquals(options.getOptions().get("tags"), "foo bar baz");
   }
   
   @Test
   public void testSsdAffinityBeforeTags() {
       CloneDriveOptions options = new CloneDriveOptions().affinity(AffinityType.SSD);
       options.tags("foo", "bar", "baz");
       assertEquals(options.getOptions().get("tags"), "foo bar baz affinity:ssd");
   }
   
   @Test
   public void testHddAffinityAfterTags() {
       CloneDriveOptions options = new CloneDriveOptions().tags("foo", "bar", "baz");
       options.affinity(AffinityType.HDD);
       assertEquals(options.getOptions().get("tags"), "foo bar baz");
   }
   
   @Test
   public void testSsdAffinityAfterTags() {
       CloneDriveOptions options = new CloneDriveOptions().tags("foo", "bar", "baz");
       options.affinity(AffinityType.SSD);
       assertEquals(options.getOptions().get("tags"), "foo bar baz affinity:ssd");
   }
   
}
