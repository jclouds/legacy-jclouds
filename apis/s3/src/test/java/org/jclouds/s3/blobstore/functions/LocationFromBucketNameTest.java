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
package org.jclouds.s3.blobstore.functions;

import static org.testng.Assert.assertEquals;

import java.util.Set;

import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.testng.annotations.Test;

import com.google.common.base.Functions;
import com.google.common.base.Optional;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * Tests behavior of {@code LocationFromBucketName}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "LocationFromBucketNameTest")
public class LocationFromBucketNameTest {
   protected Location provider = new LocationBuilder().scope(LocationScope.PROVIDER).id("aws-ec2")
            .description("aws-ec2").build();

   protected Location region = new LocationBuilder().scope(LocationScope.REGION).id("us-east-1")
            .description("us-east-1").parent(provider).build();

   protected Location region2 = new LocationBuilder().scope(LocationScope.REGION).id("eu-west-1")
            .description("eu-west-1").parent(provider).build();

   public void testOnlyLocationDoesntNeedMapping() {
      LocationFromBucketName fn = new LocationFromBucketName(Functions.forMap(ImmutableMap
               .<String, Optional<String>> of()), Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet
               .of(provider)));

      assertEquals(fn.apply("mybucket"), provider);

   }

   public void testMapsToCorrectRegion() {
      LocationFromBucketName fn = new LocationFromBucketName(Functions.forMap(ImmutableMap
               .<String, Optional<String>> of("mybucket", Optional.of("eu-west-1"))),
               Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet.of(region, region2)));

      assertEquals(fn.apply("mybucket"), region2);

   }

   public void testNullOnUnmatchedRegion() {
      LocationFromBucketName fn = new LocationFromBucketName(Functions.forMap(ImmutableMap
               .<String, Optional<String>> of("mybucket", Optional.of("eu-west-2"))),
               Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet.of(region, region2)));

      assertEquals(fn.apply("mybucket"), null);

   }

   public void testNullOnAbsentData() {
      LocationFromBucketName fn = new LocationFromBucketName(Functions.forMap(ImmutableMap
               .<String, Optional<String>> of("mybucket", Optional.<String> absent())),
               Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet.of(region, region2)));

      assertEquals(fn.apply("mybucket"), null);

   }
}
