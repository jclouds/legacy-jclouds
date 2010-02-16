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
package org.jclouds.concurrent.config;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jclouds.lifecycle.Closer;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * 
 * @author Adrian Cole
 */
@Test
public class ExecutorServiceModuleTest {

   private Closer closer;

   @BeforeTest
   public void setUp() throws Exception {
      Injector i = Guice.createInjector();
      closer = i.getInstance(Closer.class);
   }

   @Test
   public void testShutdownOnClose() throws IOException {
      ExecutorService executor = Executors.newCachedThreadPool();
      assert !executor.isShutdown();
      ExecutorServiceModule.shutdownOnClose(executor, closer);
      closer.close();
      assert executor.isShutdown();
   }
}
