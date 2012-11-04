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
import java.util.concurrent.Future;

import org.jclouds.concurrent.Futures.CallGetAndRunExecutionList;
import org.testng.annotations.Test;

import com.google.common.util.concurrent.ExecutionList;

/**
 * Tests behavior of Futures
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class FuturesTest {
   ExecutorService executorService = MoreExecutors.sameThreadExecutor();

   @Test
   public void testCallGetAndRunRunnableRunsListOnRuntimeException() throws InterruptedException, ExecutionException {

      Runnable runnable = createMock(Runnable.class);
      @SuppressWarnings("unchecked")
      Future<String> future = createMock(Future.class);
      runnable.run();
      expect(future.get()).andThrow(new RuntimeException());
      replay(runnable);
      replay(future);

      ExecutionList executionList = new ExecutionList();
      executionList.add(runnable, executorService);

      CallGetAndRunExecutionList<String> caller = new CallGetAndRunExecutionList<String>(future, executionList);
      caller.run();

      verify(runnable);
      verify(future);
   }

   @Test
   public void testCallGetAndRunRunnableInterruptsAndThrowsIllegalStateExceptionOnInterruptedException()
         throws InterruptedException, ExecutionException {

      Runnable runnable = createMock(Runnable.class);
      @SuppressWarnings("unchecked")
      Future<String> future = createMock(Future.class);
      expect(future.get()).andThrow(new InterruptedException());
      replay(runnable);
      replay(future);

      ExecutionList executionList = new ExecutionList();
      executionList.add(runnable, executorService);

      CallGetAndRunExecutionList<String> caller = new CallGetAndRunExecutionList<String>(future, executionList);
      try {
         caller.run();
         fail("Expected IllegalStateException");
      } catch (IllegalStateException e) {
         assertEquals(e.getMessage(), "interrupted calling get() on [EasyMock for interface java.util.concurrent.Future], so could not run listeners");
      }

      verify(runnable);
      verify(future);
   }

}
