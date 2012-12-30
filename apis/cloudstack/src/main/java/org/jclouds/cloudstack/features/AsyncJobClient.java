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
package org.jclouds.cloudstack.features;

import java.util.Set;
import org.jclouds.cloudstack.domain.AsyncJob;
import org.jclouds.cloudstack.options.ListAsyncJobsOptions;

/**
 * Provides synchronous access to CloudStack asyncJob features.
 * <p/>
 * 
 * @see AsyncJobAsyncClient
 * @see <a href="http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_User.html" />
 * @author Adrian Cole
 */
public interface AsyncJobClient {
   /**
    * Lists asyncJobs
    * 
    * @param options
    *           if present, how to constrain the list.
    * @return asyncJobs matching query, or empty set, if no asyncJobs are found
    */
   Set<AsyncJob<?>> listAsyncJobs(ListAsyncJobsOptions... options);

   /**
    * get a specific asyncJob by id
    * 
    * @param id
    *           asyncJob to get
    * @return asyncJob or null if not found
    */
   <T> AsyncJob<T> getAsyncJob(String id);
}
