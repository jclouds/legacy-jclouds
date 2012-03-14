/*
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
package org.jclouds.vcloud.director.v1_5;

import org.jclouds.vcloud.director.v1_5.domain.Error;
import org.jclouds.vcloud.director.v1_5.domain.Task;

/**
 * @author grkvlt@apache.org
 */
public class VCloudDirectorException extends RuntimeException {

   /** The serialVersionUID. */
   private static final long serialVersionUID = -5292516858598372960L;

   private static final String MSG_FMT = "%s: %s";   

   private final Error error;
   private final Task task;

   public VCloudDirectorException(Error error) {
      super(String.format(MSG_FMT, "Error", error.getMessage()));
      this.error = error;
      this.task = null;
   }

   public VCloudDirectorException(Task task) {
      super(String.format(MSG_FMT, "Task error", task.getError().getMessage()));
      this.error = task.getError();
      this.task = task;
   }

   public Integer getMajorErrorCode() {
      return error.getMajorErrorCode();
   }

   public Error.Code getCode() {
      return Error.Code.fromCode(error.getMajorErrorCode());
   }

   public Error getError() {
      return error;
   }

   public boolean hasTask() {
      return task != null;
   }

   public Task getTask() {
      return task;
   }
}
