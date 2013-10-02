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
package org.jclouds.functions;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", sequential = true)
public class JoinOnCommaTest {
   @Test
   public void testIterableLong() {
      String list = new JoinOnComma().apply(ImmutableList.of(1l, 2l));
      assertEquals(list, "1,2");
   }

   @Test
   public void testLongArray() {
      String list = new JoinOnComma().apply(new long[] { 1l, 2l });
      assertEquals(list, "1,2");
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testEmptyArrayIllegalArgumentException() {
      new JoinOnComma().apply(new long[] {});
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testEmptyIterableIllegalArgumentException() {
      new JoinOnComma().apply(ImmutableList.of());
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testNullPointer() {
      new JoinOnComma().apply(null);
   }
}
