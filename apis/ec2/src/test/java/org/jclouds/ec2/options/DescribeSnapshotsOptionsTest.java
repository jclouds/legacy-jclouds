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
package org.jclouds.ec2.options;

import static org.jclouds.ec2.options.DescribeSnapshotsOptions.Builder.ownedBy;
import static org.jclouds.ec2.options.DescribeSnapshotsOptions.Builder.restorableBy;
import static org.jclouds.ec2.options.DescribeSnapshotsOptions.Builder.snapshotIds;
import static org.testng.Assert.assertEquals;

import java.util.Collections;

import org.jclouds.http.options.HttpRequestOptions;
import org.testng.annotations.Test;

/**
 * Tests possible uses of DescribeSnapshotsOptions and DescribeSnapshotsOptions.Builder.*
 * 
 * @author Adrian Cole
 */
public class DescribeSnapshotsOptionsTest {

   @Test
   public void testAssignability() {
      assert HttpRequestOptions.class.isAssignableFrom(DescribeSnapshotsOptions.class);
      assert !String.class.isAssignableFrom(DescribeSnapshotsOptions.class);
   }

   @Test
   public void testRestorableBy() {
      DescribeSnapshotsOptions options = new DescribeSnapshotsOptions();
      options.restorableBy("test");
      assertEquals(options.buildFormParameters().get("RestorableBy.1"), Collections
               .singletonList("test"));
   }

   @Test
   public void testNullRestorableBy() {
      DescribeSnapshotsOptions options = new DescribeSnapshotsOptions();
      assertEquals(options.buildFormParameters().get("RestorableBy.1"), Collections.EMPTY_LIST);
   }

   @Test
   public void testRestorableByStatic() {
      DescribeSnapshotsOptions options = restorableBy("test");
      assertEquals(options.buildFormParameters().get("RestorableBy.1"), Collections
               .singletonList("test"));
   }

   @Test
   public void testOwners() {
      DescribeSnapshotsOptions options = new DescribeSnapshotsOptions();
      options.ownedBy("test");
      assertEquals(options.buildFormParameters().get("Owner.1"), Collections.singletonList("test"));
   }

   @Test
   public void testMultipleOwners() {
      DescribeSnapshotsOptions options = new DescribeSnapshotsOptions();
      options.ownedBy("test", "trouble");
      assertEquals(options.buildFormParameters().get("Owner.1"), Collections.singletonList("test"));
      assertEquals(options.buildFormParameters().get("Owner.2"), Collections
               .singletonList("trouble"));
   }

   @Test
   public void testNullOwners() {
      DescribeSnapshotsOptions options = new DescribeSnapshotsOptions();
      assertEquals(options.buildFormParameters().get("Owner.1"), Collections.EMPTY_LIST);
   }

   @Test
   public void testOwnersStatic() {
      DescribeSnapshotsOptions options = ownedBy("test");
      assertEquals(options.buildFormParameters().get("Owner.1"), Collections.singletonList("test"));
   }

   public void testNoOwners() {
      ownedBy();
   }

   @Test
   public void testSnapshotIds() {
      DescribeSnapshotsOptions options = new DescribeSnapshotsOptions();
      options.snapshotIds("test");
      assertEquals(options.buildFormParameters().get("SnapshotId.1"), Collections
               .singletonList("test"));
   }

   @Test
   public void testMultipleSnapshotIds() {
      DescribeSnapshotsOptions options = new DescribeSnapshotsOptions();
      options.snapshotIds("test", "trouble");
      assertEquals(options.buildFormParameters().get("SnapshotId.1"), Collections
               .singletonList("test"));
      assertEquals(options.buildFormParameters().get("SnapshotId.2"), Collections
               .singletonList("trouble"));
   }

   @Test
   public void testNullSnapshotIds() {
      DescribeSnapshotsOptions options = new DescribeSnapshotsOptions();
      assertEquals(options.buildFormParameters().get("SnapshotId.1"), Collections.EMPTY_LIST);
   }

   @Test
   public void testSnapshotIdsStatic() {
      DescribeSnapshotsOptions options = snapshotIds("test");
      assertEquals(options.buildFormParameters().get("SnapshotId.1"), Collections
               .singletonList("test"));
   }

   public void testNoSnapshotIds() {
      snapshotIds();
   }
}
