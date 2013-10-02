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
package org.jclouds.lifecycle.config;

import static com.google.inject.name.Names.named;
import static org.jclouds.Constants.PROPERTY_IO_WORKER_THREADS;
import static org.jclouds.Constants.PROPERTY_USER_THREADS;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import javax.annotation.PostConstruct;

import static org.easymock.EasyMock.*;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.lifecycle.Closer;
import org.testng.annotations.Test;

import com.google.common.util.concurrent.ExecutionList;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;

/**
 * 
 * @author Adrian Cole
 */
@Test
public class LifeCycleModuleTest {

   @Test
   void testBindsExecutor() {
      Injector i = createInjector();
      assert i.getInstance(Key.get(ListeningExecutorService.class, named(PROPERTY_USER_THREADS))) != null;
      assert i.getInstance(Key.get(ListeningExecutorService.class, named(PROPERTY_IO_WORKER_THREADS))) != null;
   }

   private Injector createInjector() {
      Injector i = Guice.createInjector(new AbstractModule() {
         protected void configure() {
            bindConstant().annotatedWith(named(PROPERTY_IO_WORKER_THREADS)).to(1);
            bindConstant().annotatedWith(named(PROPERTY_USER_THREADS)).to(1);
         }
      }, new LifeCycleModule(), new ExecutorServiceModule());
      // TODO: currently have to manually invoke the execution list, as otherwise it may occur
      // before everything is wired up
      i.getInstance(ExecutionList.class).execute();
      return i;
   }

   @Test
   void testBindsCloser() {
      Injector i = createInjector();
      assert i.getInstance(Closer.class) != null;
      assert i.getInstance(Closer.class).getState() == Closer.State.AVAILABLE;
   }

   @Test
   void testCloserClosesExecutor() throws IOException {
      Injector i = createInjector();
      ListeningExecutorService executor = i.getInstance(Key.get(ListeningExecutorService.class,
            named(PROPERTY_USER_THREADS)));
      assert !executor.isShutdown();
      Closer closer = i.getInstance(Closer.class);
      assert closer.getState() == Closer.State.AVAILABLE;
      closer.close();
      assert executor.isShutdown();
      assert closer.getState() == Closer.State.DONE;
   }

   @Test
   void testCloserPreDestroyOrder() throws IOException {
      Injector i = createInjector();
      ListeningExecutorService userExecutor = i.getInstance(Key.get(ListeningExecutorService.class,
            named(PROPERTY_USER_THREADS)));
      assert !userExecutor.isShutdown();
      ListeningExecutorService ioExecutor = i.getInstance(Key.get(ListeningExecutorService.class,
            named(PROPERTY_IO_WORKER_THREADS)));
      assert !ioExecutor.isShutdown();
      Closer closer = i.getInstance(Closer.class);
      assert closer.getState() == Closer.State.AVAILABLE;
      closer.close();
      assert userExecutor.isShutdown();
      assert ioExecutor.isShutdown();
      assert closer.getState() == Closer.State.DONE;
   }

   static class PostConstructable {
      boolean isStarted;

      @PostConstruct
      void start() {
         isStarted = true;
      }
   }

   @Test
   void testPostConstruct() {
      Injector i = createInjector().createChildInjector(new AbstractModule() {
         protected void configure() {
            bind(PostConstructable.class);
         }
      });
      PostConstructable postConstructable = i.getInstance(PostConstructable.class);
      assert postConstructable.isStarted;
   }

   @Test
   void testCloserClosingState() throws InterruptedException {
      Injector i = createInjector();
      final Closer closer = i.getInstance(Closer.class);

      final CountDownLatch closeDone = new CountDownLatch(1);
      final CountDownLatch closeStart = new CountDownLatch(1);

      closer.addToClose(new Closeable() {
         @Override
         public void close() throws IOException {
             try {
                 closeStart.countDown();
                 assert closer.getState() == Closer.State.PROCESSING;
                 closeDone.await();
             } catch (InterruptedException e) {
                 assert false : e.getMessage();
             }
         }
      });

      Thread thread = new Thread(new Runnable() {
         @Override
         public void run() {
            try {
               closer.close();
            } catch (IOException e) {
               assert false : e.getMessage();
            }
         }
      });

      thread.start();

      closeStart.await();

      assert closer.getState() == Closer.State.PROCESSING;

      closeDone.countDown();

      thread.join();

      assert closer.getState() == Closer.State.DONE;
   }

   @Test
   void testCloserCallOneClose() throws IOException, InterruptedException {
      Injector i = createInjector();
      final Closer closer = i.getInstance(Closer.class);

      Closeable closeable = createStrictMock(Closeable.class);

      closeable.close();

      expectLastCall();

      replay(closeable);

      closer.addToClose(closeable);

      Runnable closeContext = new Runnable() {
         @Override
         public void run() {
            try {
               closer.close();
            } catch (IOException e) {
               assert false : e.getMessage();
            }
         }
      };

      Thread thread1 = new Thread(closeContext);
      Thread thread2 = new Thread(closeContext);

      thread1.start();
      thread2.start();

      thread1.join();
      thread2.join();

      verify(closeable);

      assert closer.getState() == Closer.State.DONE;
   }
}
