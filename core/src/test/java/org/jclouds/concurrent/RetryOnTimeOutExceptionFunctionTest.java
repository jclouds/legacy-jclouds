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
package org.jclouds.concurrent;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;

import org.jclouds.rest.AuthorizationException;
import org.testng.annotations.Test;

import com.google.common.base.Function;

/**
 * Tests behavior of RetryOnTimeOutExceptionFunction
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", singleThreaded = true,  testName = "RetryOnTimeOutExceptionFunctionTest")
public class RetryOnTimeOutExceptionFunctionTest {
   ExecutorService executorService = MoreExecutors.sameThreadExecutor();

   @SuppressWarnings("unchecked")
   @Test
   public void testGetThrowsOriginalExceptionButRetriesOnTimeoutException() throws InterruptedException, ExecutionException {
      Function<String, String> delegate = createMock(Function.class);
      TimeoutException timeout = createMock(TimeoutException.class);
      RuntimeException throwable = new RuntimeException(timeout);

      expect(delegate.apply("baz")).andThrow(throwable);
      expect(timeout.getCause()).andReturn(null).anyTimes();
      expect(delegate.apply("baz")).andThrow(throwable);
      expect(delegate.apply("baz")).andThrow(throwable);

      replay(delegate);
      replay(timeout);

      RetryOnTimeOutExceptionFunction<String, String> supplier = new RetryOnTimeOutExceptionFunction<String, String>(
               delegate);
      try {
         supplier.apply("baz");
         fail();
      } catch (RuntimeException e) {
         assertEquals(e.getCause(), timeout);
      }
      
      verify(delegate);
      verify(timeout);

   }

   @SuppressWarnings("unchecked")
   @Test
   public void testGetAllowsTwoFailuresOnTimeoutException() throws InterruptedException, ExecutionException {
      Function<String, String> delegate = createMock(Function.class);
      TimeoutException timeout = createMock(TimeoutException.class);
      RuntimeException throwable = new RuntimeException(timeout);

      expect(delegate.apply("baz")).andThrow(throwable);
      expect(timeout.getCause()).andReturn(null).anyTimes();
      expect(delegate.apply("baz")).andThrow(throwable);
      expect(delegate.apply("baz")).andReturn("foo");

      replay(delegate);
      replay(timeout);

      RetryOnTimeOutExceptionFunction<String, String> supplier = new RetryOnTimeOutExceptionFunction<String, String>(
               delegate);
      assertEquals(supplier.apply("baz"), "foo");

      verify(delegate);
      verify(timeout);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testGetAllowsNoFailuresOnOtherExceptions() throws InterruptedException, ExecutionException {
      Function<String, String> delegate = createMock(Function.class);
      AuthorizationException auth = createMock(AuthorizationException.class);
      RuntimeException throwable = new RuntimeException(auth);

      expect(delegate.apply("baz")).andThrow(throwable);
      expect(auth.getCause()).andReturn(null).anyTimes();
  

      replay(delegate);
      replay(auth);

      RetryOnTimeOutExceptionFunction<String, String> supplier = new RetryOnTimeOutExceptionFunction<String, String>(
               delegate);

      try {
         supplier.apply("baz");
         fail();
      } catch (RuntimeException e) {
         assertEquals(e.getCause(), auth);
      }

      verify(delegate);
      verify(auth);

   }

}
