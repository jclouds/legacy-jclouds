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
package org.jclouds.s3.blobstore.internal;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertSame;

import java.util.concurrent.TimeoutException;

import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.s3.S3Client;
import org.jclouds.s3.domain.AccessControlList;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.util.concurrent.UncheckedExecutionException;

@Test(groups = "unit", singleThreaded = true, testName = "BackoffOnNotFoundWhenGetBucketACLTest")
public class BackoffOnNotFoundWhenGetBucketACLTest {
   private S3Client mock;

   @BeforeMethod
   public void setUp() {
      mock = createMock(S3Client.class);
   }

   @Test
   void testMaxRetriesNotExceededReturnsValue() {
      AccessControlList acl = createMock(AccessControlList.class);

      int attempts = 5;
      BackoffOnNotFoundWhenGetBucketACL backoff = new BackoffOnNotFoundWhenGetBucketACL(mock);

      expect(mock.getBucketACL("foo")).andThrow(new ResourceNotFoundException()).times(attempts - 1);
      expect(mock.getBucketACL("foo")).andReturn(acl);

      replay(mock);
      assertSame(backoff.load("foo"), acl);
      verify(mock);
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   void testMaxRetriesExceededThrowsException() {
      int attempts = 5;
      BackoffOnNotFoundWhenGetBucketACL backoff = new BackoffOnNotFoundWhenGetBucketACL(mock);

      expect(mock.getBucketACL("foo")).andThrow(new ResourceNotFoundException()).times(attempts);

      replay(mock);
      backoff.load("foo");
   }

   @Test(expectedExceptions = UncheckedExecutionException.class)
   void testDoesntCatchOtherExceptions() {
      BackoffOnNotFoundWhenGetBucketACL backoff = new BackoffOnNotFoundWhenGetBucketACL(mock);

      expect(mock.getBucketACL("foo")).andThrow(new UncheckedExecutionException(new TimeoutException()));

      replay(mock);
      backoff.load("foo");
      verify(mock);
   }
}
