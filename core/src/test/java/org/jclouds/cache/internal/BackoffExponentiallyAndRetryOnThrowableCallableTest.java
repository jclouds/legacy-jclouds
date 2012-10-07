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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.util.concurrent.Callable;

import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "BackoffExponentiallyAndRetryOnThrowableCallableTest", singleThreaded = true)
public class BackoffExponentiallyAndRetryOnThrowableCallableTest {
   private Callable<String> mock;

   @SuppressWarnings("unchecked")
   @BeforeMethod
   public void setUp() {
      mock = createMock(Callable.class);
   }

   @Test
   void testMaxRetriesNotExceededReturnsValue() throws Exception {
      int attempts = 3;
      BackoffExponentiallyAndRetryOnThrowableCallable<String> backoff = new BackoffExponentiallyAndRetryOnThrowableCallable<String>(
               ResourceNotFoundException.class, 50l, 500l, attempts, mock);

      expect(mock.call()).andThrow(new ResourceNotFoundException()).times(attempts - 1);
      expect(mock.call()).andReturn("foo");

      replay(mock);
      assertEquals(backoff.call(), "foo");
      verify(mock);
   }

   @Test
   void testMaxRetriesExceededThrowsException() throws Exception {
      int attempts = 3;
      BackoffExponentiallyAndRetryOnThrowableCallable<String> backoff = new BackoffExponentiallyAndRetryOnThrowableCallable<String>(
               ResourceNotFoundException.class, 50l, 500l, attempts, mock);

      expect(mock.call()).andThrow(new ResourceNotFoundException()).times(attempts);

      replay(mock);
      try {
         backoff.call();
         fail();
      } catch (ResourceNotFoundException e) {

      }
      verify(mock);
   }
}
