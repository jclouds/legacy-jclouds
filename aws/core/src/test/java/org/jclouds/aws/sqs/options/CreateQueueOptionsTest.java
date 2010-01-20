/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.aws.sqs.options;

import static org.jclouds.aws.sqs.options.CreateQueueOptions.Builder.defaultVisibilityTimeout;
import static org.testng.Assert.assertEquals;

import java.util.Collections;

import org.jclouds.http.options.HttpRequestOptions;
import org.testng.annotations.Test;

/**
 * Tests possible uses of CreateQueueOptions and CreateQueueOptions.Builder.*
 * 
 * @author Adrian Cole
 */
public class CreateQueueOptionsTest {

   @Test
   public void testAssignability() {
      assert HttpRequestOptions.class.isAssignableFrom(CreateQueueOptions.class);
      assert !String.class.isAssignableFrom(CreateQueueOptions.class);
   }

   @Test
   public void testTimeout() {
      CreateQueueOptions options = new CreateQueueOptions();
      options.defaultVisibilityTimeout(1);
      assertEquals(options.buildFormParameters().get("DefaultVisibilityTimeout"), Collections
               .singletonList("1"));
   }

   @Test
   public void testNullTimeout() {
      CreateQueueOptions options = new CreateQueueOptions();
      assertEquals(options.buildFormParameters().get("DefaultVisibilityTimeout"), Collections.EMPTY_LIST);
   }

   @Test
   public void testTimeoutStatic() {
      CreateQueueOptions options = defaultVisibilityTimeout(1);
      assertEquals(options.buildFormParameters().get("DefaultVisibilityTimeout"), Collections
               .singletonList("1"));
   }

   public void testNoTimeout() {
      defaultVisibilityTimeout(0);
   }
}
