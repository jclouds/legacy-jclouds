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
package org.jclouds.walrus;

import java.util.List;

import org.jclouds.providers.ProviderMetadata;
import org.jclouds.s3.S3AsyncClient;
import org.jclouds.s3.S3Client;
import org.jclouds.s3.S3ContextBuilder;
import org.jclouds.s3.blobstore.S3BlobStoreContext;
import org.jclouds.walrus.config.WalrusRestClientModule;

import com.google.inject.Module;

/**
 * 
 * 
 * @author Adrian Cole
 */
public class WalrusContextBuilder extends
      S3ContextBuilder<S3Client, S3AsyncClient, S3BlobStoreContext<S3Client, S3AsyncClient>, WalrusApiMetadata> {
   public WalrusContextBuilder() {
      this(new WalrusApiMetadata());
   }

   public WalrusContextBuilder(
         ProviderMetadata<S3Client, S3AsyncClient, S3BlobStoreContext<S3Client, S3AsyncClient>, WalrusApiMetadata> providerMetadata) {
      super(providerMetadata);
   }

   public WalrusContextBuilder(WalrusApiMetadata apiMetadata) {
      super(apiMetadata);
   }

   @Override
   protected void addClientModule(List<Module> modules) {
      modules.add(new WalrusRestClientModule());
   }
}
