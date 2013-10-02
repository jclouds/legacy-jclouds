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
package org.jclouds.ultradns.ws.parse;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;

import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.ultradns.ws.domain.Task;
import org.jclouds.ultradns.ws.domain.Task.StatusCode;
import org.jclouds.ultradns.ws.xml.TaskHandler;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(testName = "GetStatusForTaskResponseResponseTest")
public class GetStatusForTaskResponseResponseTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/task.xml");

      Task expected = expected();

      TaskHandler handler = injector.getInstance(TaskHandler.class);
      Task result = factory.create(handler).parse(is);

      assertEquals(result.toString(), expected.toString());
   }

   public Task expected() {
      return Task.builder()
                 .guid("0b40c7dd-748d-4c49-8506-26f0c7d2ea9c")
                 .statusCode(StatusCode.COMPLETE)
                 .message("Processing complete")
                 .resultUrl(URI.create("http://localhost:8008/users/node01/tasks/0b40c7dd-748d-4c49-8506-26f0c7d2ea9c/result"))
                 .build();
   }
}
