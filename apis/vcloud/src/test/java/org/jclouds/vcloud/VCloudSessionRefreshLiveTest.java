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
package org.jclouds.vcloud;

import org.jclouds.vcloud.internal.BaseVCloudApiLiveTest;
import org.testng.annotations.Test;

/**
 * Tests session refresh works
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true)
public class VCloudSessionRefreshLiveTest extends BaseVCloudApiLiveTest {

   private static final int timeOut = 40;

   @Test
   public void testSessionRefresh() throws Exception {
      VCloudApi connection = view.unwrapApi(VCloudApi.class);

      connection.getOrgApi().findOrgNamed(null);
      Thread.sleep(timeOut * 1000);
      connection.getOrgApi().findOrgNamed(null);
   }

}
