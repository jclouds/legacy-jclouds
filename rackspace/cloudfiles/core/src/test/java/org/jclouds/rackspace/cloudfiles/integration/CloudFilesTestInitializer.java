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

import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.integration.internal.BaseTestInitializer;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rackspace.StubRackspaceAuthenticationModule;
import org.jclouds.rackspace.cloudfiles.CloudFilesBlobStore;
import org.jclouds.rackspace.cloudfiles.CloudFilesContextBuilder;
import org.jclouds.rackspace.cloudfiles.domain.ContainerMetadata;

import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
public class CloudFilesTestInitializer
         extends
         BaseTestInitializer<CloudFilesBlobStore, ContainerMetadata, BlobMetadata, Blob<BlobMetadata>> {

   protected BlobStoreContext<CloudFilesBlobStore, ContainerMetadata, BlobMetadata, Blob<BlobMetadata>> createStubContext() {
      return new CloudFilesContextBuilder("foo", "bar").withModules(
               new StubCloudFilesConnectionModule(), new StubRackspaceAuthenticationModule())
               .buildContext();
   }

   protected BlobStoreContext<CloudFilesBlobStore, ContainerMetadata, BlobMetadata, Blob<BlobMetadata>> createLiveContext(
            Module configurationModule, String url, String app, String account, String key) {
      return new CloudFilesContextBuilder(account, key).relaxSSLHostname().withModules(
               configurationModule, new Log4JLoggingModule()).buildContext();
   }

}