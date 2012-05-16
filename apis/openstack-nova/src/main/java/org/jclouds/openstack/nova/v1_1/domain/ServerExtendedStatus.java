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
package org.jclouds.openstack.nova.v1_1.domain;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.gson.annotations.SerializedName;

/**
 * Additional attributes delivered by Extended Server Status extension (alias "OS-EXT-STS")
 *
 * @author Adam Lowe
 * @see <a href=
 *       "http://nova.openstack.org/api/nova.api.openstack.compute.contrib.extended_status.html"
 *       />
 * @see org.jclouds.openstack.nova.v1_1.features.ExtensionClient#getExtensionByAlias
 * @see org.jclouds.openstack.nova.v1_1.extensions.ExtensionNamespaces#EXTENDED_STATUS (extended status?)
 */
public class ServerExtendedStatus {
   public static final String PREFIX = "OS-EXT-STS:";

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromServerExtendedStatus(this);
   }

   public static abstract class Builder<T extends Builder<T>>  {
      protected abstract T self();

      private String taskState;
      private String vmState;
      private int powerState = Integer.MIN_VALUE;

      /**
       * @see ServerExtendedStatus#getTaskState()
       */
      public T taskState(String taskState) {
         this.taskState = taskState;
         return self();
      }

      /**
       * @see ServerExtendedStatus#getVmState()
       */
      public T vmState(String vmState) {
         this.vmState = vmState;
         return self();
      }

      /**
       * @see ServerExtendedStatus#getPowerState()
       */
      public T powerState(int powerState) {
         this.powerState = powerState;
         return self();
      }

      public ServerExtendedStatus build() {
         return new ServerExtendedStatus(this);
      }

      public T fromServerExtendedStatus(ServerExtendedStatus in) {
         return this
               .taskState(in.getTaskState())
               .vmState(in.getVmState())
               .powerState(in.getPowerState());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }
   
   protected ServerExtendedStatus() {
      // we want serializers like Gson to work w/o using sun.misc.Unsafe,
      // prohibited in GAE. This also implies fields are not final.
      // see http://code.google.com/p/jclouds/issues/detail?id=925
   }
  
   @SerializedName(value=PREFIX + "task_state")
   private String taskState;
   @SerializedName(value=PREFIX + "vm_state")
   private String vmState;
   @SerializedName(value=PREFIX + "power_state")
   private int powerState = Integer.MIN_VALUE;

   protected ServerExtendedStatus(Builder<?> builder) {
      this.taskState = builder.taskState;
      this.vmState = builder.vmState;
      this.powerState = builder.powerState;
   }
   
   @Nullable
   public String getTaskState() {
      return this.taskState;
   }

   @Nullable
   public String getVmState() {
      return this.vmState;
   }

   public int getPowerState() {
      return this.powerState;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(taskState, vmState, powerState);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      ServerExtendedStatus that = ServerExtendedStatus.class.cast(obj);
      return Objects.equal(this.taskState, that.taskState)
            && Objects.equal(this.vmState, that.vmState)
            && Objects.equal(this.powerState, that.powerState)
            ;
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("")
            .add("taskState", taskState)
            .add("vmState", vmState)
            .add("powerState", powerState)
            ;
   }

   @Override
   public String toString() {
      return string().toString();
   }

}