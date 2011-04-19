/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import static org.jclouds.aws.domain.Region.US_STANDARD;
import static org.jclouds.location.reference.LocationConstants.ENDPOINT;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGION;

import java.net.URI;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.aws.s3.AWSS3AsyncClient;
import org.jclouds.aws.s3.AWSS3Client;
import org.jclouds.aws.s3.binders.AssignCorrectHostnameAndBindAsHostPrefixIfConfigured;
import org.jclouds.http.RequiresHttp;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.s3.Bucket;
import org.jclouds.s3.S3AsyncClient;
import org.jclouds.s3.S3Client;
import org.jclouds.s3.binders.BindAsHostPrefixIfConfigured;
import org.jclouds.s3.config.S3RestClientModule;

import com.google.inject.Provides;

/**
 * Configures the S3 connection.
 * 
 * @author Adrian Cole
 */
@RequiresHttp
@ConfiguresRestClient
public class AWSS3RestClientModule extends S3RestClientModule<AWSS3Client, AWSS3AsyncClient> {

   @Provides
   @Singleton
   @Bucket
   protected URI provideLocationURI(@Named(PROPERTY_REGION + "." + US_STANDARD + "." + ENDPOINT) String endpoint) {
      return URI.create(endpoint);
   }

   @Override
   protected void configure() {
      bind(BindAsHostPrefixIfConfigured.class).to(AssignCorrectHostnameAndBindAsHostPrefixIfConfigured.class);
      super.configure();
   }

   public AWSS3RestClientModule() {
      super(AWSS3Client.class, AWSS3AsyncClient.class);
   }

   @Singleton
   @Provides
   S3Client provide(AWSS3Client in) {
      return in;
   }

   @Singleton
   @Provides
   S3AsyncClient provide(AWSS3AsyncClient in) {
      return in;
   }

}
