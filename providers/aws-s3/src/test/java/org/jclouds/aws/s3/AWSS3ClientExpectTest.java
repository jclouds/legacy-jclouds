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

import static org.jclouds.aws.s3.blobstore.options.AWSS3PutObjectOptions.Builder.storageClass;

import java.net.URI;

import org.jclouds.aws.s3.internal.BaseAWSS3ClientExpectTest;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobBuilder;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.io.payloads.StringPayload;
import org.jclouds.s3.blobstore.functions.BlobToObject;
import org.jclouds.s3.domain.ObjectMetadata.StorageClass;
import org.testng.annotations.Test;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.inject.Injector;

/**
 * @author Andrei Savu
 */
@Test
public class AWSS3ClientExpectTest extends BaseAWSS3ClientExpectTest {

   @Test
   public void testPutWithReducedRedundancy() {
      Injector injector = createInjector(Functions.forMap(ImmutableMap.<HttpRequest, HttpResponse>of()), createModule(), setupProperties());

      Blob blob = injector.getInstance(BlobBuilder.class).name("test").payload("content").build();
      BlobToObject blobToObject = injector.getInstance(BlobToObject.class);
      
      AWSS3Client client = requestSendsResponse(
         HttpRequest.builder()
            .method("PUT")
            .endpoint(URI.create("https://test.s3.amazonaws.com/test"))
            .headers(ImmutableMultimap.of(
               "x-amz-storage-class", "REDUCED_REDUNDANCY",
               "Host", "test.s3.amazonaws.com",
               "Date", CONSTANT_DATE,
               "Authorization", "AWS identity:1mJrW85/mqZpYTFIK5Ebtt2MM6E="
            ))
            .payload(new StringPayload("content"))
            .build(),
         HttpResponse.builder()
            .statusCode(200)
            .headers(ImmutableMultimap.of(
               "x-amz-id-2", "w0rL+9fALQiCOToesVQefs8WalIgn+ZhMD7hHMKYud/xv7MyKkAWQOtFNEfK97Ri",
               "x-amz-request-id", "7A84C3CD4437A4C0",
               "Date", CONSTANT_DATE,
               "ETag", "437b930db84b8079c2dd804a71936b5f",
               "Server", "AmazonS3"
            ))
            .build()
      );

      client.putObject("test", blobToObject.apply(blob),
         storageClass(StorageClass.REDUCED_REDUNDANCY));
   }
}
