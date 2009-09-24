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

import java.io.IOException;
import java.net.URI;

import javax.annotation.Resource;

import org.jclouds.blobstore.BlobMap;
import org.jclouds.blobstore.InputStreamMap;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.lifecycle.Closer;
import org.jclouds.logging.Logger;
import org.jclouds.rackspace.Authentication;
import org.jclouds.rackspace.cloudfiles.CloudFilesBlobStore;
import org.jclouds.rackspace.cloudfiles.CloudFilesContext;
import org.jclouds.rackspace.reference.RackspaceConstants;

import javax.inject.Inject;
import com.google.inject.Injector;
import javax.inject.Named;

/**
 * Uses a Guice Injector to configure the objects served by CloudFilesContext methods.
 * 
 * @author Adrian Cole
 * @see Injector
 */
public class GuiceCloudFilesContext implements CloudFilesContext {
   public interface CloudFilesObjectMapFactory {
      BlobMap<BlobMetadata, Blob<BlobMetadata>> createMapView(String container);
   }

   public interface CloudFilesInputStreamMapFactory {
      InputStreamMap<BlobMetadata> createMapView(String container);
   }

   @Resource
   private Logger logger = Logger.NULL;
   private final Injector injector;
   private final CloudFilesInputStreamMapFactory cfInputStreamMapFactory;
   private final CloudFilesObjectMapFactory cfObjectMapFactory;
   private final Closer closer;
   private final URI endPoint;
   private final String account;

   @Inject
   private GuiceCloudFilesContext(Injector injector, Closer closer,
            CloudFilesObjectMapFactory cfObjectMapFactory,
            CloudFilesInputStreamMapFactory cfInputStreamMapFactory, @Authentication URI endPoint,
            @Named(RackspaceConstants.PROPERTY_RACKSPACE_USER) String account) {
      this.injector = injector;
      this.closer = closer;
      this.cfInputStreamMapFactory = cfInputStreamMapFactory;
      this.cfObjectMapFactory = cfObjectMapFactory;
      this.endPoint = endPoint;
      this.account = account;
   }

   /**
    * {@inheritDoc}
    * 
    * @see Closer
    */
   public void close() {
      try {
         closer.close();
      } catch (IOException e) {
         logger.error(e, "error closing content");
      }
   }

   public String getAccount() {
      return account;
   }

   public CloudFilesBlobStore getApi() {
      return injector.getInstance(CloudFilesBlobStore.class);
   }

   public URI getEndPoint() {
      return endPoint;
   }

   public BlobMap<BlobMetadata, Blob<BlobMetadata>> createBlobMap(String container) {
      getApi().createContainer(container);
      return cfObjectMapFactory.createMapView(container);
   }

   public InputStreamMap<BlobMetadata> createInputStreamMap(String container) {
      getApi().createContainer(container);
      return cfInputStreamMapFactory.createMapView(container);
   }

}
