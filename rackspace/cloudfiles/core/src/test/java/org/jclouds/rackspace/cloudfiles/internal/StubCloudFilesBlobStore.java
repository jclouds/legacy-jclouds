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
package org.jclouds.rackspace.cloudfiles.internal;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.integration.internal.StubBlobStore;
import org.jclouds.rackspace.cloudfiles.CloudFilesBlobStore;
import org.jclouds.rackspace.cloudfiles.domain.AccountMetadata;
import org.jclouds.rackspace.cloudfiles.domain.ContainerCDNMetadata;
import org.jclouds.rackspace.cloudfiles.domain.ContainerMetadata;
import org.jclouds.rackspace.cloudfiles.options.ListCdnContainerOptions;
import org.jclouds.rackspace.cloudfiles.options.ListContainerOptions;

import com.google.common.collect.Multimap;

/**
 * Implementation of {@link CloudFilesBlobStore} which keeps all data in a local Map object.
 * 
 * @author Adrian Cole
 * @author James Murty
 */
public class StubCloudFilesBlobStore extends
         StubBlobStore<ContainerMetadata, BlobMetadata, Blob<BlobMetadata>> implements
         CloudFilesBlobStore {

   @Override
   protected Blob<BlobMetadata> createBlob(String name) {
      return new Blob<BlobMetadata>(name);
   }

   @Override
   protected Blob<BlobMetadata> createBlob(BlobMetadata metadata) {
      return new Blob<BlobMetadata>(metadata);
   }

   @Override
   protected ContainerMetadata createContainerMetadata(String name) {
      return new ContainerMetadata(name);
   }

   /**
    * note this must be final and static so that tests coming from multiple threads will pass.
    */
   private static final Map<String, Map<String, Blob<BlobMetadata>>> containerToBlobs = new ConcurrentHashMap<String, Map<String, Blob<BlobMetadata>>>();

   @Override
   public Map<String, Map<String, Blob<BlobMetadata>>> getContainerToBlobs() {
      return containerToBlobs;
   }

   public AccountMetadata getAccountMetadata() {
      return null;
   }

   public List<ContainerMetadata> listContainers(ListContainerOptions options) {
      return null;
   }

   public boolean setObjectMetadata(String container, String key,
            Multimap<String, String> userMetadata) {
      return false;
   }

   public boolean disableCDN(String container) {
      return false;
   }

   public String enableCDN(String container, Long ttl) {
      return null;
   }

   public String enableCDN(String container) {
      return null;
   }

   public ContainerCDNMetadata getCDNMetadata(String container) {
      return null;
   }

   public List<ContainerCDNMetadata> listCDNContainers(ListCdnContainerOptions... options) {
      return null;
   }

   public String updateCDN(String container, Long ttl) {
      return null;
   }

}
