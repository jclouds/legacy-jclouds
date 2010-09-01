/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.filesystem.utils;

import com.google.inject.Inject;
import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.util.BlobUtils;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Implements the {@link BlobUtils} interfaced and act as a bridge to
 * {@link FilesystemStorageStrategy} when used inside {@link AsyncBlobStore}
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 */
public class FileSystemBlobUtilsImpl implements BlobUtils {

    protected final FilesystemStorageStrategy storageStrategy;

    @Inject
    public FileSystemBlobUtilsImpl(
            FilesystemStorageStrategy storageStrategy) {
        this.storageStrategy = checkNotNull(storageStrategy, "Filesystem Storage Strategy");
    }



    @Override
    public Blob newBlob(String name) {
        return storageStrategy.newBlob(name);
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
