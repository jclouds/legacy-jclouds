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
package org.jclouds.sts.internal;

import org.jclouds.apis.BaseContextLiveTest;
import org.jclouds.sts.STSApiMetadata;
import org.jclouds.sts.STSAsyncApi;
import org.jclouds.sts.STSApi;
import org.jclouds.rest.RestContext;
import org.testng.annotations.Test;

import com.google.common.reflect.TypeToken;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live")
public class BaseSTSApiLiveTest extends
         BaseContextLiveTest<RestContext<? extends STSApi, ? extends STSAsyncApi>> {

   public BaseSTSApiLiveTest() {
      provider = "sts";
   }

   @Override
   protected TypeToken<RestContext<? extends STSApi, ? extends STSAsyncApi>> contextType() {
      return STSApiMetadata.CONTEXT_TOKEN;
   }

}
