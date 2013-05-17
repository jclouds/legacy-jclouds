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
package org.jclouds.gogrid.domain;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Oleksiy Yarmula
 */
public enum JobState {

   QUEUED("Queued", "Change request is new to the system."), PROCESSING("Processing",
            "Change request is is transient state...Processing."), SUCCEEDED("Succeeded",
            "Change request has succeeded."), FAILED("Failed", "Change request has failed."), CANCELED(
            "Canceled", "Change request has been canceled."), FATAL("Fatal",
            "Change request had Fatal or Unrecoverable Failure"), CREATED("Created",
            "Change request is created but not queued yet"), UNRECOGNIZED("Unknown",
            "The state is unknown to JClouds");

   String name;
   String description;

   JobState(String name, String description) {
      this.name = name;
      this.description = description;
   }

   @Override
   public String toString() {
      return name;
   }

   public static JobState fromValue(String state) {
      for (JobState jobState : values()) {
         if (jobState.name.equals(checkNotNull(state)))
            return jobState;
      }
      return UNRECOGNIZED;
   }

}
