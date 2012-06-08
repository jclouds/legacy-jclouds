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
package org.jclouds.openstack.nova.v2_0.domain;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.gson.annotations.SerializedName;

/**
 * Additional attributes delivered by Extended Server Attributes extension (alias "OS-EXT-SRV-ATTR")
 *
 * @author Adam Lowe
 * @see <a href=
 *        "http://nova.openstack.org/api/nova.api.openstack.compute.contrib.extended_server_attributes.html"
 *       />
 * @see org.jclouds.openstack.nova.v2_0.features.ExtensionClient#getExtensionByAlias
 * @see org.jclouds.openstack.nova.v1_1.extensions.ExtensionNamespaces#EXTENDED_STATUS (extended status?)
 */
public class ServerExtendedAttributes {
   public static final String PREFIX = "OS-EXT-SRV-ATTR:";

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromServerExtraAttributes(this);
   }

   public static abstract class Builder<T extends Builder<T>>  {
      protected abstract T self();

      private String instanceName;
      private String hostName;
      private String hypervisorHostName;

      /**
       * @see ServerExtendedAttributes#getInstanceName()
       */
      public T instanceName(String instanceName) {
         this.instanceName = instanceName;
         return self();
      }

      /**
       * @see ServerExtendedAttributes#getHostName()
       */
      public T hostName(String hostName) {
         this.hostName = hostName;
         return self();
      }

      /**
       * @see ServerExtendedAttributes#getHypervisorHostName()
       */
      public T hypervisorHostame(String hypervisorHostName) {
         this.hypervisorHostName = hypervisorHostName;
         return self();
      }

      public ServerExtendedAttributes build() {
         return new ServerExtendedAttributes(this);
      }

      public T fromServerExtraAttributes(ServerExtendedAttributes in) {
         return this
               .instanceName(in.getInstanceName())
               .hostName(in.getHostName())
               .hypervisorHostame(in.getHypervisorHostName());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }
   
   protected ServerExtendedAttributes() {
      // we want serializers like Gson to work w/o using sun.misc.Unsafe,
      // prohibited in GAE. This also implies fields are not final.
      // see http://code.google.com/p/jclouds/issues/detail?id=925
   }
  
   @SerializedName(value=PREFIX + "instance_name")
   private String instanceName;
   @SerializedName(value=PREFIX + "host")
   private String hostName;
   @SerializedName(value=PREFIX + "hypervisor_hostname")
   private String hypervisorHostName;

   protected ServerExtendedAttributes(Builder<?> builder) {
      this.instanceName = builder.instanceName;
      this.hostName = builder.hostName;
      this.hypervisorHostName = builder.hypervisorHostName;
   }

   public String getInstanceName() {
      return this.instanceName;
   }

   public String getHostName() {
      return this.hostName;
   }

   public String getHypervisorHostName() {
      return this.hypervisorHostName;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(instanceName, hostName, hypervisorHostName);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      ServerExtendedAttributes that = ServerExtendedAttributes.class.cast(obj);
      return Objects.equal(this.instanceName, that.instanceName)
            && Objects.equal(this.hostName, that.hostName)
            && Objects.equal(this.hypervisorHostName, that.hypervisorHostName);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("")
            .add("instanceName", instanceName)
            .add("hostName", hostName)
            .add("hypervisorHostName", hypervisorHostName);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}