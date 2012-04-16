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
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.jclouds.concurrent.config.ExecutorServiceModuleTest.checkFutureGetFailsWith;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.easymock.EasyMock;
import org.jclouds.Constants;
import org.jclouds.concurrent.config.ExecutorServiceModuleTest.ConfigurableRunner;
import org.jclouds.lifecycle.Closer;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;

/**
 * Unit tests for the {@link ScheduledExecutorServiceModule} class.
 * 
 * @author Ignasi Barrera
 * 
 * @see ExecutorServiceModuleTest
 */
@Test(groups = "unit")
public class ScheduledExecutorServiceModuleTest {

   @Test(groups = "unit")
   public void testShutdownOnClose() throws IOException {
      Injector i = Guice.createInjector();

      Closer closer = i.getInstance(Closer.class);
      ScheduledExecutorService executor = EasyMock.createMock(ScheduledExecutorService.class);
      ExecutorServiceModule.shutdownOnClose(executor, closer);

      expect(executor.shutdownNow()).andReturn(ImmutableList.<Runnable> of()).atLeastOnce();

      replay(executor);
      closer.close();

      verify(executor);
   }

   @Test(groups = "unit")
   public void testShutdownOnCloseThroughModule() throws IOException {

      ScheduledExecutorServiceModule module = new ScheduledExecutorServiceModule() {
         @Override
         protected void configure() {
            bindConstant().annotatedWith(Names.named(Constants.PROPERTY_SCHEDULER_THREADS)).to(1);
            super.configure();
         }
      };

      Injector i = Guice.createInjector(module);
      Closer closer = i.getInstance(Closer.class);

      ScheduledExecutorService sched = i.getInstance(Key.get(ScheduledExecutorService.class, Names
              .named(Constants.PROPERTY_SCHEDULER_THREADS)));

      assert !sched.isShutdown();

      closer.close();

      assert sched.isShutdown();
   }

   @Test(groups = "unit")
   public void testDescribedFutureToString() throws Exception {

       ScheduledExecutorServiceModule module = new ScheduledExecutorServiceModule() {
         @Override
         protected void configure() {
            bindConstant().annotatedWith(Names.named(Constants.PROPERTY_SCHEDULER_THREADS)).to(1);
            super.configure();
         }
      };

      Injector i = Guice.createInjector(module);
      Closer closer = i.getInstance(Closer.class);

      ScheduledExecutorService sched = i.getInstance(Key.get(ScheduledExecutorService.class, Names
              .named(Constants.PROPERTY_SCHEDULER_THREADS)));

      ConfigurableRunner t1 = new ConfigurableRunner();
      t1.result = "okay";

      ScheduledFuture<Object> esc = performScheduleInSeparateMethod1(sched, t1);
      assert esc.toString().indexOf("ConfigurableRunner") >= 0;
      assert esc.get().equals("okay");

      closer.close();
   }

   @Test(groups = "unit")
   public void testDescribedFutureExceptionIncludesSubmissionTrace() throws Exception {

       ScheduledExecutorServiceModule module = new ScheduledExecutorServiceModule() {
         @Override
         protected void configure() {
            bindConstant().annotatedWith(Names.named(Constants.PROPERTY_SCHEDULER_THREADS)).to(1);
            super.configure();
         }
      };

      Injector i = Guice.createInjector(module);
      Closer closer = i.getInstance(Closer.class);

      ScheduledExecutorService sched = i.getInstance(Key.get(ScheduledExecutorService.class, Names
              .named(Constants.PROPERTY_SCHEDULER_THREADS)));

      ConfigurableRunner t1 = new ConfigurableRunner();
      t1.failMessage = "foo";
      t1.result = "shouldn't happen";

      ScheduledFuture<Object> esc = performScheduleInSeparateMethod1(sched, t1);
      checkFutureGetFailsWith(esc, "foo", "testDescribedFutureExceptionIncludesSubmissionTrace", "performScheduleInSeparateMethod1");

      ScheduledFuture<?> esr = performScheduleInSeparateMethod2(sched, t1);
      checkFutureGetFailsWith(esr, "foo", "testDescribedFutureExceptionIncludesSubmissionTrace", "performScheduleInSeparateMethod2");

      ScheduledFuture<?> esfr = performScheduleInSeparateMethod3(sched, t1);
      checkFutureGetFailsWith(esfr, "foo", "testDescribedFutureExceptionIncludesSubmissionTrace", "performScheduleInSeparateMethod3");

      ScheduledFuture<?> esfd = performScheduleInSeparateMethod4(sched, t1);
      checkFutureGetFailsWith(esfd, "foo", "testDescribedFutureExceptionIncludesSubmissionTrace", "performScheduleInSeparateMethod4");

      closer.close();
   }

   static ScheduledFuture<Object> performScheduleInSeparateMethod1(ScheduledExecutorService sched, ConfigurableRunner t1) {
       return sched.schedule((Callable<Object>)t1, 0, TimeUnit.SECONDS);
   }

   static ScheduledFuture<?> performScheduleInSeparateMethod2(ScheduledExecutorService sched, ConfigurableRunner t1) {
       return sched.schedule((Runnable)t1, 0, TimeUnit.SECONDS);
   }

   static ScheduledFuture<?> performScheduleInSeparateMethod3(ScheduledExecutorService sched, ConfigurableRunner t1) {
       return sched.scheduleAtFixedRate((Runnable)t1, 0, 1, TimeUnit.SECONDS);
   }

   static ScheduledFuture<?> performScheduleInSeparateMethod4(ScheduledExecutorService sched, ConfigurableRunner t1) {
       return sched.scheduleWithFixedDelay((Runnable)t1, 0, 1, TimeUnit.SECONDS);
   }
}
