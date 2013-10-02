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
package org.jclouds;

import static java.util.concurrent.Executors.defaultThreadFactory;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;

import org.jclouds.concurrent.DynamicExecutors;
import org.jclouds.date.DateService;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups="performance")
public abstract class PerformanceTest {
   protected static int LOOP_COUNT = 1000;
   protected static int THREAD_COUNT = 100;
   protected ExecutorService exec;

   @BeforeTest
   public void setupExecutorService() {
      exec = DynamicExecutors.newScalingThreadPool(1, THREAD_COUNT, 1000, defaultThreadFactory());
   }

   @AfterTest
   public void teardownExecutorService() {
      exec.shutdownNow();
      exec = null;
   }

   /**
    * Executes a list of Runnable tasks in {@link #THREAD_COUNT} simultaneous threads, and outputs
    * the timing results.
    * <p>
    * This method is careful to time only the actual task execution time, not the overhead of
    * creating and queuing the tasks. We also use CountDownLatches to ensure that all tasks start at
    * the same time, so concurrency is fully tested without ramp-up or ramp-down times.
    * <p>
    * This code is heavily based on Listing 5.11 in "Java Concurrency in Practice" by Brian Goetz et
    * al, Addison-Wesley Professional.
    * 
    * @see {@link DateService} for example usage.
    * 
    * @param performanceTestName
    * @param tasks
    * @throws InterruptedException
    * @throws ExecutionException
    * @throws Throwable
    */
   protected void executeMultiThreadedPerformanceTest(String performanceTestName,
            List<Runnable> tasks) throws InterruptedException, ExecutionException, Throwable {
      CompletionService<Throwable> completer = new ExecutorCompletionService<Throwable>(exec);
      final CountDownLatch startGate = new CountDownLatch(1);
      final CountDownLatch endGate = new CountDownLatch(THREAD_COUNT);

      for (int i = 0; i < THREAD_COUNT; i++) {
         final Runnable task = tasks.get(i % tasks.size());
         // Wrap task so we can count down endGate.
         completer.submit(new Callable<Throwable>() {
            public Throwable call() {
               try {
                  startGate.await(); // Wait to start simultaneously
                  task.run();
                  return null;
               } catch (Throwable t) {
                  return t;
               } finally {
                  endGate.countDown(); // Notify that I've finished
               }
            }
         });
      }

      // Only time the execution time for all tasks, not start/stop times.
      long startTime = System.nanoTime();
      startGate.countDown(); // Trigger start of all tasks
      endGate.await();
      long endTime = System.nanoTime() - startTime;

      // Check for assertion failures
      Throwable t;
      for (int i = 0; i < THREAD_COUNT; i++) {
         t = completer.take().get();
         if (t != null) {
            throw t;
         }
      }
      if (performanceTestName != null) {
         System.out.printf("TIMING: Multi-threaded %s took %.3fms for %d threads\n",
                  performanceTestName, ((double) endTime / 1000000), THREAD_COUNT);
      }
   }

   protected void executeMultiThreadedCorrectnessTest(List<Runnable> tasks)
            throws InterruptedException, ExecutionException, Throwable {
      executeMultiThreadedPerformanceTest(null, tasks);
   }

}
