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
package org.jclouds.blobstore;

import java.util.concurrent.Future;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ListResponse;
import org.jclouds.blobstore.domain.ListContainerResponse;
import org.jclouds.blobstore.domain.ResourceMetadata;
import org.jclouds.blobstore.options.GetOptions;
import org.jclouds.blobstore.options.ListContainerOptions;

/**
 * Provides hooks needed to run a blob store
 */
public interface BlobStore {
   
   Blob newBlob();
   
   /**
    * Lists all root-level resources available to the account.
    */
   Future<? extends ListResponse<? extends ResourceMetadata>> list();

   /**
    * Lists all resources available at the specified path. Note that path may be a container, or a
    * path within it delimited by {@code /} characters.
    * 
    * @param parent
    *           - base path to list; non-recursive
    */
   Future<? extends ListContainerResponse<? extends ResourceMetadata>> list(String container,
            ListContainerOptions... options);

   boolean exists(String container);

   Future<Boolean> createContainer(String container);

   /**
    * This will delete a container recursively.
    * 
    * @param container
    */
   Future<Void> deleteContainer(String container);

   /**
    * This will delete the contents of a container without removing it
    * 
    * @param container
    */
   Future<Void> clearContainer(String container);

   Future<String> putBlob(String container, Blob blob);

   Future<? extends Blob> getBlob(String container, String key, GetOptions... options);

   BlobMetadata blobMetadata(String container, String key);

   Future<Void> removeBlob(String container, String key);

}
