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
package org.jclouds.rackspace.clouddns.v1.predicates;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.util.Predicates2.retry;

import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import org.jclouds.rackspace.clouddns.v1.CloudDNSApi;
import org.jclouds.rackspace.clouddns.v1.CloudDNSExceptions;
import org.jclouds.rackspace.clouddns.v1.domain.Job;

import com.google.common.base.Predicate;
import com.google.common.util.concurrent.Atomics;

/**
 * Useful Predicates for dealing with Jobs.
 * 
 * @author Everett Toews
 */
public class JobPredicates {
   
   private JobPredicates() {
   }

   /**
    * Tests to see if a Job has completed.
    *
    * <pre>
    * {@code
    * CreateDomain createDomain1 = CreateDomain.builder()
    *    .name("jclouds-example.org")
    *    .email("jclouds@jclouds-example.org")
    *    .ttl(600001)
    *    .comment("Hello Domain 1")
    *    .build();
    *
    * Iterable<CreateDomain> createDomains = ImmutableList.of(createDomain1);      
    * Set<Domain> domains = awaitComplete(api, api.getDomainApi().create(createDomains));
    * }
    * </pre>
    */
   public static <T> T awaitComplete(CloudDNSApi api, Job<T> job)
         throws TimeoutException {
      AtomicReference<Job<T>> jobRef = Atomics.newReference(job);

      if (!retry(jobCompleted(api), 600, 2, 2, SECONDS).apply(jobRef)) {
         throw new TimeoutException("Timeout on: " + jobRef.get());
      }

      return jobRef.get().getResource().orNull();
   }
   
   @SuppressWarnings({ "rawtypes", "unchecked" })
   private static Predicate<AtomicReference<? extends Job<?>>> jobCompleted(CloudDNSApi cloudDNSApi) {
      return new JobStatusPredicate(cloudDNSApi, Job.Status.COMPLETED);
   }
   
   private static class JobStatusPredicate<T> implements Predicate<AtomicReference<Job<?>>> {
      private CloudDNSApi cloudDNSApi;
      private Job.Status status;

      private JobStatusPredicate(CloudDNSApi cloudDNSApi, Job.Status status) {
         this.cloudDNSApi = checkNotNull(cloudDNSApi, "domainApi must be defined");
         this.status = checkNotNull(status, "status must be defined");
      }
      
      /**
       * @return boolean Return true when the Job reaches status, false otherwise.
       */
      @Override
      public boolean apply(AtomicReference<Job<?>> jobRef) {
         checkNotNull(jobRef, "job must be defined");

         if (status.equals(jobRef.get().getStatus())) {
            return true;
         }
         else {
            jobRef.set(cloudDNSApi.getJob(jobRef.get().getId()));
            checkNotNull(jobRef.get(), "Job %s not found.", jobRef.get().getId());
            
            if (jobRef.get().getError().isPresent()) {
               throw new CloudDNSExceptions.JobErrorException(jobRef.get().getError().get());
            }
            
            return status.equals(jobRef.get().getStatus());
         }
      }
   }
}
