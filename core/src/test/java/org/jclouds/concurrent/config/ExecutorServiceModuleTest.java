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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.jclouds.Constants;
import org.jclouds.lifecycle.Closer;
import org.testng.annotations.Test;

import com.google.common.base.Throwables;
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
   
   @Test
   public void testDescribedFutureToString() throws Exception {
      
      ExecutorServiceModule module = new ExecutorServiceModule() {
         @Override
         protected void configure() {
            bindConstant().annotatedWith(Names.named(Constants.PROPERTY_IO_WORKER_THREADS)).to(1);
            bindConstant().annotatedWith(Names.named(Constants.PROPERTY_USER_THREADS)).to(1);
            super.configure();
         }
      };
      
      Injector i = Guice.createInjector(module);
      Closer closer = i.getInstance(Closer.class);
      
      ExecutorService user = i
               .getInstance(Key.get(ExecutorService.class, Names.named(Constants.PROPERTY_USER_THREADS)));
      ExecutorService io = i.getInstance(Key.get(ExecutorService.class, Names
               .named(Constants.PROPERTY_IO_WORKER_THREADS)));

      ConfigurableRunner t1 = new ConfigurableRunner();
      t1.result = "okay";
      
      Future<Object> euc = performSubmissionInSeparateMethod1(user, t1);
      assert euc.toString().indexOf("ConfigurableRunner") >= 0;
      assert euc.get().equals("okay");

      Future<Object> eic = performSubmissionInSeparateMethod1(io, t1);
      assert eic.toString().indexOf("ConfigurableRunner") >= 0;
      assert eic.get().equals("okay");

      
      closer.close();
   }

   /*
    * The decoration makes sure that the stack trace looks like the following.
    * Note the last three included trace elements: this details where the task was submitted _from_
    * (technically it is a different stack frame, since it is across threads; but logically it is the same)
    * 
java.util.concurrent.ExecutionException: java.lang.IllegalStateException: foo
   at java.util.concurrent.FutureTask$Sync.innerGet(FutureTask.java:222)
   at java.util.concurrent.FutureTask.get(FutureTask.java:83)
   at org.jclouds.concurrent.config.ExecutorServiceModule$DescribedFuture.get(ExecutorServiceModule.java:232)
   at org.jclouds.concurrent.config.ExecutorServiceModuleTest.checkFutureGetFailsWith(ExecutorServiceModuleTest.java:186)
   at org.jclouds.concurrent.config.ExecutorServiceModuleTest.testDescribedFutureExceptionIncludesSubmissionTrace(ExecutorServiceModuleTest.java:171)
   at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
   at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)
   at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)
   at java.lang.reflect.Method.invoke(Method.java:597)
   at org.testng.internal.MethodInvocationHelper.invokeMethod(MethodInvocationHelper.java:80)
   at org.testng.internal.Invoker.invokeMethod(Invoker.java:691)
   at org.testng.internal.Invoker.invokeTestMethod(Invoker.java:883)
   at org.testng.internal.Invoker.invokeTestMethods(Invoker.java:1208)
   at org.testng.internal.TestMethodWorker.invokeTestMethods(TestMethodWorker.java:127)
   at org.testng.internal.TestMethodWorker.run(TestMethodWorker.java:111)
   at org.testng.TestRunner.privateRun(TestRunner.java:753)
   at org.testng.TestRunner.run(TestRunner.java:613)
   at org.testng.SuiteRunner.runTest(SuiteRunner.java:335)
   at org.testng.SuiteRunner.runSequentially(SuiteRunner.java:330)
   at org.testng.SuiteRunner.privateRun(SuiteRunner.java:292)
   at org.testng.SuiteRunner.run(SuiteRunner.java:241)
   at org.testng.SuiteRunnerWorker.runSuite(SuiteRunnerWorker.java:52)
   at org.testng.SuiteRunnerWorker.run(SuiteRunnerWorker.java:86)
   at org.testng.TestNG.runSuitesSequentially(TestNG.java:1169)
   at org.testng.TestNG.runSuitesLocally(TestNG.java:1094)
   at org.testng.TestNG.run(TestNG.java:1006)
   at org.testng.remote.RemoteTestNG.run(RemoteTestNG.java:107)
   at org.testng.remote.RemoteTestNG.initAndRun(RemoteTestNG.java:199)
   at org.testng.remote.RemoteTestNG.main(RemoteTestNG.java:170)
Caused by: java.lang.IllegalStateException: foo
   at org.jclouds.concurrent.config.ExecutorServiceModuleTest$ConfigurableRunner.call(ExecutorServiceModuleTest.java:206)
   at org.jclouds.concurrent.config.ExecutorServiceModuleTest$ConfigurableRunner.run(ExecutorServiceModuleTest.java:203)
   at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:441)
   at java.util.concurrent.FutureTask$Sync.innerRun(FutureTask.java:303)
   at java.util.concurrent.FutureTask.run(FutureTask.java:138)
   at java.util.concurrent.ThreadPoolExecutor$Worker.runTask(ThreadPoolExecutor.java:886)
   at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:908)
   at java.lang.Thread.run(Thread.java:637)
   at org.jclouds.concurrent.config.ExecutorServiceModule$DescribingExecutorService.submit(ExecutorServiceModule.java:188)
   at org.jclouds.concurrent.config.ExecutorServiceModuleTest.performSubmissionInSeparateMethod2(ExecutorServiceModuleTest.java:181)
   at org.jclouds.concurrent.config.ExecutorServiceModuleTest.testDescribedFutureExceptionIncludesSubmissionTrace(ExecutorServiceModuleTest.java:170)
   ... 24 more

    * 
    */
   @Test
   public void testDescribedFutureExceptionIncludesSubmissionTrace() throws Exception {
      
      ExecutorServiceModule module = new ExecutorServiceModule() {
         @Override
         protected void configure() {
            bindConstant().annotatedWith(Names.named(Constants.PROPERTY_IO_WORKER_THREADS)).to(1);
            bindConstant().annotatedWith(Names.named(Constants.PROPERTY_USER_THREADS)).to(1);
            super.configure();
         }
      };

      Injector i = Guice.createInjector(module);
      Closer closer = i.getInstance(Closer.class);
      
      ExecutorService user = i
               .getInstance(Key.get(ExecutorService.class, Names.named(Constants.PROPERTY_USER_THREADS)));
      ExecutorService io = i.getInstance(Key.get(ExecutorService.class, Names
               .named(Constants.PROPERTY_IO_WORKER_THREADS)));

      ConfigurableRunner t1 = new ConfigurableRunner();
      t1.failMessage = "foo";
      t1.result = "shouldn't happen";
      
      Future<Object> euc = performSubmissionInSeparateMethod1(user, t1);
      checkFutureGetFailsWith(euc, "foo", "testDescribedFutureExceptionIncludesSubmissionTrace", "performSubmissionInSeparateMethod1");
      
      Future<Object> eur = performSubmissionInSeparateMethod2(user, t1);
      checkFutureGetFailsWith(eur, "foo", "testDescribedFutureExceptionIncludesSubmissionTrace", "performSubmissionInSeparateMethod2");
      
      Future<Object> eic = performSubmissionInSeparateMethod1(io, t1);
      checkFutureGetFailsWith(eic, "foo", "testDescribedFutureExceptionIncludesSubmissionTrace", "performSubmissionInSeparateMethod1");
      
      Future<Object> eir = performSubmissionInSeparateMethod2(io, t1);
      checkFutureGetFailsWith(eir, "foo", "testDescribedFutureExceptionIncludesSubmissionTrace", "performSubmissionInSeparateMethod2");
      
      closer.close();
   }

   static Future<Object> performSubmissionInSeparateMethod1(ExecutorService user, ConfigurableRunner t1) {
      return user.submit((Callable<Object>)t1);
   }
   
   static Future<Object> performSubmissionInSeparateMethod2(ExecutorService io, ConfigurableRunner t1) {
      return io.submit((Runnable)t1, (Object)"shouldn't happen");
   }
   
   static void checkFutureGetFailsWith(Future<?> task, String ...requiredPhrases) throws Exception {
      try {
         task.get();
         fail("task should have failed");
      } catch (ExecutionException e) {
         String trace = Throwables.getStackTraceAsString(e);
         for (String requiredPhrase : requiredPhrases) {
            assert trace.indexOf(requiredPhrase) >= 0 : "stack trace should have contained '"+requiredPhrase+"'";
         }
      }
   }

   static class ConfigurableRunner implements Runnable, Callable<Object> {
      Object result;
      String failMessage;
      
      @Override
      public void run() {
         call();
      }
      public Object call() {
         if (failMessage!=null) throw new IllegalStateException(failMessage);
         return result;
      }
   }
}
