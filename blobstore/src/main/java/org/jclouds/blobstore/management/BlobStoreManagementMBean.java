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

package org.jclouds.blobstore.management;

import org.jclouds.javax.annotation.Nullable;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.TabularData;

public interface BlobStoreManagementMBean {

   /**
    * The get locations command returns all the valid locations for containers. A location has a
    * scope, which is typically region or zone. A region is a general area, like eu-west, where a
    * zone is similar to a datacenter. If a location has a parent, that implies it is within that
    * location. For example a location can be a rack, whose parent is likely to be a zone.
    */
   TabularData listAssignableLocations() throws OpenDataException;

   /**
    * Lists all root-level resources available to the identity.
    */
   TabularData list() throws OpenDataException;

   /**
    * Lists all resources in a container non-recursive.
    *
    * @param container
    *           what to list
    * @return a list that may be incomplete, depending on whether PageSet#getNextMarker is set
    */
   TabularData list(String container) throws OpenDataException;

   /**
    * Retrieves the metadata of a {@code Blob} at location {@code container/name}
    *
    * @param container
    *           container where this exists.
    * @param name
    *           fully qualified name relative to the container.
    * @return null if name isn't present or the blob you intended to receive.
    * @throws org.jclouds.blobstore.ContainerNotFoundException
    *            if the container doesn't exist
    */
   CompositeData blobMetadata(String container, String name) throws OpenDataException;

   /**
    * Retrieves a {@code Blob} representing the data at location {@code container/name}
    *
    *
    * @param container
    *           container where this exists.
    * @param name
    *           fully qualified name relative to the container.
    * @return the blob you intended to receive or null, if it doesn't exist.
    * @throws org.jclouds.blobstore.ContainerNotFoundException
    *            if the container doesn't exist
    */
   CompositeData getBlob(String container, String name) throws OpenDataException;


   /**
    * determines if a service-level container exists
    */
   boolean containerExists(String container);

   /**
    * Creates a namespace for your blobs
    * <p/>
    *
    * A container is a namespace for your objects. Depending on the service, the scope can be
    * global, identity, or sub-identity scoped. For example, in Amazon S3, containers are called
    * buckets, and they must be uniquely named such that no-one else in the world conflicts. In
    * other blobstores, the naming convention of the container is less strict. All blobstores allow
    * you to list your containers and also the contents within them. These contents can either be
    * blobs, folders, or virtual paths.
    *
    * @param location
    *           some blobstores allow you to specify a location, such as US-EAST, for where this
    *           container will exist. null will choose a default location
    * @param container
    *           namespace. Typically constrained to lowercase alpha-numeric and hyphens.
    * @return true if the container was created, false if it already existed.
    */
   boolean createContainerInLocation(@Nullable String location, String container);


   /**
    * This will delete the contents of a container at its root path without deleting the container
    *
    * @param container
    *           what to clear
    */
   void clearContainer(String container);


   /**
    * This will delete everything inside a container recursively.
    *
    * @param container
    *           what to delete
    */
   void deleteContainer(String container);

   /**
    * Determines if a directory exists
    *
    * @param container
    *           container where the directory resides
    * @param directory
    *           full path to the directory
    */
   boolean directoryExists(String container, String directory);

   /**
    * Creates a folder or a directory marker depending on the service
    *
    * @param container
    *           container to create the directory in
    * @param directory
    *           full path to the directory
    */
   void createDirectory(String container, String directory);

   /**
    * Deletes a folder or a directory marker depending on the service
    *
    * @param container
    *           container to delete the directory from
    * @param directory
    *           full path to the directory to delete
    */
   void deleteDirectory(String containerName, String name);

   /**
    * Determines if a blob exists
    *
    * @param container
    *           container where the blob resides
    * @param directory
    *           full path to the blob
    */
   boolean blobExists(String container, String name);


   /**
    * Deletes a {@code Blob} representing the data at location {@code container/name}
    *
    * @param container
    *           container where this exists.
    * @param name
    *           fully qualified name relative to the container.
    * @throws org.jclouds.blobstore.ContainerNotFoundException
    *            if the container doesn't exist
    */
   void removeBlob(String container, String name);

   /**
    * @return a count of all blobs in the container, excluding directory markers
    */
   long countBlobs(String container);

}
