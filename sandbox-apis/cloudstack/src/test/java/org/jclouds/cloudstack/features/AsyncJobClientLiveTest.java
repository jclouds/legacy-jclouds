/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.cloudstack.domain.AsyncJob;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code AsyncJobClientLiveTest}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "AsyncJobClientLiveTest")
public class AsyncJobClientLiveTest extends BaseCloudStackClientLiveTest {
   // disabled as it takes too long
   @Test(enabled = false)
   public void testListAsyncJobs() throws Exception {
      Set<AsyncJob<?>> response = client.getAsyncJobClient().listAsyncJobs();
      assert null != response;
      long asyncJobCount = response.size();
      assertTrue(asyncJobCount >= 0);
      for (AsyncJob<?> asyncJob : response) {
         assert asyncJob.getCmd() != null : asyncJob;
         assert asyncJob.getUserId() >= 0 : asyncJob;
         checkJob(asyncJob);

         AsyncJob<?> query = client.getAsyncJobClient().getAsyncJob(asyncJob.getId());
         assertEquals(query.getId(), asyncJob.getId());

         assert query.getResultType() != null : query;
         checkJob(query);
      }
   }

   private void checkJob(AsyncJob<?> query) {
      assert query.getStatus() >= 0 : query;
      assert query.getResultCode() >= 0 : query;
      assert query.getProgress() >= 0 : query;
      if (query.getResultCode() == 0) {
         if (query.getResult() != null)// null is ok for result of success = true
            // ensure we parsed properly
            assert (query.getResult().getClass().getPackage().equals(AsyncJob.class.getPackage())) : query;
      } else if (query.getResultCode() > 400) {
         assert query.getResult() == null : query;
         assert query.getError() != null : query;
      } else {
         assert query.getResult() == null : query;
      }
   }

}
