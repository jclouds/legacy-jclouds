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
package org.jclouds.sqs.internal;

import org.jclouds.apis.BaseContextLiveTest;
import org.jclouds.rest.RestContext;
import org.jclouds.sqs.SQSApi;
import org.jclouds.sqs.SQSApiMetadata;
import org.jclouds.sqs.SQSAsyncApi;
import org.testng.annotations.Test;

import com.google.common.reflect.TypeToken;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live")
public class BaseSQSApiLiveTest extends BaseContextLiveTest<RestContext<SQSApi, SQSAsyncApi>> {

   public BaseSQSApiLiveTest() {
      provider = "sqs";
   }
   
   @Override
   protected TypeToken<RestContext<SQSApi, SQSAsyncApi>> contextType() {
      return SQSApiMetadata.CONTEXT_TOKEN;
   }

}
