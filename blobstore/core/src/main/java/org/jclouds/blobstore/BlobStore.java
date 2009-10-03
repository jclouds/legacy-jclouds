/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
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

import java.util.SortedSet;
import java.util.concurrent.Future;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ContainerMetadata;
import org.jclouds.http.options.GetOptions;

/**
 * Provides hooks needed to run a blob store
 */
public interface BlobStore<C extends ContainerMetadata, M extends BlobMetadata, B extends Blob<M>> {
   SortedSet<C> listContainers();

   boolean containerExists(String container);

   Future<Boolean> createContainer(String container);

   /**
    * if supported, this will delete a container recursively. Otherwise, it will return false, if
    * the container could not be deleted because it is not empty.
    * 
    * @param container
    * @return false if container cannot be deleted because it is not empty
    */
   Future<Boolean> deleteContainer(String container);

   Future<? extends SortedSet<M>> listBlobs(String container);

   Future<byte[]> putBlob(String container, B blob);

   Future<B> getBlob(String container, String key);

   Future<B> getBlob(String container, String key, GetOptions options);

   M blobMetadata(String container, String key);

   Future<Boolean> removeBlob(String container, String key);

}
