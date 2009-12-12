/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.blobstore.domain;

import java.net.URI;
import java.util.Map;

import org.jclouds.blobstore.domain.internal.MutableResourceMetadataImpl;
import org.joda.time.DateTime;

import com.google.inject.ImplementedBy;

/**
 * Identifies containers, files, etc.
 * 
 * @author Adrian Cole
 */
@ImplementedBy(MutableResourceMetadataImpl.class)
public interface MutableResourceMetadata extends ResourceMetadata {
   void setId(String id);

   void setLocation(URI url);

   /**
    * Whether this resource is a container, file, etc.
    */
   void setType(ResourceType type);

   /**
    * Name of this resource. Names are dictated by the user. For files, this may be the filename,
    * ex. file.txt
    * 
    */
   void setName(String name);

   /**
    * The eTag value stored in the Etag header returned by HTTP.
    * 
    * @see org.jclouds.blobstore.attr.ContainerCapability#CONTAINER_ETAG
    * @see org.jclouds.blobstore.attr.ContainerCapability#BLOB_ETAG
    */
   void setETag(String eTag);

   /**
    * Size of the resource in bytes
    * 
    * @see org.jclouds.blobstore.attr.ContainerCapability#CONTAINER_SIZE
    * @see org.jclouds.blobstore.attr.ContainerCapability#BLOB_SIZE
    */
   void setSize(long size);

   /**
    * Last modification time of the resource
    * 
    * @see org.jclouds.blobstore.attr.ContainerCapability#CONTAINER_LAST_MODIFIED
    * @see org.jclouds.blobstore.attr.ContainerCapability#BLOB_LAST_MODIFIED
    * @see org.jclouds.blobstore.attr.ContainerCapability#MILLISECOND_PRECISION
    */
   void setLastModified(DateTime lastModified);

   /**
    * Any key-value pairs associated with the resource.
    * 
    * @see org.jclouds.blobstore.attr.ContainerCapability#CONTAINER_METADATA
    * @see org.jclouds.blobstore.attr.ContainerCapability#BLOB_METADATA
    */
   void setUserMetadata(Map<String, String> userMetadata);

}