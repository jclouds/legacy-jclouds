/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.azure.storage.queue.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;

import org.jclouds.azure.storage.domain.MetadataList;
import org.jclouds.azure.storage.queue.domain.QueueMetadata;
import org.jclouds.http.functions.ParseSax;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * Tests behavior of {@code ParseFlavorListFromGsonResponseTest}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "azurequeue.AccountNameEnumerationResultsHandlerTest")
public class AccountNameEnumerationResultsHandlerTest extends BaseHandlerTest {

   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream("/test_list_queues.xml");
      MetadataList<QueueMetadata> list = new MetadataList<QueueMetadata>("q", null, 3,
               ImmutableList.of(new QueueMetadata("q1", URI
                        .create("http://myaccount.queue.core.windows.net/q1")), new QueueMetadata(
                        "q2", URI.create("http://myaccount.queue.core.windows.net/q2")),
                        new QueueMetadata("q3", URI
                                 .create("http://myaccount.queue.core.windows.net/q3")))

               , "q4");
      ParseSax<MetadataList<QueueMetadata>> parser = parserFactory
               .createContainerMetadataListParser();
      MetadataList<QueueMetadata> result = parser.parse(is);
      assertEquals(result, list);
   }

   public void testApplyInputStreamWithOptions() {
      InputStream is = getClass().getResourceAsStream("/test_list_queues_options.xml");
      MetadataList<QueueMetadata> list = new MetadataList<QueueMetadata>("q", "q4", 3,
               ImmutableList.of(new QueueMetadata("q4", URI
                        .create("http://myaccount.queue.core.windows.net/q4")), new QueueMetadata(
                        "q5", URI.create("http://myaccount.queue.core.windows.net/q5")))

               , null);
      ParseSax<MetadataList<QueueMetadata>> parser = parserFactory
               .createContainerMetadataListParser();
      MetadataList<QueueMetadata> result = parser.parse(is);
      assertEquals(result, list);
   }
}
