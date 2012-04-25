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
package org.jclouds.snia.cdmi.v1.internal;

import org.jclouds.apis.BaseContextLiveTest;
import org.jclouds.rest.RestContext;
import org.jclouds.snia.cdmi.v1.CDMIApiMetadata;
import org.jclouds.snia.cdmi.v1.CDMIAsyncClient;
import org.jclouds.snia.cdmi.v1.CDMIClient;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.reflect.TypeToken;

/**
 * Tests behavior of {@code CDMIClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live")
public class BaseCDMIClientLiveTest extends BaseContextLiveTest<RestContext<CDMIClient, CDMIAsyncClient>> {

   public BaseCDMIClientLiveTest() {
      provider = "cdmi";
   }

   protected RestContext<CDMIClient, CDMIAsyncClient> cdmiContext;

   @BeforeGroups(groups = { "integration", "live" })
   @Override
   public void setupContext() {
      super.setupContext();
      cdmiContext = context;
   }

   @AfterGroups(groups = "live")
   protected void tearDown() {
      if (cdmiContext != null)
         cdmiContext.close();
   }

   @Override
   protected TypeToken<RestContext<CDMIClient, CDMIAsyncClient>> contextType() {
      return CDMIApiMetadata.CONTEXT_TOKEN;
   }

}
