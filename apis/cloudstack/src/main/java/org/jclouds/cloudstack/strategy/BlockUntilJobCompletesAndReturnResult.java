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
package org.jclouds.cloudstack.strategy;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.domain.AsyncCreateResponse;
import org.jclouds.cloudstack.domain.AsyncJob;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;

import com.google.common.base.Predicate;
import com.google.common.util.concurrent.UncheckedExecutionException;

/**
 * @author Adrian Cole
 */
@Singleton
public class BlockUntilJobCompletesAndReturnResult {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   
   private final CloudStackClient client;
   private final Predicate<String> jobComplete;

   @Inject
   public BlockUntilJobCompletesAndReturnResult(CloudStackClient client, Predicate<String> jobComplete) {
      this.client = checkNotNull(client, "client");
      this.jobComplete = checkNotNull(jobComplete, "jobComplete");
   }

   /**
    * 
    * @param job
    * @return result of the job's execution
    * @throws ExecutionException
    *            if the job contained an error
    */
   public <T> T apply(AsyncCreateResponse job) {
      boolean completed = jobComplete.apply(job.getJobId());
      logger.trace("<< job(%s) complete(%s)", job, completed);
      AsyncJob<T> jobWithResult = client.getAsyncJobClient().<T> getAsyncJob(job.getJobId());
      checkState(completed, "job %s failed to complete in time %s", job.getJobId(), jobWithResult);
      if (jobWithResult.getError() != null)
         throw new UncheckedExecutionException(String.format("job %s failed with exception %s", job.getJobId(),
               jobWithResult.getError().toString())) {
         };
      return jobWithResult.getResult();
   }
}
