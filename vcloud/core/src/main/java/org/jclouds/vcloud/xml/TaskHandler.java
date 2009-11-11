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
package org.jclouds.vcloud.xml;

import javax.inject.Inject;

import org.jclouds.util.DateService;
import org.jclouds.vcloud.domain.Task;

/**
 * @author Adrian Cole
 */
public class TaskHandler extends BaseTaskHandler<Task> {

   @Inject
   public TaskHandler(DateService dateService) {
      super(dateService);
   }

   private Task task;

   public Task getResult() {
      return task;
   }

   @Override
   protected void newTask(Task task) {
      this.task = task;
   }

}
