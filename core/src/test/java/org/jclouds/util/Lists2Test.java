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
package org.jclouds.util;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class Lists2Test {

   public void testMultiMax() {
      Iterable<String> values = ImmutableList.of("1", "2", "2", "3", "3");
      assertEquals(Lists2.multiMax(Ordering.natural(), values), ImmutableList.of("3", "3"));
   }

   public void testMultiMax1() {
      Iterable<String> values = ImmutableList.of("1", "2", "2", "3");
      assertEquals(Lists2.multiMax(Ordering.natural(), values), ImmutableList.of("3"));
   }

}
