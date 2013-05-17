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

import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;
import static org.testng.Assert.assertEquals;

import org.jclouds.blobstore.domain.MutableStorageMetadata;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.StorageType;
import org.jclouds.blobstore.domain.internal.MutableStorageMetadataImpl;
import org.jclouds.blobstore.domain.internal.PageSetImpl;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.s3.domain.BucketMetadata;
import org.testng.annotations.Test;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * Tests behavior of {@code BucketsToStorageMetadata}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "BucketsToStorageMetadataTest")
public class BucketsToStorageMetadataTest {
   protected Location provider = new LocationBuilder().scope(LocationScope.PROVIDER).id("aws-ec2")
            .description("aws-ec2").build();

   protected Location region = new LocationBuilder().scope(LocationScope.REGION).id("us-east-1")
            .description("us-east-1").parent(provider).build();

   public void test() {
      BucketsToStorageMetadata fn = new BucketsToStorageMetadata(
               sameThreadExecutor(),
               new BucketToResourceMetadata(Functions.forMap(ImmutableMap.<String, Location> of("mycontainer", region))));

      MutableStorageMetadata expected = new MutableStorageMetadataImpl();
      expected.setName("mycontainer");
      expected.setType(StorageType.CONTAINER);
      expected.setLocation(region);
      
      assertEquals(
               fn.apply(ImmutableSet.of(new BucketMetadata("mycontainer", null, null))).toString(),
               new PageSetImpl<StorageMetadata>(ImmutableSet.<StorageMetadata> of(expected), null).toString());

   }
}
