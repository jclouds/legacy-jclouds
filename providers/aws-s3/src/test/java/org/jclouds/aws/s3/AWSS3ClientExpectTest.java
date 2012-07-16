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

import org.jclouds.aws.s3.internal.BaseAWSS3ClientExpectTest;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobBuilder;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.s3.blobstore.functions.BlobToObject;
import org.jclouds.s3.domain.ObjectMetadata.StorageClass;
import org.testng.annotations.Test;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Injector;

/**
 * @author Andrei Savu
 */
@Test
public class AWSS3ClientExpectTest extends BaseAWSS3ClientExpectTest {
   HttpRequest bucketLocationRequest = HttpRequest.builder()
                                                  .method("GET")
                                                  .endpoint("https://test.s3.amazonaws.com/?location")
                                                  .addHeader("Host", "test.s3.amazonaws.com")
                                                  .addHeader("Date", CONSTANT_DATE)
                                                  .addHeader("Authorization", "AWS identity:D1rymKrEdvzvhmZXeg+Z0R+tiug=").build();
   
   HttpResponse bucketLocationResponse = HttpResponse.builder()
                                                     .statusCode(200)
                                                     .payload(payloadFromStringWithContentType("<LocationConstraint xmlns=\"http://s3.amazonaws.com/doc/2006-03-01/\">eu-west-1</LocationConstraint>", "application/xml"))
                                                     .addHeader("x-amz-id-2", "BtioT9wIK04YkE2DPgWUrQFiAbjwJVP8cLyfOkJ1FHMbn2hVjBZvkMMuXPDHfGVw")
                                                     .addHeader("x-amz-request-id", "51BF4F45D49B1B34")
                                                     .addHeader("Date", CONSTANT_DATE)
                                                     .addHeader("Server", "AmazonS3").build();
   @Test
   public void testPutWithReducedRedundancy() {
      Injector injector = createInjector(Functions.forMap(ImmutableMap.<HttpRequest, HttpResponse>of()), createModule(), setupProperties());

      Blob blob = injector.getInstance(BlobBuilder.class).name("test").payload("content").build();
      BlobToObject blobToObject = injector.getInstance(BlobToObject.class);
      
      AWSS3Client client = requestsSendResponses(bucketLocationRequest, bucketLocationResponse,
         HttpRequest.builder()
                    .method("PUT")
                    .endpoint("https://test.s3-eu-west-1.amazonaws.com/test")
                    .addHeader("x-amz-storage-class", "REDUCED_REDUNDANCY")
                    .addHeader("Host", "test.s3-eu-west-1.amazonaws.com")
                    .addHeader("Date", CONSTANT_DATE)
                    .addHeader("Authorization", "AWS identity:1mJrW85/mqZpYTFIK5Ebtt2MM6E=")
                    .payload("content").build(),
         HttpResponse.builder()
                     .statusCode(200)
                     .addHeader("x-amz-id-2", "w0rL+9fALQiCOToesVQefs8WalIgn+ZhMD7hHMKYud/xv7MyKkAWQOtFNEfK97Ri")
                     .addHeader("x-amz-request-id", "7A84C3CD4437A4C0")
                     .addHeader("Date", CONSTANT_DATE)
                     .addHeader("ETag", "437b930db84b8079c2dd804a71936b5f")
                     .addHeader("Server", "AmazonS3").build()
      );

      client.putObject("test", blobToObject.apply(blob),
         storageClass(StorageClass.REDUCED_REDUNDANCY));
   }
}
