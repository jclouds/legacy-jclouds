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
package org.jclouds.sqs.parse;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.sqs.domain.BatchResult;
import org.jclouds.sqs.xml.ChangeMessageVisibilityBatchResponseHandler;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "ChangeMessageVisibilityBatchResponseTest")
public class ChangeMessageVisibilityBatchResponseTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/change_message_visibility_batch.xml");

      BatchResult<String> expected = expected();

      ChangeMessageVisibilityBatchResponseHandler handler = injector.getInstance(ChangeMessageVisibilityBatchResponseHandler.class);
      BatchResult<String> result = factory.create(handler).parse(is);

      assertEquals(result.toString(), expected.toString());

   }

   public BatchResult<String> expected() {
      return BatchResult.<String> builder()
            .put("change_visibility_msg_2","change_visibility_msg_2")
            .put("change_visibility_msg_3","change_visibility_msg_3")
            .build();
   }
}
