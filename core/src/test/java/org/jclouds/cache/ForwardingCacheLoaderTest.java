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

package org.jclouds.cache;

import static org.easymock.EasyMock.createMockBuilder;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.cache.CacheLoader;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Unit test for {@link ForwardingCacheLoader}.
 * 
 * @author Adrian Cole
 */
@Test(testName = "ForwardingCacheLoaderTest", singleThreaded = true)
public class ForwardingCacheLoaderTest {
   private CacheLoader<String, Boolean> forward;
   private CacheLoader<String, Boolean> mock;

   @SuppressWarnings("unchecked")
   @BeforeMethod
   public void setUp() {
      // add mocked methods for default forwarded ones
      mock = createMockBuilder(CacheLoader.class).addMockedMethods("loadAll", "reload").createMock();
      forward = new ForwardingCacheLoader<String, Boolean>() {
         @Override
         protected CacheLoader<String, Boolean> delegate() {
            return mock;
         }
      };
   }

   public void testLoad() throws Exception {
      expect(mock.load("key")).andReturn(Boolean.TRUE);
      replay(mock);
      assertSame(Boolean.TRUE, forward.load("key"));
      verify(mock);
   }

   public void testReload() throws Exception {
      ListenableFuture<Boolean> trueF = Futures.immediateFuture(true);
      expect(mock.reload("key", false)).andReturn(trueF);
      replay(mock);
      assertSame(forward.reload("key", false), trueF);
      verify(mock);
   }

   public void testLoadAll() throws Exception {
      expect(mock.loadAll(ImmutableList.of("key"))).andReturn(ImmutableMap.of("key", Boolean.TRUE));
      replay(mock);
      assertEquals(ImmutableMap.of("key", Boolean.TRUE), forward.loadAll(ImmutableList.of("key")));
      verify(mock);
   }
}
