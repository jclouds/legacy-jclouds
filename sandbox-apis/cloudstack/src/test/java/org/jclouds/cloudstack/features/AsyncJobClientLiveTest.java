/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.cloudstack.features;

import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.cloudstack.domain.AsyncJob;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code AsyncJobClientLiveTest}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "AsyncJobClientLiveTest")
public class AsyncJobClientLiveTest extends BaseCloudStackClientLiveTest {

   public void testListAsyncJobs() throws Exception {
      Set<AsyncJob> response = client.getAsyncJobClient().listAsyncJobs();
      assert null != response;
      long asyncJobCount = response.size();
      assertTrue(asyncJobCount >= 0);
      for (AsyncJob asyncJob : response) {
         assert asyncJob.getId() > 0 : asyncJob;
         assert asyncJob.getAccountId() >= 0 : asyncJob;
         assert asyncJob.getCmd() != null : asyncJob;
         assert asyncJob.getCreated() != null : asyncJob;
         // seemingly unused fields
         assert asyncJob.getInstanceId() == -1 : asyncJob;
         assert asyncJob.getInstanceType() == null : asyncJob;
         assert asyncJob.getResultType() == null : asyncJob;
         // end
         if (asyncJob.getProgress() != null) {
            assert asyncJob.getResult() == null : asyncJob;
            assert asyncJob.getResultCode() == -1 : asyncJob;
         } else {
            assert asyncJob.getResult() != null : asyncJob;
            assert asyncJob.getResultCode() >= 0 : asyncJob;
         }
         assert asyncJob.getStatus() >= 0 : asyncJob;
         assert asyncJob.getUserId() >= 0 : asyncJob;
      }
   }

}
