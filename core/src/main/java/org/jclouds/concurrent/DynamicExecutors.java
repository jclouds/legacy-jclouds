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
package org.jclouds.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Factory and utility methods for handling {@link DynamicThreadPoolExecutor}.
 * 
 * @author kimchy (shay.banon)
 */
public class DynamicExecutors {

   /**
    * Creates a thread pool, same as in {@link #newScalingThreadPool(int, int, long)}, using the provided ThreadFactory
    * to create new threads when needed.
    * 
    * @param min
    *           the number of threads to keep in the pool, even if they are idle.
    * @param max
    *           the maximum number of threads to allow in the pool.
    * @param keepAliveTime
    *           when the number of threads is greater than the min, this is the maximum time that excess idle threads
    *           will wait for new tasks before terminating (in milliseconds).
    * @param threadFactory
    *           the factory to use when creating new threads.
    * @return the newly created thread pool
    */
   public static ExecutorService newScalingThreadPool(int min, int max, long keepAliveTime, ThreadFactory threadFactory) {
      DynamicThreadPoolExecutor.DynamicQueue<Runnable> queue = new DynamicThreadPoolExecutor.DynamicQueue<Runnable>();
      ThreadPoolExecutor executor = new DynamicThreadPoolExecutor(min, max, keepAliveTime,
               TimeUnit.MILLISECONDS, queue, threadFactory);
      executor.setRejectedExecutionHandler(new DynamicThreadPoolExecutor.ForceQueuePolicy());
      queue.setThreadPoolExecutor(executor);
      return executor;
   }

   /**
    * Cannot instantiate.
    */
   private DynamicExecutors() {
   }
}
