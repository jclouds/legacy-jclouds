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
package org.jclouds.rackspace.cloudfiles.integration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.integration.internal.StubBlobStore;
import org.jclouds.cloud.ConfiguresCloudConnection;
import org.jclouds.rackspace.cloudfiles.CloudFilesConnection;
import org.jclouds.rackspace.cloudfiles.domain.ContainerMetadata;
import org.jclouds.rackspace.cloudfiles.internal.StubCloudFilesConnection;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

@ConfiguresCloudConnection
public class StubCloudFilesBlobStoreModule extends AbstractModule {
   // must be singleton for all threads and all objects or tests may fail;
   static final ConcurrentHashMap<String, Map<String, Blob<BlobMetadata>>> map = new ConcurrentHashMap<String, Map<String, Blob<BlobMetadata>>>();

   @Override
   protected void configure() {
      bind(new TypeLiteral<Map<String, Map<String, Blob<BlobMetadata>>>>() {
      }).toInstance(map);
      bind(new TypeLiteral<BlobStore<ContainerMetadata, BlobMetadata, Blob<BlobMetadata>>>() {
      }).to(new TypeLiteral<StubBlobStore<ContainerMetadata, BlobMetadata, Blob<BlobMetadata>>>() {
      }).asEagerSingleton();
      bind(new TypeLiteral<CloudFilesConnection>() {
      }).to(new TypeLiteral<StubCloudFilesConnection>() {
      }).asEagerSingleton();
   }

}