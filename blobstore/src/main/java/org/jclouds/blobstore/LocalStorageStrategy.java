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
package org.jclouds.blobstore;

import java.io.IOException;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.domain.Location;

/**
 * Strategy for local operations related to container and blob
 *
 * @author Alfredo "Rainbowbreeze" Morresi
 */
public interface LocalStorageStrategy {

    /**
     * Creates a new container
     *
     * @param container
     * @return
     */
    boolean createContainerInLocation(String container, Location location);

    /**
     * Deletes a container and all its content
     * @param container
     */
    void deleteContainer(String container);

    /**
     * Checks if a container exists
     * @param container
     * @return
     */
    boolean containerExists(String container);

    /**
     * Empty the container of its content (files and subdirectories), but doesn't
     * delete the container itself
     * @param container
     */
    void clearContainer(String container);

    /**
     * Like {@link #clearContainer(String)} except you can use options to do things like recursive
     * deletes, or clear at a different path than root.
     *
     * @param container
     *           what to clear
     * @param options
     *           recursion and path to clear
     */
    void clearContainer(String container, ListContainerOptions options);

    /**
     * Return an iterator that reports all the containers under base path
     * @return
     */
    Iterable<String> getAllContainerNames();

    /**
     * Return true if a blob named by key exists
     * @param container
     * @param key
     * @return
     */
    boolean blobExists(String container, String key);

    /**
     * Load the blob with the given key belonging to the container with the given
     * name. There must exist a resource on the file system whose complete name
     * is given concatenating the container name and the key
     *
     * @param container
     *           it's the name of the container the blob belongs to
     * @param key
     *           it's the key of the blob
     *
     * @return the blob belonging to the given container with the given key
     */
    Blob getBlob(String containerName, String blobName);

    /**
     * Returns all the blobs key inside a container
     * @param container
     * @return
     * @throws IOException
     */
    Iterable<String> getBlobKeysInsideContainer(String container) throws IOException;

    /**
     * Remove blob named by the given key
     * @param container
     * @param key
     */
    void removeBlob(String container, String key);

    /**
     * Write a {@link Blob} into a file
     * @param container
     * @param blob
     * @throws IOException
     */
    void putBlob(String containerName, Blob blob) throws IOException;

}
