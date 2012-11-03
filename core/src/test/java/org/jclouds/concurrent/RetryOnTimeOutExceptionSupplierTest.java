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

import com.google.common.base.Supplier;

/**
 * Tests behavior of RetryOnTimeOutExceptionSupplier
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class RetryOnTimeOutExceptionSupplierTest {
   ExecutorService executorService = MoreExecutors.sameThreadExecutor();

   @SuppressWarnings("unchecked")
   @Test
   public void testGetThrowsOriginalExceptionButRetriesOnTimeoutException() throws InterruptedException, ExecutionException {
      Supplier<String> delegate = createMock(Supplier.class);
      TimeoutException timeout = createMock(TimeoutException.class);
      RuntimeException throwable = new RuntimeException(timeout);

      expect(delegate.get()).andThrow(throwable);
      expect(timeout.getCause()).andReturn(null).anyTimes();
      expect(delegate.get()).andThrow(throwable);
      expect(delegate.get()).andThrow(throwable);

      replay(delegate);
      replay(timeout);

      RetryOnTimeOutExceptionSupplier<String> supplier = new RetryOnTimeOutExceptionSupplier<String>(
               delegate);
      try {
         supplier.get();
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
      Supplier<String> delegate = createMock(Supplier.class);
      TimeoutException timeout = createMock(TimeoutException.class);
      RuntimeException throwable = new RuntimeException(timeout);

      expect(delegate.get()).andThrow(throwable);
      expect(timeout.getCause()).andReturn(null).anyTimes();
      expect(delegate.get()).andThrow(throwable);
      expect(delegate.get()).andReturn("foo");

      replay(delegate);
      replay(timeout);

      RetryOnTimeOutExceptionSupplier<String> supplier = new RetryOnTimeOutExceptionSupplier<String>(
               delegate);
      assertEquals(supplier.get(), "foo");

      verify(delegate);
      verify(timeout);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testGetAllowsNoFailuresOnOtherExceptions() throws InterruptedException, ExecutionException {
      Supplier<String> delegate = createMock(Supplier.class);
      AuthorizationException auth = createMock(AuthorizationException.class);
      RuntimeException throwable = new RuntimeException(auth);

      expect(delegate.get()).andThrow(throwable);
      expect(auth.getCause()).andReturn(null).anyTimes();
  

      replay(delegate);
      replay(auth);

      RetryOnTimeOutExceptionSupplier<String> supplier = new RetryOnTimeOutExceptionSupplier<String>(
               delegate);

      try {
         supplier.get();
         fail();
      } catch (RuntimeException e) {
         assertEquals(e.getCause(), auth);
      }

      verify(delegate);
      verify(auth);

   }

}
