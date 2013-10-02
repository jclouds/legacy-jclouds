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
package org.jclouds.s3.fallbacks;
import static com.google.common.util.concurrent.Futures.getUnchecked;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.jclouds.reflect.Reflection2.method;
import static org.testng.Assert.assertFalse;

import org.jclouds.aws.AWSResponseException;
import org.jclouds.aws.domain.AWSError;
import org.jclouds.reflect.Invocation;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.s3.S3Client;
import org.jclouds.s3.options.PutBucketOptions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

/**
 * @author Adrian Cole
 */
@Test(testName = "FalseIfBucketAlreadyOwnedByYouOrOperationAbortedWhenBucketExistsTest")
public class FalseIfBucketAlreadyOwnedByYouOrOperationAbortedWhenBucketExistsTest {

   GeneratedHttpRequest putBucket;

   @BeforeClass
   void setUp() throws SecurityException, NoSuchMethodException {
      putBucket = GeneratedHttpRequest.builder()
            .method("PUT")
            .endpoint("https://adriancole-blobstore113.s3.amazonaws.com/")
            .invocation(
                  Invocation.create(method(S3Client.class, "putBucketInRegion", String.class,
                        String.class, PutBucketOptions[].class), Lists.<Object> newArrayList(null, "bucket"))).build();
   }

   @Test
   void testBucketAlreadyOwnedByYouIsOk() throws Exception {
      S3Client client = createMock(S3Client.class);
      replay(client);

      Exception e = getErrorWithCode("BucketAlreadyOwnedByYou");
      assertFalse(getUnchecked(new FalseIfBucketAlreadyOwnedByYouOrOperationAbortedWhenBucketExists(client).setContext(
            putBucket).create(e)));
      verify(client);
   }

   @Test
   void testOperationAbortedIsOkWhenBucketExists() throws Exception {
      S3Client client = createMock(S3Client.class);
      expect(client.bucketExists("bucket")).andReturn(true);
      replay(client);
      Exception e = getErrorWithCode("OperationAborted");
      assertFalse(getUnchecked(new FalseIfBucketAlreadyOwnedByYouOrOperationAbortedWhenBucketExists(client).setContext(
            putBucket).create(e)));
      verify(client);
   }

   @Test(expectedExceptions = Exception.class)
   void testOperationAbortedNotOkWhenBucketDoesntExist() throws Exception {
      S3Client client = createMock(S3Client.class);
      expect(client.bucketExists("bucket")).andReturn(false);
      replay(client);
      Exception e = getErrorWithCode("OperationAborted");
      new FalseIfBucketAlreadyOwnedByYouOrOperationAbortedWhenBucketExists(client).setContext(putBucket).create(e);
   }

   @Test(expectedExceptions = IllegalStateException.class)
   void testIllegalStateIsNotOk() throws Exception {
      S3Client client = createMock(S3Client.class);
      replay(client);

      Exception e = new IllegalStateException();
      new FalseIfBucketAlreadyOwnedByYouOrOperationAbortedWhenBucketExists(client).create(e);
   }

   @Test(expectedExceptions = AWSResponseException.class)
   void testBlahIsNotOk() throws Exception {
      S3Client client = createMock(S3Client.class);
      replay(client);
      Exception e = getErrorWithCode("blah");
      new FalseIfBucketAlreadyOwnedByYouOrOperationAbortedWhenBucketExists(client).create(e);
   }

   private Exception getErrorWithCode(String code) {
      AWSError error = new AWSError();
      error.setCode(code);
      return new AWSResponseException(null, null, null, error);
   }
}
