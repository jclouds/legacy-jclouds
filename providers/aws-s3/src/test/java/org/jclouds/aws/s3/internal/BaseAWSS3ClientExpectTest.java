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
package org.jclouds.aws.s3.internal;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import org.jclouds.aws.s3.AWSS3Client;
import org.jclouds.aws.s3.config.AWSS3RestClientModule;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.BlobStoreContextFactory;
import org.jclouds.date.TimeStamp;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.RequiresHttp;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.rest.BaseRestClientExpectTest;
import org.jclouds.rest.ConfiguresRestClient;

import java.util.Properties;

/**
 * Base class for writing Expect tests for AWS-S3
 *
 * @author Andrei Savu
 */
public class BaseAWSS3ClientExpectTest extends BaseRestClientExpectTest<AWSS3Client> {

   protected static final String CONSTANT_DATE = "2009-11-08T15:54:08.897Z";

   protected BlobStoreContext blobStoreContext;
   protected BlobStore blobStore;

   public BaseAWSS3ClientExpectTest() {
      provider = "aws-s3";
   }

   @RequiresHttp
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

   @Override
   public AWSS3Client createClient(Function<HttpRequest, HttpResponse> fn, Module module, Properties props) {
      return clientFrom(BlobStoreContext.class.cast(new BlobStoreContextFactory(setupRestProperties())
         .createContext(provider, "identity", "credential", ImmutableSet.<Module>of(new ExpectModule(fn),
            new NullLoggingModule(), module), props)));
   }

   protected AWSS3Client clientFrom(BlobStoreContext context) {
      blobStoreContext = context;
      blobStore = context.getBlobStore();
      return AWSS3Client.class.cast(context.getProviderSpecificContext().getApi());
   }

}
