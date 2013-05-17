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
package org.jclouds.blobstore.options;

import static org.jclouds.blobstore.options.ListContainerOptions.Builder.afterMarker;
import static org.jclouds.blobstore.options.ListContainerOptions.Builder.inDirectory;
import static org.jclouds.blobstore.options.ListContainerOptions.Builder.maxResults;
import static org.jclouds.blobstore.options.ListContainerOptions.Builder.recursive;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

/**
 * Tests possible uses of ListOptions and ListOptions.Builder.*
 * 
 * @author Adrian Cole
 */
public class ListOptionsTest {
   @Test
   public void testRecursive() {
      ListContainerOptions options = new ListContainerOptions();
      options.recursive();
      assertTrue(options.isRecursive());
   }

   @Test
   public void testRecursiveStatic() {
      ListContainerOptions options = recursive();
      assertTrue(options.isRecursive());
   }

   @Test
   public void testPath() {
      ListContainerOptions options = new ListContainerOptions();
      options.inDirectory("test");
      assertEquals(options.getDir(), "test");
   }

   @Test
   public void testPathStatic() {
      ListContainerOptions options = inDirectory("test");
      assertEquals(options.getDir(), "test");
   }

   @Test
   public void testTwoOptions() {
      ListContainerOptions options = new ListContainerOptions();
      options.inDirectory("test").maxResults(1);
      assertEquals(options.getDir(), "test");
      assertEquals(options.getMaxResults(), Integer.valueOf(1));

   }

   @Test
   public void testNullPath() {
      ListContainerOptions options = new ListContainerOptions();
      assertEquals(options.getDir(), null);
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testPathNPE() {
      inDirectory(null);
   }

   @Test
   public void testMarker() {
      ListContainerOptions options = new ListContainerOptions();
      options.afterMarker("test");
      assertEquals(options.getMarker(), "test");

   }

   @Test
   public void testNullMarker() {
      ListContainerOptions options = new ListContainerOptions();
      assertEquals(options.getMarker(), null);
   }

   @Test
   public void testMarkerStatic() {
      ListContainerOptions options = afterMarker("test");
      assertEquals(options.getMarker(), "test");
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testMarkerNPE() {
      afterMarker(null);
   }

   @Test
   public void testMaxResults() {
      ListContainerOptions options = new ListContainerOptions();
      options.maxResults(1000);
      assertEquals(options.getMaxResults(), Integer.valueOf(1000));
   }

   @Test
   public void testNullMaxResults() {
      ListContainerOptions options = new ListContainerOptions();
      assertEquals(options.getMaxResults(), null);
   }

   @Test
   public void testMaxResultsStatic() {
      ListContainerOptions options = maxResults(1000);
      assertEquals(options.getMaxResults(), Integer.valueOf(1000));
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testMaxResultsNegative() {
      maxResults(-1);
   }
}
