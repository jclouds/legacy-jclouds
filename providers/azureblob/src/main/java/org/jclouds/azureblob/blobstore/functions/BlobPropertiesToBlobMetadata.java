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
package org.jclouds.azureblob.blobstore.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.azureblob.domain.BlobProperties;
import org.jclouds.azureblob.domain.PublicAccess;
import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.blobstore.domain.StorageType;
import org.jclouds.blobstore.domain.internal.MutableBlobMetadataImpl;
import org.jclouds.blobstore.strategy.IfDirectoryReturnNameStrategy;
import org.jclouds.http.HttpUtils;

import com.google.common.base.Function;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * @author Adrian Cole
 */
@Singleton
public class BlobPropertiesToBlobMetadata implements Function<BlobProperties, MutableBlobMetadata> {
   private final IfDirectoryReturnNameStrategy ifDirectoryReturnName;
   private final LoadingCache<String, PublicAccess> containerAcls;

   @Inject
   public BlobPropertiesToBlobMetadata(IfDirectoryReturnNameStrategy ifDirectoryReturnName,
            LoadingCache<String, PublicAccess> containerAcls) {
      this.ifDirectoryReturnName = checkNotNull(ifDirectoryReturnName, "ifDirectoryReturnName");
      this.containerAcls = checkNotNull(containerAcls, "containerAcls");
   }

   public MutableBlobMetadata apply(BlobProperties from) {
      if (from == null)
         return null;
      MutableBlobMetadata to = new MutableBlobMetadataImpl();
      HttpUtils.copy(from.getContentMetadata(), to.getContentMetadata());
      to.setUserMetadata(from.getMetadata());
      to.setETag(from.getETag());
      to.setLastModified(from.getLastModified());
      to.setName(from.getName());
      to.setContainer(from.getContainer());
      to.setUri(from.getUrl());
      if (from.getContainer() != null)
         try {
            PublicAccess containerAcl = containerAcls.getUnchecked(from.getContainer());
            if (containerAcl != PublicAccess.PRIVATE)
               to.setPublicUri(from.getUrl());
         } catch (CacheLoader.InvalidCacheLoadException e) {
            // nulls not permitted from cache loader
         }
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
