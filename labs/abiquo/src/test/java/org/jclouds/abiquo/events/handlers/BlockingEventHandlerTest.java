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

package org.jclouds.abiquo.events.handlers;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jclouds.abiquo.events.monitor.MonitorEvent;
import org.jclouds.abiquo.events.monitor.MonitorEvent.Type;
import org.testng.annotations.Test;

/**
 * Unit tests for the {@link BlockingEventHandler} handler.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "unit", testName = "BlockingEventHandlerTest")
public class BlockingEventHandlerTest {
   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testConstructorWithoutObjects() {
      new BlockingEventHandler<Object>();
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testConstructorWithNullObjects() {
      new BlockingEventHandler<Object>((Object[]) null);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testConstructorWithtEmptyObjects() {
      new BlockingEventHandler<Object>(new Object[] {});
   }

   public void testHandles() {
      Object object = new Object();
      BlockingEventHandler<Object> handler = new BlockingEventHandler<Object>(object);

      assertTrue(handler.handles(new MonitorEvent<Object>(Type.COMPLETED, object)));
      assertFalse(handler.handles(new MonitorEvent<Object>(Type.COMPLETED, new Object())));
   }

   public void testReleaseDoesNothingIfNotLocked() {
      Object object = new Object();
      BlockingEventHandler<Object> handler = new BlockingEventHandler<Object>(object);
      handler.release(object);
   }

   public void testRelease() {
      final Object object = new Object();
      final BlockingEventHandler<Object> handler = new BlockingEventHandler<Object>(object);

      // Unlock the handler (in a separate thread) after a certain delay
      Executors.newSingleThreadScheduledExecutor().schedule(new Runnable() {
         @Override
         public void run() {
            handler.release(object);
            assertTrue(handler.lockedObjects.isEmpty());
         }

      }, 500L, TimeUnit.MILLISECONDS);

      handler.lock();
   }

   public void testHandle() {
      final Object object = new Object();
      final BlockingEventHandler<Object> handler = new BlockingEventHandler<Object>(object);

      // Unlock the handler (in a separate thread) after a certain delay
      Executors.newSingleThreadScheduledExecutor().schedule(new Runnable() {
         @Override
         public void run() {
            handler.handle(new MonitorEvent<Object>(Type.COMPLETED, object));
            assertTrue(handler.lockedObjects.isEmpty());
         }

      }, 500L, TimeUnit.MILLISECONDS);

      handler.lock();
   }

   public void testLockDoesNothingIfNoObjects() {
      Object object = new Object();
      BlockingEventHandler<Object> handler = new BlockingEventHandler<Object>(object);
      handler.lockedObjects.clear();

      handler.lock(); // Lock should do nothing

      assertNull(handler.completeSignal);
   }
}
