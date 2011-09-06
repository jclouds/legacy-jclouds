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
package org.jclouds.ec2.domain;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * 
 * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-ItemType-TerminateInstancesResponseInfoType.html"
 *      />
 * @author Adrian Cole
 */
public class InstanceStateChange implements Comparable<InstanceStateChange> {

   private final String region;
   private final String instanceId;
   private final InstanceState currentState;
   private final InstanceState previousState;

   public int compareTo(InstanceStateChange o) {
      return (this == o) ? 0 : getInstanceId().compareTo(o.getInstanceId());
   }

   public InstanceStateChange(String region, String instanceId, InstanceState currentState,
            InstanceState previousState) {
      this.region = checkNotNull(region, "region");
      this.instanceId = instanceId;
      this.currentState = currentState;
      this.previousState = previousState;
   }

   /**
    * Instances are tied to Availability Zones. However, the instance ID is tied to the Region.
    */
   public String getRegion() {
      return region;
   }

   public String getInstanceId() {
      return instanceId;
   }

   public InstanceState getCurrentState() {
      return currentState;
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
      result = prime * result + ((region == null) ? 0 : region.hashCode());
      result = prime * result + ((currentState == null) ? 0 : currentState.hashCode());
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
      InstanceStateChange other = (InstanceStateChange) obj;
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
      if (region == null) {
         if (other.region != null)
            return false;
      } else if (!region.equals(other.region))
         return false;
      if (currentState == null) {
         if (other.currentState != null)
            return false;
      } else if (!currentState.equals(other.currentState))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "InstanceStateChange [currentState=" + currentState + ", instanceId=" + instanceId
               + ", previousState=" + previousState + ", region=" + region + "]";
   }

}
