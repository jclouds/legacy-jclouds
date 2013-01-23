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
package org.jclouds.dynect.v3.internal;

import org.jclouds.apis.BaseContextLiveTest;
import org.jclouds.dynect.v3.DynECTApi;
import org.jclouds.dynect.v3.DynECTApiMetadata;
import org.jclouds.dynect.v3.DynECTAsyncApi;
import org.jclouds.rest.RestContext;
import org.testng.annotations.Test;

import com.google.common.reflect.TypeToken;

@Test(groups = "live")
public class BaseDynECTApiLiveTest extends BaseContextLiveTest<RestContext<DynECTApi, DynECTAsyncApi>> {

   public BaseDynECTApiLiveTest() {
      provider = "dynect";
   }

   @Override
   protected TypeToken<RestContext<DynECTApi, DynECTAsyncApi>> contextType() {
      return DynECTApiMetadata.CONTEXT_TOKEN;
   }
}
