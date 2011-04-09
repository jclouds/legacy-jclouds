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
package org.jclouds.cloudstack.predicates;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Resource;
import javax.inject.Singleton;

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
      logger.trace("looking for status on job %s", checkNotNull(jobId, "jobId"));
      AsyncJob<?> job = refresh(jobId);
      if (job == null)
         return false;
      logger.trace("%s: looking for job status %s: currently: %s", job.getId(), 1, job.getStatus());
      if (job.getError() != null)
         throw new IllegalStateException(String.format("job %s failed with exception %s", job.getId(), job.getError()
                  .toString()));
      return job.getStatus() > 0;
   }

   private AsyncJob<?> refresh(Long jobId) {
      return client.getAsyncJobClient().getAsyncJob(jobId);
   }
}
