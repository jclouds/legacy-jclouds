/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.aws.s3.config;

import static org.jclouds.aws.domain.Region.US_STANDARD;
import static org.jclouds.reflect.Reflection2.typeToken;

import javax.inject.Singleton;

import org.jclouds.aws.s3.AWSS3AsyncClient;
import org.jclouds.aws.s3.AWSS3Client;
import org.jclouds.aws.s3.filters.AWSRequestAuthorizeSignature;
import org.jclouds.aws.s3.predicates.validators.AWSS3BucketNameValidator;
import org.jclouds.location.Region;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.s3.S3AsyncClient;
import org.jclouds.s3.S3Client;
import org.jclouds.s3.config.S3RestClientModule;
import org.jclouds.s3.filters.RequestAuthorizeSignature;
import org.jclouds.s3.predicates.validators.BucketNameValidator;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.inject.Provides;
import com.google.inject.Scopes;

/**
 * Configures the S3 connection.
 *
 * @author Adrian Cole
 */
@ConfiguresRestClient
public class AWSS3RestClientModule extends S3RestClientModule<AWSS3Client, AWSS3AsyncClient> {
   
   public AWSS3RestClientModule() {
      super(typeToken(AWSS3Client.class), typeToken(AWSS3AsyncClient.class));
   }
   
   @Override
   protected Supplier<String> defaultRegionForBucket(@Region Supplier<String> defaultRegion) {
      return Suppliers.ofInstance(US_STANDARD);
   }
   
   @Override
   protected void configure() {
      bind(BucketNameValidator.class).to(AWSS3BucketNameValidator.class);
      super.configure();
   }

   @Override
   protected void bindRequestSigner() {
      bind(RequestAuthorizeSignature.class).to(AWSRequestAuthorizeSignature.class).in(Scopes.SINGLETON);
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
