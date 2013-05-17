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
package org.jclouds.blobstore.functions;

import static org.testng.Assert.assertEquals;

import javax.inject.Provider;

import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.StorageType;
import org.jclouds.blobstore.domain.internal.MutableBlobMetadataImpl;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ResourceMetadataToRelativePathResourceMetadataTest {

   private ResourceMetadataToRelativePathResourceMetadata parser = new ResourceMetadataToRelativePathResourceMetadata();

   private Provider<MutableBlobMetadata> blobMetadataProvider = new Provider<MutableBlobMetadata>() {

      public MutableBlobMetadata get() {
         return new MutableBlobMetadataImpl();
      }

   };

   @Test
   public void test1() {
      MutableBlobMetadata md = blobMetadataProvider.get();
      md.setName("dir/");
      md.setId("dir/");
      StorageMetadata rd = parser.apply(md);
      assertEquals(rd.getName(), "dir");
      assertEquals(rd.getProviderId(), "dir/");
      assertEquals(rd.getType(), StorageType.RELATIVE_PATH);
   }

   @Test
   public void test2() {
      MutableBlobMetadata md = blobMetadataProvider.get();
      md.setName("dir_$folder$");
      md.setId("dir_$folder$");
      StorageMetadata rd = parser.apply(md);
      assertEquals(rd.getName(), "dir");
      assertEquals(rd.getProviderId(), "dir_$folder$");
      assertEquals(rd.getType(), StorageType.RELATIVE_PATH);
   }

   @Test
   public void testNoNameChange() {
      MutableBlobMetadata md = blobMetadataProvider.get();
      md.setName("dir");
      md.setId("dir");
      StorageMetadata rd = parser.apply(md);
      assertEquals(rd.getName(), "dir");
      assertEquals(rd.getProviderId(), "dir");
      assertEquals(rd.getType(), StorageType.RELATIVE_PATH);
   }
}
