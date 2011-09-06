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
package org.jclouds.concurrent.config;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

import org.jclouds.Constants;
import org.jclouds.lifecycle.Closer;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;

/**
 * 
 * @author Adrian Cole
 */
@Test
public class ExecutorServiceModuleTest {

   @Test
   public void testShutdownOnClose() throws IOException {
      Injector i = Guice.createInjector();

      Closer closer = i.getInstance(Closer.class);
      ExecutorService executor = createMock(ExecutorService.class);
      ExecutorServiceModule.shutdownOnClose(executor, closer);

      expect(executor.shutdownNow()).andReturn(ImmutableList.<Runnable> of()).atLeastOnce();

      replay(executor);
      closer.close();

      verify(executor);
   }

   @Test
   public void testShutdownOnCloseThroughModule() throws IOException {

      ExecutorServiceModule module = new ExecutorServiceModule() {

         @Override
         protected void configure() {
            bindConstant().annotatedWith(Names.named(Constants.PROPERTY_IO_WORKER_THREADS)).to(1);
            bindConstant().annotatedWith(Names.named(Constants.PROPERTY_USER_THREADS)).to(1);
            super.configure();
         }

      };
      Injector i = Guice.createInjector(module);
      assertEquals(module.userExecutorFromConstructor, null);
      assertEquals(module.ioExecutorFromConstructor, null);

      Closer closer = i.getInstance(Closer.class);

      ExecutorService user = i
               .getInstance(Key.get(ExecutorService.class, Names.named(Constants.PROPERTY_USER_THREADS)));
      ExecutorService io = i.getInstance(Key.get(ExecutorService.class, Names
               .named(Constants.PROPERTY_IO_WORKER_THREADS)));

      assert !user.isShutdown();
      assert !io.isShutdown();

      closer.close();

      assert user.isShutdown();
      assert io.isShutdown();

   }
}
