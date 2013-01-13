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
import static com.google.common.base.Throwables.getStackTraceAsString;
import static com.google.inject.name.Names.named;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.jclouds.Constants.PROPERTY_SCHEDULER_THREADS;
import static org.jclouds.concurrent.config.ExecutorServiceModuleTest.assertTraceHasSubmission;
import static org.jclouds.concurrent.config.ExecutorServiceModuleTest.incrementInitialElement;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.jclouds.lifecycle.Closer;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;

/**
 * 
 * @author Adrian Cole
 */
@Test
public class ScheduledExecutorServiceModuleTest {

   private Injector injector;

   @BeforeMethod
   private void setupExecutorModule() {
      ScheduledExecutorServiceModule module = new ScheduledExecutorServiceModule() {
         @Override
         protected void configure() {
            bindConstant().annotatedWith(named(PROPERTY_SCHEDULER_THREADS)).to(1);
            super.configure();
         }
      };

      injector = Guice.createInjector(module);
   }

   @AfterMethod
   private void close() throws IOException {
      injector.getInstance(Closer.class).close();
   }

   @Test
   public void testShutdownOnClose() throws IOException {
      Injector i = Guice.createInjector();

      Closer closer = i.getInstance(Closer.class);
      ListeningScheduledExecutorService executor = createMock(ListeningScheduledExecutorService.class);
      ExecutorServiceModule.shutdownOnClose(executor, closer);

      expect(executor.shutdownNow()).andReturn(ImmutableList.<Runnable> of()).atLeastOnce();

      replay(executor);
      closer.close();

      verify(executor);
   }

   @Test
   public void testShutdownOnCloseThroughModule() throws IOException {

      ListeningScheduledExecutorService sched = injector.getInstance(Key.get(ListeningScheduledExecutorService.class,
            named(PROPERTY_SCHEDULER_THREADS)));

      assertFalse(sched.isShutdown());

      injector.getInstance(Closer.class).close();

      assertTrue(sched.isShutdown());
   }

   @Test
   public void testExceptionInSubmitRunnableIncludesSubmissionTrace() throws Exception {
      ListeningScheduledExecutorService sched = injector.getInstance(Key.get(ListeningScheduledExecutorService.class,
            named(PROPERTY_SCHEDULER_THREADS)));
      String submission = null;
      try {
         // this is sensitive to formatting as we are looking for the stack traces to match. if you wrap the below
         // line again, you'll need to change incrementInitialElement to 3 line numbers instead of 2.
         submission = getStackTraceAsString(incrementInitialElement(new RuntimeException(), 2))
               .replaceFirst(".*\n", "");
         sched.submit(runnableThrowsRTE()).get();
      } catch (ExecutionException e) {
         assertTraceHasSubmission(getStackTraceAsString(e), submission);
         assertTraceHasSubmission(getStackTraceAsString(e.getCause()), submission);
      }
   }
   
   @Test
   public void testExceptionInScheduleWithFixedDelayRunnableIncludesSubmissionTrace() throws Exception {
      ListeningScheduledExecutorService sched = injector.getInstance(Key.get(ListeningScheduledExecutorService.class,
            named(PROPERTY_SCHEDULER_THREADS)));
      String submission = null;
      try {
         // this is sensitive to formatting as we are looking for the stack traces to match. if you wrap the below
         // line again, you'll need to change incrementInitialElement to 3 line numbers instead of 2.
         submission = getStackTraceAsString(incrementInitialElement(new RuntimeException(), 2))
               .replaceFirst(".*\n", "");
         sched.scheduleWithFixedDelay(runnableThrowsRTE(), 0, 1, TimeUnit.MICROSECONDS).get();
      } catch (ExecutionException e) {
         assertTraceHasSubmission(getStackTraceAsString(e), submission);
         assertTraceHasSubmission(getStackTraceAsString(e.getCause()), submission);
      }
   }

   static Runnable runnableThrowsRTE() {
      return new Runnable() {

         @Override
         public void run() {
            throw new RuntimeException();
         }

      };
   }
}
