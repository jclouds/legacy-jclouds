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
package org.jclouds.aws.s3.internal;

import org.jclouds.aws.s3.AWSS3Client;
import org.jclouds.aws.s3.AWSS3ProviderMetadata;
import org.jclouds.aws.s3.config.AWSS3RestClientModule;
import org.jclouds.date.TimeStamp;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.internal.BaseRestClientExpectTest;

import com.google.common.base.Supplier;
import com.google.inject.Module;

/**
 * Base class for writing Expect tests for AWS-S3
 *
 * @author Andrei Savu
 */
public class BaseAWSS3ClientExpectTest extends BaseRestClientExpectTest<AWSS3Client> {

   protected static final String CONSTANT_DATE = "2009-11-08T15:54:08.897Z";
   

   public BaseAWSS3ClientExpectTest() {
      provider = "aws-s3";
   }
   
   @Override
   public ProviderMetadata createProviderMetadata() {
      return new AWSS3ProviderMetadata();
   }

      @ConfiguresRestClient
   private static final class TestAWSS3RestClientModule extends AWSS3RestClientModule {
      @Override
      protected String provideTimeStamp(@TimeStamp Supplier<String> cache) {
         return CONSTANT_DATE;
      }
   }

   @Override
   protected Module createModule() {
      return new TestAWSS3RestClientModule();
   }

}
