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
package org.jclouds.cache.internal;

import static org.easymock.EasyMock.createMockBuilder;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.fail;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.cache.CacheLoader;

@Test(groups = "unit", singleThreaded = true, testName = "BackoffExponentiallyAndRetryOnThrowableCacheLoaderTest")
public class BackoffExponentiallyAndRetryOnThrowableCacheLoaderTest {
   private CacheLoader<String, Boolean> mock;

   @SuppressWarnings("unchecked")
   @BeforeMethod
   public void setUp() {
      // add mocked methods for default forwarded ones
      mock = createMockBuilder(CacheLoader.class).addMockedMethods("loadAll", "reload").createMock();
   }

   @Test
   void testMaxRetriesNotExceededReturnsValue() throws Exception {
      int attempts = 3;
      BackoffExponentiallyAndRetryOnThrowableCacheLoader<String, Boolean> backoff = new BackoffExponentiallyAndRetryOnThrowableCacheLoader<String, Boolean>(
               ResourceNotFoundException.class, 50l, 500l, attempts, mock);

      expect(mock.load("foo")).andThrow(new ResourceNotFoundException()).times(attempts - 1);
      expect(mock.load("foo")).andReturn(Boolean.TRUE);

      replay(mock);
      assertSame(backoff.load("foo"), Boolean.TRUE);
      verify(mock);
   }

   @Test
   void testMaxRetriesExceededThrowsException() throws Exception {
      int attempts = 3;
      BackoffExponentiallyAndRetryOnThrowableCacheLoader<String, Boolean> backoff = new BackoffExponentiallyAndRetryOnThrowableCacheLoader<String, Boolean>(
               ResourceNotFoundException.class, 50l, 500l, attempts, mock);

      expect(mock.load("foo")).andThrow(new ResourceNotFoundException()).times(attempts);

      replay(mock);
      try {
         backoff.load("foo");
         fail();
      } catch (ResourceNotFoundException e) {

      }
      verify(mock);
   }

   @Test
   void testThrowsTimeoutException() throws Exception {
      int attempts = 3;
      BackoffExponentiallyAndRetryOnThrowableCacheLoader<String, Boolean> backoff = new BackoffExponentiallyAndRetryOnThrowableCacheLoader<String, Boolean>(
               ResourceNotFoundException.class, 50l, 500l, attempts, mock);

      expect(mock.load("foo")).andThrow(new ExecutionException(new TimeoutException()));

      replay(mock);
      try {
         backoff.load("foo");
         fail();
      } catch (TimeoutException e) {

      }
      verify(mock);
   }
}
