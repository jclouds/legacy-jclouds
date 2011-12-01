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
package org.jclouds.cloudstack.parse;

import org.jclouds.cloudstack.domain.AsyncJob;
import org.jclouds.cloudstack.domain.OperationResult;
import org.jclouds.json.BaseItemParserTest;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

/**
 * 
 * @author Richard Downer
 */
@Test(groups = "unit", testName = "DeleteNetworkResponseTest")
public class OperationResultResponseTest extends BaseItemParserTest<AsyncJob> {

   @Override
   public String resource() {
      return "/deletetemplateresponse.json";
   }

   @Override
   @SelectJson("queryasyncjobresultresponse")
   public AsyncJob expected() {
      OperationResult payload = new OperationResult(true, null);
      AsyncJob<OperationResult> wrapper = AsyncJob.<OperationResult>builder().id(118).status(1).progress(0).resultCode(0).resultType("object").result(payload).build();
      return wrapper;
   }

}
