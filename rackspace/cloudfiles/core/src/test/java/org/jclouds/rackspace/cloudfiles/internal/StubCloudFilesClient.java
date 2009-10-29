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
package org.jclouds.rackspace.cloudfiles.internal;

import java.util.Map;
import java.util.SortedSet;
import java.util.concurrent.Future;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.BoundedSortedSet;
import org.jclouds.http.options.GetOptions;
import org.jclouds.rackspace.cloudfiles.CloudFilesClient;
import org.jclouds.rackspace.cloudfiles.domain.AccountMetadata;
import org.jclouds.rackspace.cloudfiles.domain.ContainerCDNMetadata;
import org.jclouds.rackspace.cloudfiles.domain.ContainerMetadata;
import org.jclouds.rackspace.cloudfiles.options.ListCdnContainerOptions;
import org.jclouds.rackspace.cloudfiles.options.ListContainerOptions;

/**
 * Implementation of {@link CloudFilesClient} which keeps all data in a local Map object.
 * 
 * @author Adrian Cole
 */
public class StubCloudFilesClient implements CloudFilesClient {

   public boolean containerExists(String container) {
      throw new UnsupportedOperationException();
   }

   public Future<Boolean> createContainer(String container) {
      throw new UnsupportedOperationException();
   }

   public Future<Boolean> deleteContainerIfEmpty(String container) {
      throw new UnsupportedOperationException();
   }

   public boolean disableCDN(String container) {
      throw new UnsupportedOperationException();
   }

   public String enableCDN(String container, Long ttl) {
      throw new UnsupportedOperationException();
   }

   public String enableCDN(String container) {
      throw new UnsupportedOperationException();
   }

   public AccountMetadata getAccountStatistics() {
      throw new UnsupportedOperationException();
   }

   public ContainerCDNMetadata getCDNMetadata(String container) {
      throw new UnsupportedOperationException();
   }

   public Future<Blob> getObject(String container, String key, GetOptions... options) {
      throw new UnsupportedOperationException();
   }

   public BlobMetadata getObjectMetadata(String container, String key) {
      throw new UnsupportedOperationException();
   }

   public SortedSet<ContainerCDNMetadata> listCDNContainers(ListCdnContainerOptions... options) {
      throw new UnsupportedOperationException();
   }

   public SortedSet<ContainerMetadata> listContainers(ListContainerOptions... options) {
      throw new UnsupportedOperationException();
   }

   public Future<BoundedSortedSet<BlobMetadata>> listObjects(String container,
            ListContainerOptions... options) {
      throw new UnsupportedOperationException();
   }

   public Blob newBlob() {
      throw new UnsupportedOperationException();
   }

   public Future<String> putObject(String container, Blob object) {
      throw new UnsupportedOperationException();
   }

   public Future<Void> removeObject(String container, String key) {
      throw new UnsupportedOperationException();
   }

   public boolean setObjectMetadata(String container, String key, Map<String, String> userMetadata) {
      throw new UnsupportedOperationException();
   }

   public String updateCDN(String container, Long ttl) {
      throw new UnsupportedOperationException();
   }

}
