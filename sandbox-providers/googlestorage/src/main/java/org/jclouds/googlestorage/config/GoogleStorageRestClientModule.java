/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.googlestorage.config;

import com.google.common.reflect.TypeToken;
import com.google.inject.Provides;
import org.jclouds.googlestorage.GoogleStorageAsyncClient;
import org.jclouds.googlestorage.binders.BindGoogleStorageObjectMetadataToRequest;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.s3.S3AsyncClient;
import org.jclouds.s3.S3Client;
import org.jclouds.s3.binders.BindS3ObjectMetadataToRequest;
import org.jclouds.s3.config.S3RestClientModule;

import javax.inject.Singleton;

/**
 * @author Adrian Cole
 */
@ConfiguresRestClient
public class GoogleStorageRestClientModule extends S3RestClientModule<S3Client, GoogleStorageAsyncClient> {

   public GoogleStorageRestClientModule() {
      super(TypeToken.class.cast(TypeToken.of(S3Client.class)), TypeToken.class.cast(TypeToken.of
              (GoogleStorageAsyncClient.class)));
   }

   @Override
   protected void configure() {
      bind(BindS3ObjectMetadataToRequest.class).to(BindGoogleStorageObjectMetadataToRequest.class);
      super.configure();
   }

   @Provides
   @Singleton
   S3AsyncClient provideS3AsyncClient(GoogleStorageAsyncClient in) {
      return in;
   }
}
