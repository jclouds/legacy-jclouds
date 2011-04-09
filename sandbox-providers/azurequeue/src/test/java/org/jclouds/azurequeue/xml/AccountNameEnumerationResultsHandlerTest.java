/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.azurequeue.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;

import org.jclouds.azure.storage.domain.BoundedSet;
import org.jclouds.azure.storage.domain.internal.BoundedHashSet;
import org.jclouds.azurequeue.domain.QueueMetadata;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSortedSet;

/**
 * Tests behavior of {@code AccountNameEnumerationResultsHandler}
 * 
 * @author Adrian Cole
 */
//NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "queue.AccountNameEnumerationResultsHandlerTest")
public class AccountNameEnumerationResultsHandlerTest extends BaseHandlerTest {

   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream("/test_list_queues.xml");
      BoundedSet<QueueMetadata> list = new BoundedHashSet<QueueMetadata>(ImmutableSortedSet.of(
               new QueueMetadata("q1", URI.create("http://myaccount.queue.core.windows.net/q1")),
               new QueueMetadata("q2", URI.create("http://myaccount.queue.core.windows.net/q2")),
               new QueueMetadata("q3", URI.create("http://myaccount.queue.core.windows.net/q3"))),
               URI.create("http://myaccount.queue.core.windows.net"), "q", null, 3, "q4");
      BoundedSet<QueueMetadata> result = (BoundedSet<QueueMetadata>) factory.create(
               injector.getInstance(AccountNameEnumerationResultsHandler.class)).parse(is);
      assertEquals(result, list);
   }

   public void testApplyInputStreamWithOptions() {
      InputStream is = getClass().getResourceAsStream("/test_list_queues_options.xml");
      BoundedSet<QueueMetadata> list = new BoundedHashSet<QueueMetadata>(ImmutableSortedSet.of(
               new QueueMetadata("q4", URI.create("http://myaccount.queue.core.windows.net/q4")),
               new QueueMetadata("q5", URI.create("http://myaccount.queue.core.windows.net/q5"))),
               URI.create("http://myaccount.queue.core.windows.net"), "q", "q4", 3, null);

      BoundedSet<QueueMetadata> result = (BoundedSet<QueueMetadata>) factory.create(
               injector.getInstance(AccountNameEnumerationResultsHandler.class)).parse(is);

      assertEquals(result, list);
   }
}
