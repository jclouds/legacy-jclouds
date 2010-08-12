/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.azure.storage.queue.options;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * Tests behavior of {@code GetOptions}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "azurequeue.GetOptionsTest")
public class GetOptionsTest {

   public void testMaxMessages() {
      GetOptions options = new GetOptions().maxMessages(1);
      assertEquals(ImmutableList.of("1"), options.buildQueryParameters().get("numofmessages"));
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testMaxMessagesTooSmall() {
      new GetOptions().maxMessages(0);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testMaxMessagesTooBig() {
      new GetOptions().maxMessages(33);
   }

   public void testMaxMessagesStatic() {
      GetOptions options = GetOptions.Builder.maxMessages(1);
      assertEquals(ImmutableList.of("1"), options.buildQueryParameters().get("numofmessages"));
   }

   public void testVisibilityTimeout() {
      GetOptions options = new GetOptions().visibilityTimeout(1);
      assertEquals(ImmutableList.of("1"), options.buildQueryParameters().get("visibilitytimeout"));
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testVisibilityTimeoutTooSmall() {
      new GetOptions().visibilityTimeout(0);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testVisibilityTimeoutTooBig() {
      new GetOptions().visibilityTimeout((2 * 60 * 60) + 1);
   }

   public void testVisibilityTimeoutStatic() {
      GetOptions options = GetOptions.Builder.visibilityTimeout(1);
      assertEquals(ImmutableList.of("1"), options.buildQueryParameters().get("visibilitytimeout"));
   }

}
