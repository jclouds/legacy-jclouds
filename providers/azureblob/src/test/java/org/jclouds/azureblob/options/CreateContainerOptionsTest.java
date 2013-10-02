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
package org.jclouds.azureblob.options;

import static org.testng.Assert.assertEquals;

import org.jclouds.azure.storage.reference.AzureStorageHeaders;
import org.jclouds.azureblob.domain.PublicAccess;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;

/**
 * Tests behavior of {@code CreateContainerOptions}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class CreateContainerOptionsTest {

   public void testPublicAccess() {
      CreateContainerOptions options = new CreateContainerOptions().withPublicAccess(PublicAccess.BLOB);
      assertEquals(ImmutableList.of("blob"), options.buildRequestHeaders().get(
               "x-ms-blob-public-access"));
   }

   public void testPublicAccessStatic() {
      CreateContainerOptions options = CreateContainerOptions.Builder.withPublicAccess(PublicAccess.BLOB);
      assertEquals(ImmutableList.of("blob"), options.buildRequestHeaders().get(
               "x-ms-blob-public-access"));
   }

   public void testMetadata() {
      CreateContainerOptions options = new CreateContainerOptions().withMetadata(ImmutableMultimap
               .of("test", "foo"));
      assertEquals(ImmutableList.of("foo"), options.buildRequestHeaders().get(
               AzureStorageHeaders.USER_METADATA_PREFIX + "test"));
   }

   public void testMetadataAlreadyPrefixed() {
      CreateContainerOptions options = new CreateContainerOptions().withMetadata(ImmutableMultimap
               .of(AzureStorageHeaders.USER_METADATA_PREFIX + "test", "foo"));
      assertEquals(ImmutableList.of("foo"), options.buildRequestHeaders().get(
               AzureStorageHeaders.USER_METADATA_PREFIX + "test"));
   }

   public void testMetadataStatic() {
      CreateContainerOptions options = CreateContainerOptions.Builder
               .withMetadata(ImmutableMultimap.of("test", "foo"));
      assertEquals(ImmutableList.of("foo"), options.buildRequestHeaders().get(
               AzureStorageHeaders.USER_METADATA_PREFIX + "test"));
   }

}
