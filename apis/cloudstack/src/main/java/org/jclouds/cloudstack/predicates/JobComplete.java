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
package org.jclouds.cloudstack.predicates;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.cloudstack.domain.AsyncJob.ResultCode.FAIL;
import static org.jclouds.cloudstack.domain.AsyncJob.ResultCode.SUCCESS;
import static org.jclouds.cloudstack.domain.AsyncJob.Status.FAILED;
import static org.jclouds.cloudstack.domain.AsyncJob.Status.SUCCEEDED;

import javax.annotation.Resource;
import javax.inject.Singleton;

import org.jclouds.cloudstack.AsyncJobException;
import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.domain.AsyncJob;
import org.jclouds.logging.Logger;

import com.google.common.base.Predicate;
import com.google.inject.Inject;

/**
 * 
 * Tests to see if a job is in progress.
 * 
 * @author Adrian Cole
 */
@Singleton
public class JobComplete implements Predicate<Long> {

   private final CloudStackClient client;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public JobComplete(CloudStackClient client) {
      this.client = client;
   }

   public boolean apply(Long jobId) {
      logger.trace(">> looking for status on job %s", checkNotNull(jobId, "jobId"));
      AsyncJob<?> job = refresh(jobId);
      if (job == null) {
         return false;
      }
      logger.trace("%s: looking for job status %s: currently: %s", job.getId(), 1, job.getStatus());
      if (job.hasFailed()) {

         throw new AsyncJobException(String.format("job %s failed with exception %s",
            job.toString(), job.getError().toString()));
      }
      return job.hasSucceed();
   }

   private AsyncJob<?> refresh(Long jobId) {
      return client.getAsyncJobClient().getAsyncJob(jobId);
   }
}
