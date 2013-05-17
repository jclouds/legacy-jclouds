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
package org.jclouds.cloudstack.parse;

import org.jclouds.cloudstack.domain.AsyncCreateResponse;
import org.jclouds.json.BaseItemParserTest;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "DeployVirtualMachineResponse3xTest")
public class DeployVirtualMachineResponse4xTest extends BaseItemParserTest<AsyncCreateResponse> {

   @Override
   public String resource() {
      return "/deployvirtualmachineresponse4x.json";
   }

   @Override
   @SelectJson({ "deployvirtualmachine", "deployvirtualmachineresponse" })
   public AsyncCreateResponse expected() {
      return AsyncCreateResponse.builder().id("1cce6cb7-2268-47ff-9696-d9e610f6619a")
            .jobId("13330fc9-8b3e-4582-aa3e-90883c041ff0").build();
   }
}
