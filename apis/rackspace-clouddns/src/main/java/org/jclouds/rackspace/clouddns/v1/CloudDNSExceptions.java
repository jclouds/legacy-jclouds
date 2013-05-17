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
package org.jclouds.rackspace.clouddns.v1;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.rackspace.clouddns.v1.domain.Job;

/**
 * Exceptions likely to be encountered when using {@link CloudDNSApi}
 * 
 * @author Everett Toews
 */
public interface CloudDNSExceptions {
   /**
    * A Job errored out.
    */
   public static class JobErrorException extends RuntimeException {
      private static final long serialVersionUID = 1L;
      private final Job.Error jobError;

      public JobErrorException(Job.Error jobError) {
         super(jobError.toString());
         this.jobError = checkNotNull(jobError, "jobError");
      }

      public Job.Error getJobError() {
         return jobError;
      }
   }
}
