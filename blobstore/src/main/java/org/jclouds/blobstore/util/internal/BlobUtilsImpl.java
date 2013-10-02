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
package org.jclouds.blobstore.util.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.blobstore.domain.BlobBuilder;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.strategy.ClearListStrategy;
import org.jclouds.blobstore.strategy.CountListStrategy;
import org.jclouds.blobstore.strategy.DeleteDirectoryStrategy;
import org.jclouds.blobstore.strategy.GetDirectoryStrategy;
import org.jclouds.blobstore.strategy.MkdirStrategy;
import org.jclouds.blobstore.util.BlobUtils;

/**
 * Encryption, Hashing, and IO Utilities needed to sign and verify blobstore requests and responses.
 * 
 * @author Adrian Cole
 */
@Singleton
public class BlobUtilsImpl implements BlobUtils {

   protected final Provider<BlobBuilder> blobBuilders;
   protected final ClearListStrategy clearContainerStrategy;
   protected final GetDirectoryStrategy getDirectoryStrategy;
   protected final MkdirStrategy mkdirStrategy;
   protected final DeleteDirectoryStrategy rmDirStrategy;
   protected final CountListStrategy countBlobsStrategy;

   @Inject
   protected BlobUtilsImpl(Provider<BlobBuilder> blobBuilders, ClearListStrategy clearContainerStrategy,
         GetDirectoryStrategy getDirectoryStrategy, MkdirStrategy mkdirStrategy, CountListStrategy countBlobsStrategy,
         DeleteDirectoryStrategy rmDirStrategy) {
      this.blobBuilders = checkNotNull(blobBuilders, "blobBuilders");
      this.clearContainerStrategy = checkNotNull(clearContainerStrategy, "clearContainerStrategy");
      this.getDirectoryStrategy = checkNotNull(getDirectoryStrategy, "getDirectoryStrategy");
      this.mkdirStrategy = checkNotNull(mkdirStrategy, "mkdirStrategy");
      this.rmDirStrategy = checkNotNull(rmDirStrategy, "rmDirStrategy");
      this.countBlobsStrategy = checkNotNull(countBlobsStrategy, "countBlobsStrategy");
   }
   
   @Override
   public BlobBuilder blobBuilder() {
      return blobBuilders.get();
   }

   public boolean directoryExists(String containerName, String directory) {
      return getDirectoryStrategy.execute(containerName, directory) != null;
   }

   public void createDirectory(String containerName, String directory) {
      mkdirStrategy.execute(containerName, directory);
   }

   public long countBlobs(String container, ListContainerOptions options) {
      return countBlobsStrategy.execute(container, options);
   }

   public void clearContainer(String container, ListContainerOptions options) {
      clearContainerStrategy.execute(container, options);
   }

   public void deleteDirectory(String container, String directory) {
      rmDirStrategy.execute(container, directory);
   }

}
