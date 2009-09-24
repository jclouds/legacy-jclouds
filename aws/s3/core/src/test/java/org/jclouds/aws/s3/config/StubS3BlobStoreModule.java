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

import org.jclouds.aws.s3.S3BlobStore;
import org.jclouds.aws.s3.internal.StubS3BlobStore;
import org.jclouds.cloud.ConfiguresCloudConnection;
import org.jclouds.http.functions.config.ParserModule;

import com.google.inject.AbstractModule;

/**
 * adds a stub alternative to invoking S3
 * 
 * @author Adrian Cole
 */
@ConfiguresCloudConnection
public class StubS3BlobStoreModule extends AbstractModule {
   protected void configure() {
      install(new ParserModule());
      bind(S3BlobStore.class).to(StubS3BlobStore.class);
      bind(URI.class).toInstance(URI.create("http://localhost:8080"));
   }
}
