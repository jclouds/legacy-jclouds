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
package org.jclouds.gogrid.services;

import static org.testng.Assert.assertEquals;

import java.util.Set;

import org.jclouds.gogrid.domain.Job;
import org.jclouds.gogrid.options.GetJobListOptions;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "GridJobClientLiveTest")
public class GridJobClientLiveTest extends BaseGoGridClientLiveTest {

   public void testListJobs() throws Exception {
      Set<Job> response = api.getJobServices().getJobList(GetJobListOptions.Builder.maxItems(10));
      assert null != response;
      assert response.size() <= 10 : response;
      for (Job job : response) {
         assert job.getId() >= 0 : job;
         checkJob(job);

         Job query = Iterables.getOnlyElement(api.getJobServices().getJobsById(job.getId()));
         assertEquals(query.getId(), job.getId());

         checkJob(query);
      }
   }

   private void checkJob(Job job) {
      assert job.getAttempts() >= 0 : job;
      assert job.getCommand() != null : job;
      assert job.getCreatedOn() != null : job;
      assert job.getCreatedOn() != null : job;
      assert job.getDetails() != null : job;
      assert job.getHistory() != null : job;
      assert job.getId() >= 0 : job;
      assert job.getLastUpdatedOn() != null : job;
      assert job.getObjectType() != null : job;
      assert job.getOwner() != null : job;
   }
}
