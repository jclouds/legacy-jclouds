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
package org.jclouds.collect;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;

import org.easymock.EasyMock;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;

/**
 * Tests behavior of {@code AdvanceUntilEmptyIterable}.
 * 
 * @author Adrian Cole
 */
@Test(testName = "AdvanceUntilEmptyIterableTest")
public class AdvanceUntilEmptyIterableTest {

   @SuppressWarnings("unchecked")
   @Test
   public void testSinglePageResultReturnsSame() {

      FluentIterable<String> initial = FluentIterable.from(ImmutableSet.of("foo", "bar"));
      Supplier<FluentIterable<String>> nextIterable = createMock(Supplier.class);
      expect(nextIterable.get()).andReturn(initial);

      EasyMock.replay(nextIterable);

      AdvanceUntilEmptyIterable<String> iterable = new AdvanceUntilEmptyIterable<String>(nextIterable);

      Assert.assertSame(iterable.get(0), initial);

      EasyMock.verify(nextIterable);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testConcatStopsWhenEmpty() {

      Supplier<FluentIterable<String>> nextIterable = createMock(Supplier.class);

      expect(nextIterable.get()).andReturn(FluentIterable.from(ImmutableSet.of("foo", "bar")));
      expect(nextIterable.get()).andReturn(FluentIterable.from(ImmutableSet.of("boo", "baz")));

      expect(nextIterable.get()).andReturn(FluentIterable.from(ImmutableSet.of("ham", "cheeze")));
      expect(nextIterable.get()).andReturn(FluentIterable.from(ImmutableSet.<String>of()));

      EasyMock.replay(nextIterable);

      AdvanceUntilEmptyIterable<String> iterable = new AdvanceUntilEmptyIterable<String>(nextIterable);

      Assert.assertEquals(iterable.concat().toSet(),
            ImmutableSet.of("foo", "bar", "boo", "baz", "ham", "cheeze"));

      EasyMock.verify(nextIterable);

   }

}
