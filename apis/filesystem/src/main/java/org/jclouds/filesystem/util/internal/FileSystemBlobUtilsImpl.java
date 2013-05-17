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
package org.jclouds.filesystem.util.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Provider;

import org.jclouds.blobstore.LocalStorageStrategy;
import org.jclouds.blobstore.domain.BlobBuilder;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.util.BlobUtils;
import org.jclouds.filesystem.strategy.internal.FilesystemStorageStrategyImpl;

import com.google.inject.Inject;

/**
 * Implements the {@link BlobUtils} interfaced and act as a bridge to
 * {@link LocalStorageStrategy} when used inside {@link AsyncBlobStore}
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 */
public class FileSystemBlobUtilsImpl implements BlobUtils {

   protected final FilesystemStorageStrategyImpl storageStrategy;
   protected final Provider<BlobBuilder> blobBuilders;

   @Inject
   public FileSystemBlobUtilsImpl(LocalStorageStrategy storageStrategy, Provider<BlobBuilder> blobBuilders) {
      this.storageStrategy = (FilesystemStorageStrategyImpl) checkNotNull(storageStrategy, "Filesystem Storage Strategy");
      this.blobBuilders = checkNotNull(blobBuilders, "Filesystem  blobBuilders");
   }

   @Override
   public BlobBuilder blobBuilder() {
      return blobBuilders.get();
   }

   @Override
   public boolean directoryExists(String containerName, String directory) {
      return storageStrategy.directoryExists(containerName, directory);
   }

   @Override
   public void createDirectory(String containerName, String directory) {
      storageStrategy.createDirectory(containerName, directory);
   }

   @Override
   public long countBlobs(String container, ListContainerOptions options) {
      return storageStrategy.countBlobs(container, options);
   }

   @Override
   public void clearContainer(String container, ListContainerOptions options) {
      storageStrategy.clearContainer(container, options);
   }

   @Override
   public void deleteDirectory(String container, String directory) {
      storageStrategy.deleteDirectory(container, directory);
   }

}
