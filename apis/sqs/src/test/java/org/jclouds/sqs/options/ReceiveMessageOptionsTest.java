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

import static org.jclouds.sqs.options.ReceiveMessageOptions.Builder.attribute;
import static org.jclouds.sqs.options.ReceiveMessageOptions.Builder.visibilityTimeout;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests behavior of {@code ReceiveMessageOptions}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ReceiveMessageOptionsTest")
public class ReceiveMessageOptionsTest {

   public void testVisibilityTimeout() {
      ReceiveMessageOptions options = new ReceiveMessageOptions().visibilityTimeout(2);
      assertEquals(ImmutableSet.of("2"), options.buildFormParameters().get("VisibilityTimeout"));
   }

   public void testVisibilityTimeoutStatic() {
      ReceiveMessageOptions options = visibilityTimeout(2);
      assertEquals(ImmutableSet.of("2"), options.buildFormParameters().get("VisibilityTimeout"));
   }

   public void testAttribute() {
      ReceiveMessageOptions options = new ReceiveMessageOptions().attribute("All");
      assertEquals(ImmutableSet.of("All"), options.buildFormParameters().get("AttributeName.1"));
   }

   public void testAttributeStatic() {
      ReceiveMessageOptions options = attribute("All");
      assertEquals(ImmutableSet.of("All"), options.buildFormParameters().get("AttributeName.1"));
   }

}
