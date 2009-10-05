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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jclouds.aws.s3.S3;
import org.jclouds.aws.s3.S3Connection;
import org.jclouds.aws.s3.domain.BucketMetadata;
import org.jclouds.aws.s3.domain.ObjectMetadata;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.aws.s3.internal.StubS3Connection;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.integration.internal.StubBlobStore;
import org.jclouds.cloud.ConfiguresCloudConnection;
import org.jclouds.http.functions.config.ParserModule;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

/**
 * adds a stub alternative to invoking S3
 * 
 * @author Adrian Cole
 */
@ConfiguresCloudConnection
public class StubS3BlobStoreModule extends AbstractModule {
   static final ConcurrentHashMap<String, Map<String, S3Object>> map = new ConcurrentHashMap<String, Map<String, S3Object>>();

   protected void configure() {
      install(new ParserModule());
      bind(new TypeLiteral<Map<String, Map<String, S3Object>>>() {
      }).toInstance(map);
      bind(new TypeLiteral<BlobStore<BucketMetadata, ObjectMetadata, S3Object>>() {
      }).to(new TypeLiteral<StubBlobStore<BucketMetadata, ObjectMetadata, S3Object>>() {
      }).asEagerSingleton();
      bind(S3Connection.class).to(StubS3Connection.class).asEagerSingleton();
      bind(URI.class).annotatedWith(S3.class).toInstance(URI.create("https://localhost/s3stub"));
   }
}
