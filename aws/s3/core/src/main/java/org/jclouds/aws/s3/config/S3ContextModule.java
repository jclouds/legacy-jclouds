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
package org.jclouds.aws.s3.config;

import java.net.URI;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import org.jclouds.aws.reference.AWSConstants;
import org.jclouds.aws.s3.S3;
import org.jclouds.aws.s3.S3BlobStore;
import org.jclouds.aws.s3.S3Context;
import org.jclouds.aws.s3.domain.BucketMetadata;
import org.jclouds.aws.s3.domain.ObjectMetadata;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.blobstore.BlobStoreContextImpl;
import org.jclouds.blobstore.BlobMap.Factory;
import org.jclouds.lifecycle.Closer;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

/**
 * Configures the {@link S3Context}; requires {@link S3BlobStore} bound.
 * 
 * @author Adrian Cole
 */
public class S3ContextModule extends AbstractModule {

   @Override
   protected void configure() {
      bind(S3Context.class).to(S3ContextImpl.class).in(Scopes.SINGLETON);
   }

   public static class S3ContextImpl extends
            BlobStoreContextImpl<S3BlobStore, BucketMetadata, ObjectMetadata, S3Object> implements
            S3Context {
      @Inject
      S3ContextImpl(Factory<ObjectMetadata, S3Object> blobMapFactory,
               org.jclouds.blobstore.InputStreamMap.Factory<ObjectMetadata> inputStreamMapFactory,
               Closer closer, Provider<S3Object> blobProvider, S3BlobStore defaultApi,
               @S3 URI endPoint, @Named(AWSConstants.PROPERTY_AWS_ACCESSKEYID) String account) {
         super(blobMapFactory, inputStreamMapFactory, closer, blobProvider, defaultApi, endPoint,
                  account);
      }

   }

}
