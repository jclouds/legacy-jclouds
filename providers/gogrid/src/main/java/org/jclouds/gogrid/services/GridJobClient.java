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
package org.jclouds.gogrid.services;

import java.util.Set;
import org.jclouds.gogrid.domain.Job;
import org.jclouds.gogrid.options.GetJobListOptions;

/**
 * Manages the customer's jobs.
 * 
 * @see <a href="http://wiki.gogrid.com/wiki/index.php/API#Job_Methods" />
 * 
 * @author Oleksiy Yarmula
 */
public interface GridJobClient {

   /**
    * Returns all jobs found. The resulting set may be narrowed down by providing
    * {@link GetJobListOptions}.
    * 
    * By default, the result is <=100 items from the date range of 4 weeks ago to now.
    * 
    * NOTE: this method results in a big volume of data in response
    * 
    * @return jobs found by request
    */
   Set<Job> getJobList(GetJobListOptions... options);

   /**
    * Returns jobs found for an object with a provided name.
    * 
    * Usually, in GoGrid a name will uniquely identify the object, or, as the docs state, some API
    * methods will cause errors.
    * 
    * @param serverName
    *           name of the object
    * @return found jobs for the object
    */
   Set<Job> getJobsForObjectName(String serverName);

   /**
    * Returns jobs for the corresponding id(s).
    * 
    * NOTE: there is a 1:1 relation between a job and its ID.
    * 
    * @param ids
    *           ids for the jobs
    * @return jobs found by the ids
    */
   Set<Job> getJobsById(long... ids);

}
