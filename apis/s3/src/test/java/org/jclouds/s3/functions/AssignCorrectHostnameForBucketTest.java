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
package org.jclouds.s3.functions;

import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.Map;

import org.jclouds.location.functions.RegionToEndpointOrProviderIfNull;
import org.testng.annotations.Test;

import com.google.common.base.Functions;
import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;

/**
 * Tests behavior of {@code AssignCorrectHostnameForBucket}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "AssignCorrectHostnameForBucketTest")
public class AssignCorrectHostnameForBucketTest {

   public void testWhenNoBucketRegionMappingInCache() {

      AssignCorrectHostnameForBucket fn = new AssignCorrectHostnameForBucket(new RegionToEndpointOrProviderIfNull(
               "aws-s3", Suppliers.ofInstance(URI.create("https://s3.amazonaws.com")),

               Suppliers.<Map<String, Supplier<URI>>> ofInstance(ImmutableMap.of("us-standard",
                        Suppliers.ofInstance(URI.create("https://s3.amazonaws.com")), "us-west-1",
                        Suppliers.ofInstance(URI.create("https://s3-us-west-1.amazonaws.com"))))),
                        
               Functions.forMap(ImmutableMap.<String, Optional<String>> of("bucket", Optional.<String> absent())));

      assertEquals(fn.apply("bucket"), URI.create("https://s3.amazonaws.com"));

   }

   public void testWhenBucketRegionMappingInCache() {

      AssignCorrectHostnameForBucket fn = new AssignCorrectHostnameForBucket(new RegionToEndpointOrProviderIfNull(
               "aws-s3", Suppliers.ofInstance(URI.create("https://s3.amazonaws.com")),

               Suppliers.<Map<String, Supplier<URI>>> ofInstance(ImmutableMap.of("us-standard",
                        Suppliers.ofInstance(URI.create("https://s3.amazonaws.com")), "us-west-1",
                        Suppliers.ofInstance(URI.create("https://s3-us-west-1.amazonaws.com"))))),
               
               Functions.forMap(ImmutableMap.<String, Optional<String>> of("bucket", Optional.of("us-west-1"))));

      assertEquals(fn.apply("bucket"), URI.create("https://s3-us-west-1.amazonaws.com"));

   }
}
