/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.lifecycle.config;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.lifecycle.Closer;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import javax.inject.Inject;
import com.google.inject.Injector;

/**
 * // TODO: Adrian: Document this!
 * 
 * @author Adrian Cole
 */
@Test
public class LifeCycleModuleTest {

   @Test
   void testBindsExecutor() {
      Injector i = createInjector();
      assert i.getInstance(ExecutorService.class) != null;
   }

   private Injector createInjector() {
      Injector i = Guice.createInjector(new LifeCycleModule(), new ExecutorServiceModule(Executors
               .newCachedThreadPool()));
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
      ExecutorService executor = i.getInstance(ExecutorService.class);
      assert !executor.isShutdown();
      Closer closer = i.getInstance(Closer.class);
      closer.close();
      assert executor.isShutdown();
   }

   static class PreDestroyable {
      boolean isClosed = false;

      @Inject
      PreDestroyable(ExecutorService executor) {
         this.executor = executor;
      }

      ExecutorService executor;

      @PreDestroy
      public void close() {
         assert !executor.isShutdown();
         isClosed = true;
      }
   }

   @Test
   void testCloserPreDestroyOrder() throws IOException {
      Injector i = createInjector().createChildInjector(new AbstractModule() {
         protected void configure() {
            bind(PreDestroyable.class);
         }
      });
      ExecutorService executor = i.getInstance(ExecutorService.class);
      assert !executor.isShutdown();
      PreDestroyable preDestroyable = i.getInstance(PreDestroyable.class);
      assert !preDestroyable.isClosed;
      Closer closer = i.getInstance(Closer.class);
      closer.close();
      assert preDestroyable.isClosed;
      assert executor.isShutdown();
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
