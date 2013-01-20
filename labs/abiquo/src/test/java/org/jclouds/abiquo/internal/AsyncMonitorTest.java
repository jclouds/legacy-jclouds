/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.internal;

import static org.easymock.EasyMock.anyLong;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.easymock.EasyMock;
import org.jclouds.abiquo.events.monitor.MonitorEvent;
import org.jclouds.abiquo.internal.BaseMonitoringService.AsyncMonitor;
import org.jclouds.abiquo.monitor.MonitorStatus;
import org.jclouds.rest.RestContext;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

/**
 * Unit tests for the {@link AsyncMonitor} class.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "unit", testName = "AsyncMonitorTest")
public class AsyncMonitorTest {
   @SuppressWarnings({ "rawtypes", "unchecked" })
   public void testStartMonitoringWithoutTimeout() {
      ScheduledFuture mockFuture = EasyMock.createMock(ScheduledFuture.class);
      ScheduledExecutorService schedulerMock = EasyMock.createMock(ScheduledExecutorService.class);
      expect(
            schedulerMock.scheduleWithFixedDelay(anyObject(Runnable.class), anyLong(), anyLong(),
                  anyObject(TimeUnit.class))).andReturn(mockFuture);

      replay(mockFuture);
      replay(schedulerMock);

      AsyncMonitor<Object> monitor = mockMonitor(schedulerMock, new Object(), mockFunction(MonitorStatus.DONE),
            new EventBus());

      assertNull(monitor.getFuture());
      assertNull(monitor.getTimeout());

      monitor.startMonitoring(null, TimeUnit.MILLISECONDS);

      assertNotNull(monitor.getFuture());
      assertNull(monitor.getTimeout());

      verify(mockFuture);
      verify(schedulerMock);
   }

   @Test(expectedExceptions = NullPointerException.class, expectedExceptionsMessageRegExp = "timeUnit must not be null when using timeouts")
   public void testStartMonitoringWithNullTimeout() {
      ScheduledExecutorService schedulerMock = EasyMock.createMock(ScheduledExecutorService.class);
      AsyncMonitor<Object> monitor = mockMonitor(schedulerMock, new Object(), mockFunction(MonitorStatus.DONE),
            new EventBus());

      monitor.startMonitoring(100L, null);
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   public void testStartMonitoringWithoutTimeoutAndNullTimeUnit() {
      ScheduledFuture mockFuture = EasyMock.createMock(ScheduledFuture.class);
      ScheduledExecutorService schedulerMock = EasyMock.createMock(ScheduledExecutorService.class);
      expect(
            schedulerMock.scheduleWithFixedDelay(anyObject(Runnable.class), anyLong(), anyLong(),
                  anyObject(TimeUnit.class))).andReturn(mockFuture);

      replay(mockFuture);
      replay(schedulerMock);

      AsyncMonitor<Object> monitor = mockMonitor(schedulerMock, new Object(), mockFunction(MonitorStatus.DONE),
            new EventBus());

      assertNull(monitor.getFuture());
      assertNull(monitor.getTimeout());

      // If the maxWait parameter is null, timeUnit is not required
      monitor.startMonitoring(null, null);

      assertNotNull(monitor.getFuture());
      assertNull(monitor.getTimeout());

      verify(mockFuture);
      verify(schedulerMock);
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   public void testStartMonitoringWithTimeout() {
      ScheduledFuture mockFuture = EasyMock.createMock(ScheduledFuture.class);
      ScheduledExecutorService schedulerMock = EasyMock.createMock(ScheduledExecutorService.class);
      expect(
            schedulerMock.scheduleWithFixedDelay(anyObject(Runnable.class), anyLong(), anyLong(),
                  anyObject(TimeUnit.class))).andReturn(mockFuture);

      replay(mockFuture);
      replay(schedulerMock);

      AsyncMonitor<Object> monitor = mockMonitor(schedulerMock, new Object(), mockFunction(MonitorStatus.DONE),
            new EventBus());

      assertNull(monitor.getFuture());
      assertNull(monitor.getTimeout());

      monitor.startMonitoring(100L, TimeUnit.MILLISECONDS);

      assertNotNull(monitor.getFuture());
      assertNotNull(monitor.getTimeout());
      assertTrue(monitor.getTimeout() > 100L);

      verify(mockFuture);
      verify(schedulerMock);
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   public void testStartMonitoringWithTimeoutInMinutes() {
      ScheduledFuture mockFuture = EasyMock.createMock(ScheduledFuture.class);
      ScheduledExecutorService schedulerMock = EasyMock.createMock(ScheduledExecutorService.class);
      expect(
            schedulerMock.scheduleWithFixedDelay(anyObject(Runnable.class), anyLong(), anyLong(),
                  anyObject(TimeUnit.class))).andReturn(mockFuture);

      replay(mockFuture);
      replay(schedulerMock);

      AsyncMonitor<Object> monitor = mockMonitor(schedulerMock, new Object(), mockFunction(MonitorStatus.DONE),
            new EventBus());

      assertNull(monitor.getFuture());
      assertNull(monitor.getTimeout());

      monitor.startMonitoring(1L, TimeUnit.MINUTES);

      assertNotNull(monitor.getFuture());
      assertNotNull(monitor.getTimeout());
      assertTrue(monitor.getTimeout() > TimeUnit.MINUTES.toMillis(1));

      verify(mockFuture);
      verify(schedulerMock);
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   public void testIsTimeoutWhenNullTimeout() {
      ScheduledFuture mockFuture = EasyMock.createMock(ScheduledFuture.class);
      ScheduledExecutorService schedulerMock = EasyMock.createMock(ScheduledExecutorService.class);
      expect(
            schedulerMock.scheduleWithFixedDelay(anyObject(Runnable.class), anyLong(), anyLong(),
                  anyObject(TimeUnit.class))).andReturn(mockFuture);

      replay(mockFuture);
      replay(schedulerMock);

      AsyncMonitor<Object> monitor = mockMonitor(schedulerMock, new Object(), mockFunction(MonitorStatus.DONE),
            new EventBus());

      assertNull(monitor.getFuture());
      assertNull(monitor.getTimeout());

      monitor.startMonitoring(null, TimeUnit.MILLISECONDS);
      assertNotNull(monitor.getFuture());
      assertNull(monitor.getTimeout());
      assertFalse(monitor.isTimeout());

      verify(mockFuture);
      verify(schedulerMock);
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   public void testIsTimeoutReturnsFalseWhenNotFinished() {
      ScheduledFuture mockFuture = EasyMock.createMock(ScheduledFuture.class);
      ScheduledExecutorService schedulerMock = EasyMock.createMock(ScheduledExecutorService.class);
      expect(
            schedulerMock.scheduleWithFixedDelay(anyObject(Runnable.class), anyLong(), anyLong(),
                  anyObject(TimeUnit.class))).andReturn(mockFuture);

      replay(mockFuture);
      replay(schedulerMock);

      AsyncMonitor<Object> monitor = mockMonitor(schedulerMock, new Object(), mockFunction(MonitorStatus.DONE),
            new EventBus());

      assertNull(monitor.getFuture());
      assertNull(monitor.getTimeout());

      monitor.startMonitoring(60000L, TimeUnit.MILLISECONDS);
      assertNotNull(monitor.getFuture());
      assertNotNull(monitor.getTimeout());
      assertFalse(monitor.isTimeout());

      verify(mockFuture);
      verify(schedulerMock);
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   public void testIsTimeoutReturnsTrueWhenFinished() throws InterruptedException {
      ScheduledFuture mockFuture = EasyMock.createMock(ScheduledFuture.class);
      ScheduledExecutorService schedulerMock = EasyMock.createMock(ScheduledExecutorService.class);
      expect(
            schedulerMock.scheduleWithFixedDelay(anyObject(Runnable.class), anyLong(), anyLong(),
                  anyObject(TimeUnit.class))).andReturn(mockFuture);

      replay(mockFuture);
      replay(schedulerMock);

      AsyncMonitor<Object> monitor = mockMonitor(schedulerMock, new Object(), mockFunction(MonitorStatus.DONE),
            new EventBus());

      assertNull(monitor.getFuture());
      assertNull(monitor.getTimeout());

      monitor.startMonitoring(1L, TimeUnit.MILLISECONDS);
      Thread.sleep(2L);
      assertNotNull(monitor.getFuture());
      assertNotNull(monitor.getTimeout());
      assertTrue(monitor.isTimeout());

      verify(mockFuture);
      verify(schedulerMock);
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   public void testStopMonitoringWhenFutureIsCancelled() {
      ScheduledFuture mockFuture = EasyMock.createMock(ScheduledFuture.class);
      expect(mockFuture.isCancelled()).andReturn(true);

      ScheduledExecutorService schedulerMock = EasyMock.createMock(ScheduledExecutorService.class);
      expect(
            schedulerMock.scheduleWithFixedDelay(anyObject(Runnable.class), anyLong(), anyLong(),
                  anyObject(TimeUnit.class))).andReturn(mockFuture);

      replay(mockFuture);
      replay(schedulerMock);

      AsyncMonitor<Object> monitor = mockMonitor(schedulerMock, new Object(), mockFunction(MonitorStatus.DONE),
            new EventBus());

      assertNull(monitor.getFuture());
      assertNull(monitor.getTimeout());

      monitor.startMonitoring(null, TimeUnit.MILLISECONDS);
      assertNotNull(monitor.getFuture());
      assertNull(monitor.getTimeout());

      monitor.stopMonitoring();

      verify(mockFuture);
      verify(schedulerMock);
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   public void testStopMonitoringWhenFutureIsDone() {
      ScheduledFuture mockFuture = EasyMock.createMock(ScheduledFuture.class);
      expect(mockFuture.isCancelled()).andReturn(false);
      expect(mockFuture.isDone()).andReturn(true);

      ScheduledExecutorService schedulerMock = EasyMock.createMock(ScheduledExecutorService.class);
      expect(
            schedulerMock.scheduleWithFixedDelay(anyObject(Runnable.class), anyLong(), anyLong(),
                  anyObject(TimeUnit.class))).andReturn(mockFuture);

      replay(mockFuture);
      replay(schedulerMock);

      AsyncMonitor<Object> monitor = mockMonitor(schedulerMock, new Object(), mockFunction(MonitorStatus.DONE),
            new EventBus());

      assertNull(monitor.getFuture());
      assertNull(monitor.getTimeout());

      monitor.startMonitoring(null, TimeUnit.MILLISECONDS);
      assertNotNull(monitor.getFuture());
      assertNull(monitor.getTimeout());

      monitor.stopMonitoring();

      verify(mockFuture);
      verify(schedulerMock);
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   public void testStopMonitoringWhenFutureIsNotComplete() {
      ScheduledFuture mockFuture = EasyMock.createMock(ScheduledFuture.class);
      expect(mockFuture.isCancelled()).andReturn(false);
      expect(mockFuture.isDone()).andReturn(false);
      expect(mockFuture.cancel(false)).andReturn(true);

      ScheduledExecutorService schedulerMock = EasyMock.createMock(ScheduledExecutorService.class);
      expect(
            schedulerMock.scheduleWithFixedDelay(anyObject(Runnable.class), anyLong(), anyLong(),
                  anyObject(TimeUnit.class))).andReturn(mockFuture);

      replay(mockFuture);
      replay(schedulerMock);

      AsyncMonitor<Object> monitor = mockMonitor(schedulerMock, new Object(), mockFunction(MonitorStatus.DONE),
            new EventBus());

      assertNull(monitor.getFuture());
      assertNull(monitor.getTimeout());

      monitor.startMonitoring(null, TimeUnit.MILLISECONDS);
      assertNotNull(monitor.getFuture());
      assertNull(monitor.getTimeout());

      monitor.stopMonitoring();

      verify(mockFuture);
      verify(schedulerMock);
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   public void testMonitorAndDone() {
      ScheduledFuture mockFuture = EasyMock.createMock(ScheduledFuture.class);
      expect(mockFuture.isCancelled()).andReturn(true);

      ScheduledExecutorService schedulerMock = EasyMock.createMock(ScheduledExecutorService.class);
      expect(
            schedulerMock.scheduleWithFixedDelay(anyObject(Runnable.class), anyLong(), anyLong(),
                  anyObject(TimeUnit.class))).andReturn(mockFuture);

      replay(mockFuture);
      replay(schedulerMock);

      CoutingEventHandler handler = new CoutingEventHandler();
      EventBus eventBus = new EventBus();
      eventBus.register(handler);

      AsyncMonitor<Object> monitor = mockMonitor(schedulerMock, new Object(), mockFunction(MonitorStatus.DONE),
            eventBus);

      assertNull(monitor.getFuture());
      assertNull(monitor.getTimeout());

      monitor.startMonitoring(null, TimeUnit.MILLISECONDS);
      assertNotNull(monitor.getFuture());
      assertNull(monitor.getTimeout());

      monitor.run();
      assertEquals(handler.numCompletes, 1);
      assertEquals(handler.numFailures, 0);
      assertEquals(handler.numTimeouts, 0);

      verify(mockFuture);
      verify(schedulerMock);
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   public void testMonitorAndFail() {
      ScheduledFuture mockFuture = EasyMock.createMock(ScheduledFuture.class);
      expect(mockFuture.isCancelled()).andReturn(true);

      ScheduledExecutorService schedulerMock = EasyMock.createMock(ScheduledExecutorService.class);
      expect(
            schedulerMock.scheduleWithFixedDelay(anyObject(Runnable.class), anyLong(), anyLong(),
                  anyObject(TimeUnit.class))).andReturn(mockFuture);

      replay(mockFuture);
      replay(schedulerMock);

      CoutingEventHandler handler = new CoutingEventHandler();
      EventBus eventBus = new EventBus();
      eventBus.register(handler);

      AsyncMonitor<Object> monitor = mockMonitor(schedulerMock, new Object(), mockFunction(MonitorStatus.FAILED),
            eventBus);

      assertNull(monitor.getFuture());
      assertNull(monitor.getTimeout());

      monitor.startMonitoring(null, TimeUnit.MILLISECONDS);
      assertNotNull(monitor.getFuture());
      assertNull(monitor.getTimeout());

      monitor.run();
      assertEquals(handler.numCompletes, 0);
      assertEquals(handler.numFailures, 1);
      assertEquals(handler.numTimeouts, 0);

      verify(mockFuture);
      verify(schedulerMock);
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   public void testMonitorAndContinueWithoutTimeout() {
      ScheduledFuture mockFuture = EasyMock.createMock(ScheduledFuture.class);
      ScheduledExecutorService schedulerMock = EasyMock.createMock(ScheduledExecutorService.class);
      expect(
            schedulerMock.scheduleWithFixedDelay(anyObject(Runnable.class), anyLong(), anyLong(),
                  anyObject(TimeUnit.class))).andReturn(mockFuture);

      replay(mockFuture);
      replay(schedulerMock);

      CoutingEventHandler handler = new CoutingEventHandler();
      EventBus eventBus = new EventBus();
      eventBus.register(handler);

      AsyncMonitor<Object> monitor = mockMonitor(schedulerMock, new Object(), mockFunction(MonitorStatus.CONTINUE),
            eventBus);

      assertNull(monitor.getFuture());
      assertNull(monitor.getTimeout());

      monitor.startMonitoring(null, TimeUnit.MILLISECONDS);
      assertNotNull(monitor.getFuture());
      assertNull(monitor.getTimeout());

      monitor.run();
      assertEquals(handler.numCompletes, 0);
      assertEquals(handler.numFailures, 0);
      assertEquals(handler.numTimeouts, 0);

      verify(mockFuture);
      verify(schedulerMock);
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   public void testMonitorAndContinueWithtTimeout() throws InterruptedException {
      ScheduledFuture mockFuture = EasyMock.createMock(ScheduledFuture.class);
      expect(mockFuture.isCancelled()).andReturn(true);

      ScheduledExecutorService schedulerMock = EasyMock.createMock(ScheduledExecutorService.class);
      expect(
            schedulerMock.scheduleWithFixedDelay(anyObject(Runnable.class), anyLong(), anyLong(),
                  anyObject(TimeUnit.class))).andReturn(mockFuture);

      replay(mockFuture);
      replay(schedulerMock);

      CoutingEventHandler handler = new CoutingEventHandler();
      EventBus eventBus = new EventBus();
      eventBus.register(handler);

      AsyncMonitor<Object> monitor = mockMonitor(schedulerMock, new Object(), mockFunction(MonitorStatus.CONTINUE),
            eventBus);

      assertNull(monitor.getFuture());
      assertNull(monitor.getTimeout());

      monitor.startMonitoring(1L, TimeUnit.MILLISECONDS);
      assertNotNull(monitor.getFuture());
      assertNotNull(monitor.getTimeout());

      Thread.sleep(2L);
      monitor.run();
      assertEquals(handler.numCompletes, 0);
      assertEquals(handler.numFailures, 0);
      assertEquals(handler.numTimeouts, 1);

      verify(mockFuture);
      verify(schedulerMock);
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testCreateMonitorWithNullObject() {
      mockMonitor(null, null, new Function<Object, MonitorStatus>() {
         @Override
         public MonitorStatus apply(final Object input) {
            return MonitorStatus.DONE;
         }
      }, new EventBus());
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testCreateMonitorWithNullFunction() {
      mockMonitor(null, new Object(), null, new EventBus());
   }

   @SuppressWarnings("unchecked")
   private AsyncMonitor<Object> mockMonitor(final ScheduledExecutorService scheduler, final Object object,
         final Function<Object, MonitorStatus> function, final EventBus eventBus) {
      BaseMonitoringService monitorService = new BaseMonitoringService(EasyMock.createMock(RestContext.class),
            scheduler, 100L, eventBus);

      return monitorService.new AsyncMonitor<Object>(object, function);
   }

   private Function<Object, MonitorStatus> mockFunction(final MonitorStatus status) {
      return new Function<Object, MonitorStatus>() {
         @Override
         public MonitorStatus apply(final Object input) {
            return status;
         }
      };
   }

   static class CoutingEventHandler {
      public int numCompletes = 0;

      public int numFailures = 0;

      public int numTimeouts = 0;

      @Subscribe
      public void handle(final MonitorEvent<?> event) {
         switch (event.getType()) {
            case COMPLETED:
               numCompletes++;
               break;
            case FAILED:
               numFailures++;
               break;
            case TIMEOUT:
               numTimeouts++;
               break;
         }
      }
   }
}
