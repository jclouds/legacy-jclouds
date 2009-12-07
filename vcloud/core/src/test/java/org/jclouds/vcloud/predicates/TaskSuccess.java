/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.vcloud.predicates;

import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.logging.Logger;
import org.jclouds.vcloud.VCloudAsyncClient;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.TaskStatus;

import com.google.common.base.Predicate;
import com.google.inject.Inject;

/**
 * 
 * Tests to see if a task succeeds.
 * 
 * @author Adrian Cole
 */
@Singleton
public class TaskSuccess implements Predicate<URI> {

   private final VCloudAsyncClient client;

   @Inject(optional = true)
   @Named("org.jclouds.vcloud.timeout")
   private long taskTimeout = 30000;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public TaskSuccess(VCloudAsyncClient client) {
      this.client = client;
   }

   public boolean apply(URI taskUri) {
      logger.trace("looking for status on task %s", taskUri);

      Task task;
      try {
         task = client.getTask(taskUri).get(taskTimeout, TimeUnit.MILLISECONDS);
         logger.trace("%s: looking for status %s: currently: %s", task, TaskStatus.SUCCESS, task
                  .getStatus());
         return task.getStatus() == TaskStatus.SUCCESS;
      } catch (InterruptedException e) {
         logger.warn(e, "%s interrupted, returning false", taskUri);
      } catch (ExecutionException e) {
         logger.warn(e, "%s exception, returning false", taskUri);
      } catch (TimeoutException e) {
         logger.warn(e, "%s timeout, returning false", taskUri);
      }

      return false;
   }

}
