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
package org.jclouds.vcloud.domain;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Adrian Cole
 */
public enum TaskStatus {
   /**
    * The task has completed and returned a value indicating success.
    */
   SUCCESS,
   /**
    * The task is running.
    */
   RUNNING,

   /**
    * The task has been queued for execution.
    */
   QUEUED,
   /**
    * The task has completed and returned a value indicating an error.
    */
   ERROR,
   /**
    * not an official status, temporarily in.
    */
   CANCELLED, UNRECOGNIZED;
   public String value() {
      return name().toLowerCase();
   }

   @Override
   public String toString() {
      return value();
   }

   public static TaskStatus fromValue(String status) {
      if ("CANCELED".equals(status.toUpperCase())) {
         // TODO: ecloud hack
         status = "CANCELLED";
      } else if ("FAILED".equals(status.toUpperCase())) {
         status = "ERROR";
      } else if ("COMPLETED".equals(status.toUpperCase())) {
         status = "SUCCESS";
      }
      try {
         return valueOf(checkNotNull(status, "status").toUpperCase());
      } catch (IllegalArgumentException e) {
         return UNRECOGNIZED;
      }
   }

}
