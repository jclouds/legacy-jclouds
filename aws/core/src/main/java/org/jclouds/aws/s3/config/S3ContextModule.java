/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.aws.s3.config;

import java.net.URI;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.aws.reference.AWSConstants;
import org.jclouds.aws.s3.S3;
import org.jclouds.aws.s3.S3AsyncClient;
import org.jclouds.aws.s3.S3Client;
import org.jclouds.blobstore.config.BlobStoreObjectModule;
import org.jclouds.lifecycle.Closer;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.RestContextImpl;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * Configures the {@link S3ContextModule}; requires {@link S3AsyncClient} bound.
 * 
 * @author Adrian Cole
 */
public class S3ContextModule extends AbstractModule {

   @Override
   protected void configure() {
      // for converters
      install(new BlobStoreObjectModule());
      install(new S3ObjectModule());
   }

   @Provides
   @Singleton
   RestContext<S3AsyncClient, S3Client> provideContext(Closer closer, S3AsyncClient defaultApi,
            S3Client syncApi, @S3 URI endPoint,
            @Named(AWSConstants.PROPERTY_AWS_ACCESSKEYID) String account) {
      return new RestContextImpl<S3AsyncClient, S3Client>(closer, defaultApi, syncApi, endPoint,
               account);
   }

}
