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
package org.jclouds.s3.internal;

import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.internal.BaseAsyncClientTest;
import org.jclouds.s3.S3ApiMetadata;
import org.jclouds.s3.S3AsyncClient;
import org.jclouds.s3.blobstore.functions.BlobToObject;
import org.jclouds.s3.filters.RequestAuthorizeSignature;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public abstract class BaseS3AsyncClientTest<T extends S3AsyncClient> extends BaseAsyncClientTest<T> {

   protected BlobToObject blobToS3Object;
   protected RequestAuthorizeSignature filter;

   @Override
   protected void checkFilters(HttpRequest request) {
      assertEquals(request.getFilters().size(), 1);
      assertEquals(request.getFilters().get(0).getClass(), RequestAuthorizeSignature.class);
   }


   @BeforeClass
   @Override
   protected void setupFactory() throws IOException {
      super.setupFactory();
      blobToS3Object = injector.getInstance(BlobToObject.class);
      filter = injector.getInstance(RequestAuthorizeSignature.class);
   }

   public BaseS3AsyncClientTest() {
      super();
   }

   @Override
   public S3ApiMetadata createApiMetadata() {
      return new S3ApiMetadata();
   }

}
