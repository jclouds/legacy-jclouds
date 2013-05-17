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
package org.jclouds.sqs.options;

import static org.jclouds.sqs.options.ListQueuesOptions.Builder.queuePrefix;
import static org.testng.Assert.assertEquals;

import com.google.common.collect.ImmutableList;

import org.jclouds.http.options.HttpRequestOptions;
import org.testng.annotations.Test;

/**
 * Tests possible uses of ListQueuesOptions and ListQueuesOptions.Builder.*
 * 
 * @author Adrian Cole
 */
public class ListQueuesOptionsTest {

   @Test
   public void testAssignability() {
      assert HttpRequestOptions.class.isAssignableFrom(ListQueuesOptions.class);
      assert !String.class.isAssignableFrom(ListQueuesOptions.class);
   }

   @Test
   public void testPrefix() {
      ListQueuesOptions options = new ListQueuesOptions();
      options.queuePrefix("test");
      assertEquals(options.buildFormParameters().get("QueueNamePrefix"), ImmutableList.of("test"));
   }

   @Test
   public void testNullPrefix() {
      ListQueuesOptions options = new ListQueuesOptions();
      assertEquals(options.buildFormParameters().get("QueueNamePrefix"), ImmutableList.of());
   }

   @Test
   public void testPrefixStatic() {
      ListQueuesOptions options = queuePrefix("test");
      assertEquals(options.buildFormParameters().get("QueueNamePrefix"), ImmutableList.of("test"));
   }

   public void testNoPrefix() {
      queuePrefix(null);
   }
}
