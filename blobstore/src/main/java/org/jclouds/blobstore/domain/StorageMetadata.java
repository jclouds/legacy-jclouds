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
package org.jclouds.blobstore.domain;

import java.net.URI;
import java.util.Date;
import java.util.Map;

import org.jclouds.blobstore.domain.internal.StorageMetadataImpl;
import org.jclouds.domain.ResourceMetadata;

import com.google.inject.ImplementedBy;

/**
 * Identifies containers, files, etc.
 * 
 * @author Adrian Cole
 */
@ImplementedBy(StorageMetadataImpl.class)
public interface StorageMetadata extends ResourceMetadata<StorageType> {

   /**
    * Whether this resource is a container, file, etc.
    */
   @Override
   StorageType getType();

   /**
    * Unique identifier of this resource within its enclosing namespace. In some scenarios, this id
    * is not user assignable. For files, this may be an system generated key, or the full path to
    * the resource. ex. /path/to/file.txt
    * 
    * @see org.jclouds.blobstore.attr.ContainerCapability#CONTAINER_METADATA
    */
   @Override
   String getProviderId();

   /**
    * Name of this resource. Names are dictated by the user. For files, this may be the filename,
    * ex. file.txt
    * 
    */
   @Override
   String getName();

   /**
    * URI used to access this resource
    */
   @Override
   URI getUri();

   /**
    * Any key-value pairs associated with the resource.
    * 
    * @see org.jclouds.blobstore.attr.ContainerCapability#CONTAINER_METADATA
    * @see org.jclouds.blobstore.attr.ContainerCapability#BLOB_METADATA
    */
   @Override
   Map<String, String> getUserMetadata();

   /**
    * The eTag value stored in the Etag header returned by HTTP.
    * 
    * @see org.jclouds.blobstore.attr.ContainerCapability#CONTAINER_ETAG
    * @see org.jclouds.blobstore.attr.ContainerCapability#BLOB_ETAG
    */
   String getETag();

   /**
    * Last modification time of the resource
    * 
    * @see org.jclouds.blobstore.attr.ContainerCapability#CONTAINER_LAST_MODIFIED
    * @see org.jclouds.blobstore.attr.ContainerCapability#BLOB_LAST_MODIFIED
    * @see org.jclouds.blobstore.attr.ContainerCapability#MILLISECOND_PRECISION
    */
   Date getLastModified();

}
