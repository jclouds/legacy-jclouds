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
package org.jclouds.sqs.functions;

import static org.testng.Assert.assertEquals;

import java.util.Date;

import org.jclouds.sqs.domain.QueueAttributes;
import org.jclouds.sqs.parse.GetQueueAttributesResponseTest;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "MapToQueueAttributesTest")
public class MapToQueueAttributesTest {

   public void test() {
      

      QueueAttributes expected = expected();
      
      MapToQueueAttributes fn = new MapToQueueAttributes();

      QueueAttributes result = fn.apply(new GetQueueAttributesResponseTest().expected());

      assertEquals(result.toString(), expected.toString());

   }

   public QueueAttributes expected() {
      return QueueAttributes.builder()
                            .queueArn("arn:aws:sqs:us-east-1:993194456877:adrian-sqs1")
                            .approximateNumberOfMessages(0)
                            .approximateNumberOfMessagesNotVisible(0)
                            .approximateNumberOfMessagesDelayed(0)
                            .createdTimestamp(new Date(1347566436l))
                            .lastModifiedTimestamp(new Date(1347566436))
                            .visibilityTimeout(30)
                            .maximumMessageSize(65536)
                            .messageRetentionPeriod(345600)
                            .delaySeconds(0)
                            .build();
   }
}
