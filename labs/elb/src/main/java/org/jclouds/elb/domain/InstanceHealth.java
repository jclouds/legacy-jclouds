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
package org.jclouds.elb.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;
import com.google.common.base.Optional;

/**
 * the current state of an instance in a loadbalancer.
 * 
 * @see <a
 *      href="http://docs.amazonwebservices.com/ElasticLoadBalancing/latest/APIReference/API_InstanceState.html"
 *      >doc</a>
 * 
 * @author Adrian Cole
 */
public class InstanceHealth {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromAttributeMetadata(this);
   }

   public static class Builder {

      protected String description;
      protected String instanceId;
      protected Optional<String> reasonCode = Optional.absent();
      protected String state;

      /**
       * @see InstanceHealth#getDescription()
       */
      public Builder description(String description) {
         this.description = description;
         return this;
      }

      /**
       * @see InstanceHealth#getInstanceId()
       */
      public Builder instanceId(String instanceId) {
         this.instanceId = instanceId;
         return this;
      }

      /**
       * @see InstanceHealth#getReasonCode()
       */
      public Builder reasonCode(String reasonCode) {
         this.reasonCode = Optional.fromNullable(reasonCode);
         return this;
      }

      /**
       * @see InstanceHealth#getState()
       */
      public Builder state(String state) {
         this.state = state;
         return this;
      }

      public InstanceHealth build() {
         return new InstanceHealth(description, instanceId, reasonCode, state);
      }

      public Builder fromAttributeMetadata(InstanceHealth in) {
         return this.description(in.getDescription()).instanceId(in.getInstanceId())
                  .reasonCode(in.getReasonCode().orNull()).state(in.getState());
      }
   }

   protected final String description;
   protected final String instanceId;
   protected final Optional<String> reasonCode;
   protected final String state;

   protected InstanceHealth(String description, String instanceId, Optional<String> reasonCode, String state) {
      this.description = checkNotNull(description, "description");
      this.instanceId = checkNotNull(instanceId, "instanceId");
      this.reasonCode = checkNotNull(reasonCode, "reasonCode");
      this.state = checkNotNull(state, "state");
   }

   /**
    * Provides a description of the instance.
    */
   public String getDescription() {
      return description;
   }

   /**
    * Provides an EC2 instance ID.
    */
   public String getInstanceId() {
      return instanceId;
   }

   /**
    * Provides information about the cause of OutOfService instances. Specifically, it indicates
    * whether the cause is Elastic Load Balancing or the instance behind the LoadBalancer.
    */
   public Optional<String> getReasonCode() {
      return reasonCode;
   }

   /**
    * Specifies the current status of the instance.
    */
   public String getState() {
      return state;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(description, instanceId, reasonCode, state);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      InstanceHealth other = InstanceHealth.class.cast(obj);
      return Objects.equal(this.description, other.description) && Objects.equal(this.instanceId, other.instanceId)
               && Objects.equal(this.reasonCode, other.reasonCode) && Objects.equal(this.state, other.state);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("description", description)
               .add("instanceId", instanceId).add("reasonCode", reasonCode.orNull()).add("state", state).toString();
   }

}
