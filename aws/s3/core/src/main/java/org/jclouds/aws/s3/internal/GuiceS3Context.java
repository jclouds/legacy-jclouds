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
package org.jclouds.aws.s3.internal;

import java.io.IOException;
import java.net.URI;

import javax.annotation.Resource;

import org.jclouds.aws.s3.S3BlobStore;
import org.jclouds.aws.s3.S3Context;
import org.jclouds.aws.s3.domain.ObjectMetadata;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.aws.s3.reference.S3Constants;
import org.jclouds.blobstore.BlobMap;
import org.jclouds.blobstore.InputStreamMap;
import org.jclouds.lifecycle.Closer;
import org.jclouds.logging.Logger;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;

/**
 * Uses a Guice Injector to configure the objects served by S3Context methods.
 * 
 * @author Adrian Cole
 * @see Injector
 */
public class GuiceS3Context implements S3Context {
   public interface S3ObjectMapFactory {
      BlobMap<ObjectMetadata, S3Object> createMapView(String bucket);
   }

   public interface S3InputStreamMapFactory {
      InputStreamMap<ObjectMetadata> createMapView(String bucket);
   }

   @Resource
   private Logger logger = Logger.NULL;
   private final Injector injector;
   private final S3InputStreamMapFactory s3InputStreamMapFactory;
   private final S3ObjectMapFactory s3ObjectMapFactory;
   private final Closer closer;
   private final String account;
   private final URI endPoint;

   @Inject
   private GuiceS3Context(Injector injector, Closer closer, S3ObjectMapFactory s3ObjectMapFactory,
            S3InputStreamMapFactory s3InputStreamMapFactory,
            @Named(S3Constants.PROPERTY_AWS_ACCESSKEYID) String accessKey, URI endPoint) {
      this.injector = injector;
      this.s3InputStreamMapFactory = s3InputStreamMapFactory;
      this.s3ObjectMapFactory = s3ObjectMapFactory;
      this.closer = closer;
      this.account = accessKey;
      this.endPoint = endPoint;
   }

   /**
    * {@inheritDoc}
    */
   public S3BlobStore getApi() {
      return injector.getInstance(S3BlobStore.class);
   }

   /**
    * {@inheritDoc}
    */
   public InputStreamMap<ObjectMetadata> createInputStreamMap(String bucket) {
      getApi().createContainer(bucket);
      return s3InputStreamMapFactory.createMapView(bucket);
   }

   /**
    * {@inheritDoc}
    */
   public BlobMap<ObjectMetadata, S3Object> createBlobMap(String bucket) {
      getApi().createContainer(bucket);
      return s3ObjectMapFactory.createMapView(bucket);
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

   @Override
   public String toString() {
      return "GuiceS3Context [account=" + account + ", endPoint=" + endPoint + "]";
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((account == null) ? 0 : account.hashCode());
      result = prime * result + ((endPoint == null) ? 0 : endPoint.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      GuiceS3Context other = (GuiceS3Context) obj;
      if (account == null) {
         if (other.account != null)
            return false;
      } else if (!account.equals(other.account))
         return false;
      if (endPoint == null) {
         if (other.endPoint != null)
            return false;
      } else if (!endPoint.equals(other.endPoint))
         return false;
      return true;
   }

   public String getAccount() {
      return account;
   }

   public URI getEndPoint() {
      return endPoint;
   }

}
