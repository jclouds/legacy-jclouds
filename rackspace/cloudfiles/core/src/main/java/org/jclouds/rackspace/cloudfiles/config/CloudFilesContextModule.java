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
package org.jclouds.rackspace.cloudfiles.config;

import java.net.URI;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import org.jclouds.blobstore.BlobStoreContextImpl;
import org.jclouds.blobstore.BlobMap.Factory;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.lifecycle.Closer;
import org.jclouds.rackspace.CloudFiles;
import org.jclouds.rackspace.cloudfiles.CloudFilesBlobStore;
import org.jclouds.rackspace.cloudfiles.CloudFilesContext;
import org.jclouds.rackspace.cloudfiles.domain.ContainerMetadata;
import org.jclouds.rackspace.reference.RackspaceConstants;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class CloudFilesContextModule extends AbstractModule {

   @Override
   protected void configure() {
      bind(CloudFilesContext.class).to(CloudFilesContextImpl.class).in(Scopes.SINGLETON);
   }

   public static class CloudFilesContextImpl
            extends
            BlobStoreContextImpl<CloudFilesBlobStore, ContainerMetadata, BlobMetadata, Blob<BlobMetadata>>
            implements CloudFilesContext {
      @Inject
      CloudFilesContextImpl(Factory<BlobMetadata, Blob<BlobMetadata>> blobMapFactory,
               org.jclouds.blobstore.InputStreamMap.Factory<BlobMetadata> inputStreamMapFactory,
               Closer closer, Provider<Blob<BlobMetadata>> blobProvider,
               CloudFilesBlobStore defaultApi, @CloudFiles URI endPoint,
               @Named(RackspaceConstants.PROPERTY_RACKSPACE_USER) String account) {
         super(blobMapFactory, inputStreamMapFactory, closer, blobProvider, defaultApi, endPoint,
                  account);
      }

   }
}
