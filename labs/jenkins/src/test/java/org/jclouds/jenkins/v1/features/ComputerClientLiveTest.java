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
package org.jclouds.jenkins.v1.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.jclouds.jenkins.v1.domain.Computer;
import org.jclouds.jenkins.v1.domain.ComputerView;
import org.jclouds.jenkins.v1.internal.BaseJenkinsClientLiveTest;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "ComputerClientLiveTest")
public class ComputerClientLiveTest extends BaseJenkinsClientLiveTest {

   public void testGetComputerView(){
      ComputerView view = getClient().getView();
      assertNotNull(view);
      assertNotNull(view.getDisplayName());
      for (Computer computerFromView : view.getComputers()) {
         assertNotNull(computerFromView.getDisplayName());
         if (!"master".equals(computerFromView.getDisplayName())) {
            Computer computerFromGetRequest = getClient().get(computerFromView.getDisplayName());
            assertEquals(computerFromGetRequest, computerFromView);
         }
      }
   }

   private ComputerClient getClient() {
      return context.getApi().getComputerClient();
   }
}