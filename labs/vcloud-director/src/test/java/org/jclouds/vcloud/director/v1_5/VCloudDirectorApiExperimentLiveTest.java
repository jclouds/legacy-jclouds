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
package org.jclouds.vcloud.director.v1_5;

import static org.testng.Assert.assertEquals;

import org.jclouds.vcloud.director.v1_5.domain.Session;
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorApiLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "VCloudDirectorApiExperimentLiveTest")
public class VCloudDirectorApiExperimentLiveTest extends BaseVCloudDirectorApiLiveTest {

   public void testImplicitSession() {
      Session session = context.getApi().getCurrentSession();
      assertEquals(session.getHref().toASCIIString(), context.getProviderMetadata().getEndpoint() + "/session/");
   }

   /**
    * No operation.
    *
    * @see BaseVCloudDirectorApiLiveTest#setupRequiredApis()
    */
   @Override
   @BeforeClass(alwaysRun = true)
   public void setupRequiredApis() { }

}
