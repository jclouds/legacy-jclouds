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
package org.jclouds.lifecycle.config;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

import javax.annotation.PostConstruct;
import javax.inject.Named;

import org.jclouds.Constants;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.lifecycle.Closer;
import com.google.inject.name.Names;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provides;

/**
 * 
 * @author Adrian Cole
 */
@Test
public class LifeCycleModuleTest {

   @Test
   void testBindsExecutor() {
      Injector i = createInjector();
      assert i.getInstance(Key.get(ExecutorService.class, Names
               .named(Constants.PROPERTY_USER_THREADS))) != null;
      assert i.getInstance(Key.get(ExecutorService.class, Names
               .named(Constants.PROPERTY_IO_WORKER_THREADS))) != null;
   }

   private Injector createInjector() {
      Injector i = Guice.createInjector(new LifeCycleModule() {
         @SuppressWarnings("unused")
         @Provides
         @Named(Constants.PROPERTY_USER_THREADS)
         int p() {
            return 1;
         }

         @SuppressWarnings("unused")
         @Provides
         @Named(Constants.PROPERTY_MAX_CONNECTIONS_PER_CONTEXT)
         int p2() {
            return 1;
         }

         @SuppressWarnings("unused")
         @Provides
         @Named(Constants.PROPERTY_IO_WORKER_THREADS)
         int p3() {
            return 1;
         }
      }, new ExecutorServiceModule());
      return i;
   }

   @Test
   void testBindsCloser() {
      Injector i = createInjector();
      assert i.getInstance(Closer.class) != null;
   }

   @Test
   void testCloserClosesExecutor() throws IOException {
      Injector i = createInjector();
      ExecutorService executor = i.getInstance(Key.get(ExecutorService.class, Names
               .named(Constants.PROPERTY_USER_THREADS)));
      assert !executor.isShutdown();
      Closer closer = i.getInstance(Closer.class);
      closer.close();
      assert executor.isShutdown();
   }

   @Test
   void testCloserPreDestroyOrder() throws IOException {
      Injector i = createInjector();
      ExecutorService userThreads = i.getInstance(Key.get(ExecutorService.class, Names
               .named(Constants.PROPERTY_USER_THREADS)));
      assert !userThreads.isShutdown();
      ExecutorService ioThreads = i.getInstance(Key.get(ExecutorService.class, Names
               .named(Constants.PROPERTY_IO_WORKER_THREADS)));
      assert !ioThreads.isShutdown();
      Closer closer = i.getInstance(Closer.class);
      closer.close();
      assert userThreads.isShutdown();
      assert ioThreads.isShutdown();
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

}
