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
package org.jclouds.openstack.swift.options;

import static org.testng.Assert.assertEquals;

import org.jclouds.openstack.swift.options.CreateContainerOptions;
import org.jclouds.openstack.swift.reference.SwiftHeaders;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * Tests behavior of {@code CreateContainerOptions}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class CreateContainerOptionsTest {

   public void testPublicAccess() {
      CreateContainerOptions options = new CreateContainerOptions().withPublicAccess();
      assertEquals(ImmutableList.of(".r:*,.rlistings"), 
    		       options.buildRequestHeaders().get("X-Container-Read"));
   }

   public void testPublicAccessStatic() {
      CreateContainerOptions options = CreateContainerOptions.Builder.withPublicAccess();
      assertEquals(ImmutableList.of(".r:*,.rlistings"), 
    		       options.buildRequestHeaders().get("X-Container-Read"));
   }

   public void testMetadata() {
      CreateContainerOptions options = new CreateContainerOptions().withMetadata(ImmutableMap
               .of("test", "foo"));
      assertEquals(ImmutableList.of("foo"), options.buildRequestHeaders().get(
               SwiftHeaders.CONTAINER_METADATA_PREFIX + "test"));
   }

   public void testMetadataAlreadyPrefixed() {
      CreateContainerOptions options = new CreateContainerOptions().withMetadata(ImmutableMap
               .of(SwiftHeaders.CONTAINER_METADATA_PREFIX + "test", "foo"));
      assertEquals(ImmutableList.of("foo"), options.buildRequestHeaders().get(
    		  SwiftHeaders.CONTAINER_METADATA_PREFIX + "test"));
   }

   public void testMetadataStatic() {
      CreateContainerOptions options = CreateContainerOptions.Builder
               .withMetadata(ImmutableMap.of("test", "foo"));
      assertEquals(ImmutableList.of("foo"), options.buildRequestHeaders().get(
    		  SwiftHeaders.CONTAINER_METADATA_PREFIX + "test"));
   }

}
