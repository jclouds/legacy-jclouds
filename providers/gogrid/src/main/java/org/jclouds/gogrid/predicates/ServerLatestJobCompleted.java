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
package org.jclouds.gogrid.predicates;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.gogrid.options.GetJobListOptions.Builder.latestJobForObjectByName;

import javax.annotation.Resource;
import javax.inject.Singleton;

import org.jclouds.gogrid.domain.Job;
import org.jclouds.gogrid.domain.JobState;
import org.jclouds.gogrid.domain.Server;
import org.jclouds.gogrid.services.GridJobClient;
import org.jclouds.logging.Logger;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

/**
 * Checks if the latest job for the server is in "Succeeded" state. To achieve meaningful results,
 * this must be run in a sequential environment when a server has only one job related to it at a
 * time.
 * 
 * The passed server instance must not be null and must have a name.
 * 
 * @author Oleksiy Yarmula
 */
@Singleton
public class ServerLatestJobCompleted implements Predicate<Server> {

   protected GridJobClient jobClient;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public ServerLatestJobCompleted(GridJobClient jobClient) {
      this.jobClient = jobClient;
   }

   @Override
   public boolean apply(Server server) {
      checkNotNull(server, "Server must be a valid instance");
      checkNotNull(server.getName(), "Server must be a valid name");
      Job latestJob = Iterables.getOnlyElement(jobClient.getJobList(latestJobForObjectByName(server.getName())));
      return JobState.SUCCEEDED.equals(latestJob.getCurrentState());
   }
}
