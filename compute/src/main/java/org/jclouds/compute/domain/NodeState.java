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
package org.jclouds.compute.domain;

import org.jclouds.compute.domain.NodeMetadata.Status;

/**
 * Indicates the status of a node.  Replaced by {@link Status}
 * 
 * @author Adrian Cole
 * @see NodeMetadata#getStatus()
 */
@Deprecated
public enum NodeState {
   /**
    * The node is in transition
    * 
    * @see Status#PENDING
    */
   PENDING,
   /**
    * The node is visible, and in the process of being deleted.
    * 
    * @see Status#TERMINATED
    */
   TERMINATED,
   /**
    * The node is deployed, but suspended or stopped.
    * 
    * @see Status#SUSPENDED
    */
   SUSPENDED,
   /**
    * The node is available for requests
    * 
    * @see Status#RUNNING
    */
   RUNNING,
   /**
    * There is an error on the node
    * 
    * @see Status#ERROR
    */
   ERROR,
   /**
    * The state of the node is unrecognized.
    * 
    * @see Status#UNRECOGNIZED
    */
   UNRECOGNIZED;

   public static NodeState from(Status in) {
      switch (in) {
         case PENDING:
            return PENDING;
         case TERMINATED:
            return TERMINATED;
         case SUSPENDED:
            return SUSPENDED;
         case RUNNING:
            return RUNNING;
         case ERROR:
            return ERROR;
         default:
            return UNRECOGNIZED;
      }
   }

   public Status toStatus() {
      switch (this) {
         case PENDING:
            return Status.PENDING;
         case TERMINATED:
            return Status.TERMINATED;
         case SUSPENDED:
            return Status.SUSPENDED;
         case RUNNING:
            return Status.RUNNING;
         case ERROR:
            return Status.ERROR;
         default:
            return Status.UNRECOGNIZED;
      }
   }
}
