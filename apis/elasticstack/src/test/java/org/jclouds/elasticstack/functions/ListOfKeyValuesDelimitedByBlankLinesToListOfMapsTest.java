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
package org.jclouds.elasticstack.functions;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = { "unit" })
public class ListOfKeyValuesDelimitedByBlankLinesToListOfMapsTest {

   private static final ListOfKeyValuesDelimitedByBlankLinesToListOfMaps FN = new ListOfKeyValuesDelimitedByBlankLinesToListOfMaps();

   public void testNone() {
      assertEquals(FN.apply(""), Lists.newArrayList());
      assertEquals(FN.apply("\n\n\n"), Lists.newArrayList());
   }

   public void testOneMap() {
      assertEquals(FN.apply("key1 value1\nkey2 value2"),
            ImmutableList.of(ImmutableMap.of("key1", "value1", "key2", "value2")));
      assertEquals(FN.apply("key1 value1\nkey2 value2\n\n"),
            ImmutableList.of(ImmutableMap.of("key1", "value1", "key2", "value2")));
   }

   public void testValueEncodesNewlines() {
      assertEquals(FN.apply("key1 value1\\n\nkey2 value2"),
            ImmutableList.of(ImmutableMap.of("key1", "value1\n", "key2", "value2")));
   }

   public void testTwoMaps() {
      assertEquals(
            FN.apply("key1 value1\nkey2 value2\n\nkey1 v1\nkey2 v2"),
            ImmutableList.of(ImmutableMap.of("key1", "value1", "key2", "value2"),
                  ImmutableMap.of("key1", "v1", "key2", "v2")));
      assertEquals(
            FN.apply("key1 value1\nkey2 value2\n\nkey1 v1\nkey2 v2\n\n"),
            ImmutableList.of(ImmutableMap.of("key1", "value1", "key2", "value2"),
                  ImmutableMap.of("key1", "v1", "key2", "v2")));
   }
}
