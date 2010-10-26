/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.pool;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Inspired by work by Aslak Knutsen in the Arquillian project
 * 
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 */
public class ObjectPool<T> {

   private final Creator<T> creator;
   private final UsedObjectStrategy usedObjectStrategy;
   private final int poolSize;
   private final PoolListener<T> listener;
   private final ConcurrentLinkedQueue<T> pool = new ConcurrentLinkedQueue<T>();
   private final ExecutorService executor;

   public enum UsedObjectStrategy {
      THROW_AWAY, REUSE
   }

   public interface PoolListener<T> {

      void added(T object);
   }

   public static final class EmptyListener<B> implements PoolListener<B> {
      public void added(B object) {
      }
   }

   public ObjectPool(Creator<T> creator, int poolSize, UsedObjectStrategy usedObjectStrategy) {
      this(creator, poolSize, usedObjectStrategy, new EmptyListener<T>());
   }

   public ObjectPool(Creator<T> creator, int poolSize, UsedObjectStrategy usedObjectStrategy, PoolListener<T> listener) {
      this(creator, poolSize, usedObjectStrategy, listener, new ThreadPoolExecutor(3, Integer.MAX_VALUE, 60L,
            TimeUnit.SECONDS, new SynchronousQueue<Runnable>()));
   }

   public ObjectPool(Creator<T> creator, int poolSize, UsedObjectStrategy usedObjectStrategy, PoolListener<T> listener,
         ExecutorService executor) {
      this.creator = creator;
      this.poolSize = poolSize;
      this.usedObjectStrategy = usedObjectStrategy;
      this.listener = listener;
      this.executor = executor;
      initialize();
   }

   private void initialize() {
      for (int i = 0; i < poolSize; i++) {
         createNewObject();
      }
   }

   public synchronized void shutdown() {
      while (pool.size() > 0) {
         removeObject(pool.poll());
      }
      executor.shutdown();
      try {
         int maxShutdownTimeoutInMinutes = 5;
         if (!executor.awaitTermination(maxShutdownTimeoutInMinutes, TimeUnit.MINUTES)) {
            throw new RuntimeException("Not all object were destroyed, timeout accured: " + maxShutdownTimeoutInMinutes
                  + " min");
         }
      } catch (InterruptedException e) {
         throw new RuntimeException(e);
      }
   }

   protected void removeObject(final T object) {
      executor.execute(new Runnable() {
         public void run() {
            creator.destroy(object);
         }
      });
   }

   protected void createNewObject() {
      FutureTask<T> futureTask = new FutureTask<T>(new Callable<T>() {
         public T call() throws Exception {
            T object = creator.create();
            addToPool(object);
            return object;
         }
      });
      executor.submit(futureTask);
   }

   protected void addToPool(T object) {
      pool.add(object);
      listener.added(object);
   }

   public int currentSize() {
      return pool.size();
   }

   public PooledObject<T> get() {
      return new PooledObject<T>(getFromPoolBlocking(), getDestroyer());
   }

   /**
    * @return
    */
   private T getFromPoolBlocking() {
      T object;
      while (true) {
         while (pool.size() == 0) {
            try {
               Thread.sleep(100);
            } catch (InterruptedException e) {
               throw new RuntimeException(e);
            }
         }
         object = pool.poll();
         boolean returnToPool = true;
         try {
            if (object != null) {
               returnToPool = false;
               return object;
            }
         } catch (Exception e) {
         } finally {
            if (returnToPool) {
               addToPool(object);
            }
         }
      }
   }

   public Destroyer<T> getDestroyer() {
      switch (usedObjectStrategy) {
      case THROW_AWAY:
         return new Destroyer<T>() {
            public void destroy(T object) {
               createNewObject();
               removeObject(object);
            };
         };

      case REUSE:
         return new Destroyer<T>() {
            public void destroy(T object) {
               addToPool(object);
            };
         };
      }
      return null;
   }
}
