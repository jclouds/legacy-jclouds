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
package org.jclouds.iam.internal;

import org.jclouds.apis.BaseContextLiveTest;
import org.jclouds.iam.IAMApiMetadata;
import org.jclouds.iam.IAMAsyncClient;
import org.jclouds.iam.IAMClient;
import org.jclouds.rest.RestContext;
import org.testng.annotations.Test;

import com.google.common.reflect.TypeToken;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live")
public class BaseIAMClientLiveTest extends
         BaseContextLiveTest<RestContext<? extends IAMClient, ? extends IAMAsyncClient>> {

   public BaseIAMClientLiveTest() {
      provider = "iam";
   }

   @Override
   protected TypeToken<RestContext<? extends IAMClient, ? extends IAMAsyncClient>> contextType() {
      return IAMApiMetadata.CONTEXT_TOKEN;
   }

}
