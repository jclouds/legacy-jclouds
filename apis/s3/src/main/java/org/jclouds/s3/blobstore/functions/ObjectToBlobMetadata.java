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

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.blobstore.domain.StorageType;
import org.jclouds.blobstore.domain.internal.MutableBlobMetadataImpl;
import org.jclouds.blobstore.strategy.IfDirectoryReturnNameStrategy;
import org.jclouds.domain.Location;
import org.jclouds.http.HttpUtils;
import org.jclouds.s3.domain.AccessControlList;
import org.jclouds.s3.domain.AccessControlList.GroupGranteeURI;
import org.jclouds.s3.domain.AccessControlList.Permission;
import org.jclouds.s3.domain.ObjectMetadata;

import com.google.common.base.Function;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * @author Adrian Cole
 */
@Singleton
public class ObjectToBlobMetadata implements Function<ObjectMetadata, MutableBlobMetadata> {
   private final IfDirectoryReturnNameStrategy ifDirectoryReturnName;
   private final LoadingCache<String, AccessControlList> bucketAcls;
   private final Function<String, Location> locationOfBucket;

   @Inject
   public ObjectToBlobMetadata(IfDirectoryReturnNameStrategy ifDirectoryReturnName,
            LoadingCache<String, AccessControlList> bucketAcls,Function<String, Location> locationOfBucket) {
      this.ifDirectoryReturnName = ifDirectoryReturnName;
      this.bucketAcls = bucketAcls;
      this.locationOfBucket = locationOfBucket;
   }

   public MutableBlobMetadata apply(ObjectMetadata from) {
      if (from == null)
         return null;
      MutableBlobMetadata to = new MutableBlobMetadataImpl();
      HttpUtils.copy(from.getContentMetadata(), to.getContentMetadata());
      try {
         AccessControlList bucketAcl = bucketAcls.getUnchecked(from.getBucket());
         if (bucketAcl.hasPermission(GroupGranteeURI.ALL_USERS, Permission.READ))
            to.setPublicUri(from.getUri());
      } catch (CacheLoader.InvalidCacheLoadException e) {
         // nulls not permitted from cache loader
      }
      to.setUri(from.getUri());
      to.setContainer(from.getBucket());
      to.setETag(from.getETag());
      to.setName(from.getKey());
      to.setLastModified(from.getLastModified());
      to.setUserMetadata(from.getUserMetadata());
      to.setLocation(locationOfBucket.apply(from.getBucket()));
      String directoryName = ifDirectoryReturnName.execute(to);
      if (directoryName != null) {
         to.setName(directoryName);
         to.setType(StorageType.RELATIVE_PATH);
      } else {
         to.setType(StorageType.BLOB);
      }
      return to;
   }
}
