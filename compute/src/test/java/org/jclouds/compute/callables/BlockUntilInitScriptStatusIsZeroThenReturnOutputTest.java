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
package org.jclouds.compute.callables;
import static org.easymock.EasyMock.createMockBuilder;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.jclouds.compute.callables.BlockUntilInitScriptStatusIsZeroThenReturnOutput.loopUntilTrueOrThrowCancellationException;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jclouds.compute.callables.BlockUntilInitScriptStatusIsZeroThenReturnOutput.ExitStatusOfCommandGreaterThanZero;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.scriptbuilder.InitScript;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.AbstractFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", singleThreaded = true, testName = "BlockUntilInitScriptStatusIsZeroThenReturnOutputTest")
public class BlockUntilInitScriptStatusIsZeroThenReturnOutputTest {

   public void testloopUntilTrueOrThrowCancellationExceptionReturnsWhenPredicateIsTrue() {
      AbstractFuture<ExecResponse> future = new AbstractFuture<ExecResponse>() {

         @Override
         public boolean isCancelled() {
            return false;
         }

      };

      Predicate<String> pred = loopUntilTrueOrThrowCancellationException(Predicates
               .<String> alwaysTrue(), 1, 1, future);
      assertEquals(pred.apply("foo"), true);

   }

   public void testloopUntilTrueOrThrowCancellationExceptionReturnsWhenPredicateIsTrueSecondTimeWhileNotCancelled() {
      AbstractFuture<ExecResponse> future = new AbstractFuture<ExecResponse>() {

         @Override
         public boolean isCancelled() {
            return false;
         }

      };

      Predicate<String> predicate = new Predicate<String>() {
         AtomicBoolean bool = new AtomicBoolean();

         @Override
         public boolean apply(String input) {
            return bool.getAndSet(true);
         }

      };

      Predicate<String> pred = loopUntilTrueOrThrowCancellationException(predicate, 1, 1, future);
      assertEquals(pred.apply("foo"), true);

   }

   // need to break the loop when cancelled.
   public void testloopUntilTrueOrThrowCancellationExceptionSkipsAndReturnsFalseOnCancelled() {
      AbstractFuture<ExecResponse> future = new AbstractFuture<ExecResponse>() {

         @Override
         public boolean isCancelled() {
            return true;
         }

      };
      Predicate<String> pred = loopUntilTrueOrThrowCancellationException(Predicates.<String> alwaysFalse(), 1, 1,
            future);
      assertEquals(pred.apply("foo"), false);

   }

   public void testExitStatusOfCommandGreaterThanZeroTrueWhen1() {

      SudoAwareInitManager commandRunner = createMockBuilder(SudoAwareInitManager.class).addMockedMethod("runAction")
               .createStrictMock();
      expect(commandRunner.runAction("status")).andReturn(new ExecResponse("", "", 1));
      replay(commandRunner);

      Predicate<String> pred = new ExitStatusOfCommandGreaterThanZero(commandRunner);
      assertEquals(pred.apply("status"), true);

      verify(commandRunner);

   }

   public void testExitStatusOfCommandGreaterThanZeroFalseWhen0() {

      SudoAwareInitManager commandRunner = createMockBuilder(SudoAwareInitManager.class).addMockedMethod("runAction")
               .createStrictMock();
      expect(commandRunner.runAction("status")).andReturn(new ExecResponse("", "", 0));
      replay(commandRunner);

      Predicate<String> pred = new ExitStatusOfCommandGreaterThanZero(commandRunner);
      assertEquals(pred.apply("status"), false);

      verify(commandRunner);

   }

   EventBus eventBus = new EventBus();

   public void testExitStatusZeroReturnsExecResponse() throws InterruptedException, ExecutionException {
      ListeningExecutorService userExecutor = MoreExecutors.sameThreadExecutor();
      Predicate<String> notRunningAnymore = Predicates.alwaysTrue();
      SudoAwareInitManager commandRunner = createMockBuilder(SudoAwareInitManager.class).addMockedMethod("runAction")
               .addMockedMethod("getStatement").addMockedMethod("getNode").addMockedMethod("toString")
               .createStrictMock();
      InitScript initScript = createMockBuilder(InitScript.class).addMockedMethod("getInstanceName").createStrictMock();

      expect(commandRunner.runAction("stdout")).andReturn(new ExecResponse("stdout", "", 0));
      expect(commandRunner.runAction("stderr")).andReturn(new ExecResponse("stderr", "", 0));
      expect(commandRunner.runAction("exitstatus")).andReturn(new ExecResponse("444\n", "", 0));

      toStringAndEventBusExpectations(commandRunner, initScript);

      replay(commandRunner, initScript);

      BlockUntilInitScriptStatusIsZeroThenReturnOutput future = new BlockUntilInitScriptStatusIsZeroThenReturnOutput(
               userExecutor, eventBus, notRunningAnymore, commandRunner);

      future.run();

      assertEquals(future.get(), new ExecResponse("stdout", "stderr", 444));

      verify(commandRunner, initScript);

   }

   public void testFirstExitStatusOneButSecondExitStatusZeroReturnsExecResponse() throws InterruptedException,
            ExecutionException {
      ListeningExecutorService userExecutor = MoreExecutors.sameThreadExecutor();
      Predicate<String> notRunningAnymore = Predicates.alwaysTrue();

      SudoAwareInitManager commandRunner = createMockBuilder(SudoAwareInitManager.class).addMockedMethod("runAction")
               .addMockedMethod("getStatement").addMockedMethod("getNode").addMockedMethod("toString")
               .createStrictMock();
      InitScript initScript = createMockBuilder(InitScript.class).addMockedMethod("getInstanceName").createStrictMock();

      // exit status is 1 means we are still running!
      expect(commandRunner.runAction("stdout")).andReturn(new ExecResponse("", "", 0));
      expect(commandRunner.runAction("stderr")).andReturn(new ExecResponse("", "", 0));
      expect(commandRunner.runAction("exitstatus")).andReturn(new ExecResponse("", "", 1));

      // second time around, it did stop
      expect(commandRunner.runAction("stdout")).andReturn(new ExecResponse("stdout", "", 0));
      expect(commandRunner.runAction("stderr")).andReturn(new ExecResponse("stderr", "", 0));
      expect(commandRunner.runAction("exitstatus")).andReturn(new ExecResponse("444\n", "", 0));

      toStringAndEventBusExpectations(commandRunner, initScript);

      replay(commandRunner, initScript);

      BlockUntilInitScriptStatusIsZeroThenReturnOutput future = new BlockUntilInitScriptStatusIsZeroThenReturnOutput(
               userExecutor, eventBus, notRunningAnymore, commandRunner);

      future.run();

      assertEquals(future.get(), new ExecResponse("stdout", "stderr", 444));

      verify(commandRunner, initScript);

   }

   public void testCancelInterruptStopsCommand() throws InterruptedException, ExecutionException {
      ListeningExecutorService userExecutor = MoreExecutors.sameThreadExecutor();
      Predicate<String> notRunningAnymore = Predicates.alwaysTrue();
      SudoAwareInitManager commandRunner = createMockBuilder(SudoAwareInitManager.class).addMockedMethod(
               "refreshAndRunAction").addMockedMethod("runAction").addMockedMethod("getStatement").addMockedMethod(
               "getNode").addMockedMethod("toString").createStrictMock();
      InitScript initScript = createMockBuilder(InitScript.class).addMockedMethod("getInstanceName").createStrictMock();

      // log what we are stopping
      expect(commandRunner.getStatement()).andReturn(initScript);
      expect(initScript.getInstanceName()).andReturn("init-script");

      // stop
      expect(commandRunner.refreshAndRunAction("stop")).andReturn(new ExecResponse("stdout", "", 0));

      // create cancellation exception
      expect(commandRunner.getStatement()).andReturn(initScript);
      expect(initScript.getInstanceName()).andReturn("init-script");
      expect(commandRunner.getNode()).andReturn(
               new NodeMetadataBuilder().ids("id").status(NodeMetadata.Status.RUNNING).build()).atLeastOnce();

      // StatementOnNodeFailure event
      expect(commandRunner.getStatement()).andReturn(initScript);
      expect(commandRunner.getNode()).andReturn(
               new NodeMetadataBuilder().ids("id").status(NodeMetadata.Status.RUNNING).build()).atLeastOnce();

      replay(commandRunner, initScript);

      BlockUntilInitScriptStatusIsZeroThenReturnOutput future = new BlockUntilInitScriptStatusIsZeroThenReturnOutput(
               userExecutor, eventBus, notRunningAnymore, commandRunner);

      future.cancel(true);

      try {
         future.get();
         fail();
      } catch (CancellationException e) {

      }

      verify(commandRunner, initScript);

   }

   public void testCancelDontInterruptLeavesCommandRunningAndReturnsLastStatus() throws InterruptedException,
            ExecutionException {
      ListeningExecutorService userExecutor = MoreExecutors.sameThreadExecutor();
      Predicate<String> notRunningAnymore = Predicates.alwaysTrue();
      SudoAwareInitManager commandRunner = createMockBuilder(SudoAwareInitManager.class).addMockedMethod("runAction")
               .addMockedMethod("getStatement").addMockedMethod("getNode").addMockedMethod("toString")
               .createStrictMock();
      InitScript initScript = createMockBuilder(InitScript.class).addMockedMethod("getInstanceName").createStrictMock();

      expect(commandRunner.runAction("stdout")).andReturn(new ExecResponse("stillrunning", "", 0));
      expect(commandRunner.runAction("stderr")).andReturn(new ExecResponse("", "", 0));
      expect(commandRunner.runAction("exitstatus")).andReturn(new ExecResponse("", "", 1));

      toStringAndEventBusExpectations(commandRunner, initScript);

      replay(commandRunner, initScript);

      BlockUntilInitScriptStatusIsZeroThenReturnOutput future = new BlockUntilInitScriptStatusIsZeroThenReturnOutput(
               userExecutor, eventBus, notRunningAnymore, commandRunner);

      future.cancel(false);

      // note if this didn't cancel properly, the loop would never end!
      future.run();

      try {
         future.get();
         fail();
      } catch (CancellationException e) {

      }
      verify(commandRunner, initScript);

   }

   private void toStringAndEventBusExpectations(SudoAwareInitManager commandRunner, InitScript initScript) {
      toStringExpectations(commandRunner, initScript);
      expect(commandRunner.getStatement()).andReturn(initScript);
      expect(commandRunner.getNode()).andReturn(
               new NodeMetadataBuilder().ids("id").status(NodeMetadata.Status.RUNNING).build());
   }

   private void toStringExpectations(SudoAwareInitManager commandRunner, InitScript initScript) {
      expect(commandRunner.getStatement()).andReturn(initScript);
      expect(initScript.getInstanceName()).andReturn("init-script");
   }
}
