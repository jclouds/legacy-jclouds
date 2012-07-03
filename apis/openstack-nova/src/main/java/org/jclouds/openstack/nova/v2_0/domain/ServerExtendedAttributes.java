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
package org.jclouds.openstack.nova.v2_0.domain;

import java.beans.ConstructorProperties;

import javax.inject.Named;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Additional attributes delivered by Extended Server Attributes extension (alias "OS-EXT-SRV-ATTR")
 * 
 * @author Adam Lowe
 * @see <a href=
        "http://nova.openstack.org/api/nova.api.openstack.compute.contrib.extended_server_attributes.html"
       />
 * @see org.jclouds.openstack.nova.v2_0.features.ExtensionClient#getExtensionByAlias
 * @see org.jclouds.openstack.nova.v2_0.extensions.ExtensionNamespaces#EXTENDED_STATUS
*/
public class ServerExtendedAttributes {

   public static String PREFIX;

   public static Builder<?> builder() { 
      return new ConcreteBuilder();
   }
   
   public Builder<?> toBuilder() { 
      return new ConcreteBuilder().fromServerExtendedAttributes(this);
   }

   public static abstract class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected String instanceName;
      protected String hostName;
      protected String hypervisorHostName;
   
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
      public T hypervisorHostName(String hypervisorHostName) {
         this.hypervisorHostName = hypervisorHostName;
         return self();
      }

      public ServerExtendedAttributes build() {
         return new ServerExtendedAttributes(instanceName, hostName, hypervisorHostName);
      }
      
      public T fromServerExtendedAttributes(ServerExtendedAttributes in) {
         return this
                  .instanceName(in.getInstanceName())
                  .hostName(in.getHostName())
                  .hypervisorHostName(in.getHypervisorHostName());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   @Named("OS-EXT-SRV-ATTR:instance_name")
   private final String instanceName;
   @Named("OS-EXT-SRV-ATTR:host")
   private final String hostName;
   @Named("OS-EXT-SRV-ATTR:hypervisor_hostname")
   private final String hypervisorHostName;

   @ConstructorProperties({
      "OS-EXT-SRV-ATTR:instance_name", "OS-EXT-SRV-ATTR:host", "OS-EXT-SRV-ATTR:hypervisor_hostname"
   })
   protected ServerExtendedAttributes(@Nullable String instanceName, @Nullable String hostName, @Nullable String hypervisorHostName) {
      this.instanceName = instanceName;
      this.hostName = hostName;
      this.hypervisorHostName = hypervisorHostName;
   }

   @Nullable
   public String getInstanceName() {
      return this.instanceName;
   }

   @Nullable
   public String getHostName() {
      return this.hostName;
   }

   @Nullable
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
      return Objects.toStringHelper(this)
            .add("instanceName", instanceName).add("hostName", hostName).add("hypervisorHostName", hypervisorHostName);
   }
   
   @Override
   public String toString() {
      return string().toString();
   }

}
