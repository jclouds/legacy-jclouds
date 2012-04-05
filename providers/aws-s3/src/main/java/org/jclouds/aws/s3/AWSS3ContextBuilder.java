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
package org.jclouds.aws.s3;

import java.util.List;

import org.jclouds.aws.s3.blobstore.AWSS3BlobStoreContext;
import org.jclouds.aws.s3.blobstore.config.AWSS3BlobStoreContextModule;
import org.jclouds.aws.s3.config.AWSS3RestClientModule;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.s3.S3ContextBuilder;

import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
public class AWSS3ContextBuilder extends
      S3ContextBuilder<AWSS3Client, AWSS3AsyncClient, AWSS3BlobStoreContext, AWSS3ApiMetadata> {
   public AWSS3ContextBuilder() {
      this(new AWSS3ProviderMetadata());
   }

   public AWSS3ContextBuilder(
         ProviderMetadata<AWSS3Client, AWSS3AsyncClient, AWSS3BlobStoreContext, AWSS3ApiMetadata> providerMetadata) {
      super(providerMetadata);
   }

   public AWSS3ContextBuilder(AWSS3ApiMetadata apiMetadata) {
      super(apiMetadata);
   }

   @Override
   protected void addContextModule(List<Module> modules) {
      modules.add(new AWSS3BlobStoreContextModule());
   }

   @Override
   protected void addClientModule(List<Module> modules) {
      modules.add(new AWSS3RestClientModule());
   }

}
