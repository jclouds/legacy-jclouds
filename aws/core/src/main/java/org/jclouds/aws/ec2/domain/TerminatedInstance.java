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
package org.jclouds.aws.ec2.domain;


/**
 * 
 * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-ItemType-TerminateInstancesResponseInfoType.html"
 *      />
 * @author Adrian Cole
 */
public class TerminatedInstance implements Comparable<TerminatedInstance> {

   private final String instanceId;
   private final InstanceState shutdownState;
   private final InstanceState previousState;

   public int compareTo(TerminatedInstance o) {
      return (this == o) ? 0 : getInstanceId().compareTo(o.getInstanceId());
   }

   public TerminatedInstance(String instanceId, InstanceState shutdownState,
            InstanceState previousState) {
      this.instanceId = instanceId;
      this.shutdownState = shutdownState;
      this.previousState = previousState;
   }

   public String getInstanceId() {
      return instanceId;
   }

   public InstanceState getShutdownState() {
      return shutdownState;
   }

   public InstanceState getPreviousState() {
      return previousState;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((instanceId == null) ? 0 : instanceId.hashCode());
      result = prime * result + ((previousState == null) ? 0 : previousState.hashCode());
      result = prime * result + ((shutdownState == null) ? 0 : shutdownState.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      TerminatedInstance other = (TerminatedInstance) obj;
      if (instanceId == null) {
         if (other.instanceId != null)
            return false;
      } else if (!instanceId.equals(other.instanceId))
         return false;
      if (previousState == null) {
         if (other.previousState != null)
            return false;
      } else if (!previousState.equals(other.previousState))
         return false;
      if (shutdownState == null) {
         if (other.shutdownState != null)
            return false;
      } else if (!shutdownState.equals(other.shutdownState))
         return false;
      return true;
   }

}