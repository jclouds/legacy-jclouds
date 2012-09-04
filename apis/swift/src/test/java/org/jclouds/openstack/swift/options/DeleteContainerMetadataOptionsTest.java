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
package org.jclouds.openstack.swift.options;

import static org.testng.Assert.assertEquals;

import org.jclouds.openstack.swift.reference.SwiftHeaders;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;

/**
 * Tests behavior of {@code DeleteContainerMetadataOptions}
 * 
 * @author Everett Toews
 */
@Test(groups = "unit")
public class DeleteContainerMetadataOptionsTest {

   public void testMetadata() {
      DeleteContainerMetadataOptions options = new DeleteContainerMetadataOptions().deleteMetadata(
         ImmutableList.of("test", "foo"));
      Multimap<String, String> headers = options.buildRequestHeaders();
      
      assertEquals(headers.size(), 2);
      assertEquals(headers.get(SwiftHeaders.CONTAINER_DELETE_METADATA_PREFIX + "test"), ImmutableList.of(""));
      assertEquals(headers.get(SwiftHeaders.CONTAINER_DELETE_METADATA_PREFIX + "foo"), ImmutableList.of(""));
   }

   public void testMetadataAlreadyPrefixed() {
      DeleteContainerMetadataOptions options = new DeleteContainerMetadataOptions().deleteMetadata(
         ImmutableList.of(SwiftHeaders.CONTAINER_DELETE_METADATA_PREFIX + "test", 
                          SwiftHeaders.CONTAINER_DELETE_METADATA_PREFIX + "foo"));
      Multimap<String, String> headers = options.buildRequestHeaders();
      
      assertEquals(headers.size(), 2);
      assertEquals(headers.get(SwiftHeaders.CONTAINER_DELETE_METADATA_PREFIX + "test"), ImmutableList.of(""));
      assertEquals(headers.get(SwiftHeaders.CONTAINER_DELETE_METADATA_PREFIX + "foo"), ImmutableList.of(""));
   }

   public void testMetadataStatic() {
      DeleteContainerMetadataOptions options = DeleteContainerMetadataOptions.Builder.deleteMetadata(
         ImmutableList.of(SwiftHeaders.CONTAINER_DELETE_METADATA_PREFIX + "test", 
                          SwiftHeaders.CONTAINER_DELETE_METADATA_PREFIX + "foo"));
      Multimap<String, String> headers = options.buildRequestHeaders();
      
      assertEquals(headers.size(), 2);
      assertEquals(headers.get(SwiftHeaders.CONTAINER_DELETE_METADATA_PREFIX + "test"), ImmutableList.of(""));
      assertEquals(headers.get(SwiftHeaders.CONTAINER_DELETE_METADATA_PREFIX + "foo"), ImmutableList.of(""));
   }
}
