/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.domain.network;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.AbiquoAsyncApi;
import org.jclouds.abiquo.domain.DomainWrapper;
import org.jclouds.abiquo.domain.infrastructure.Datacenter;
import org.jclouds.abiquo.reference.ValidationErrors;
import org.jclouds.rest.RestContext;

import com.abiquo.server.core.infrastructure.network.NetworkServiceTypeDto;

/**
 * Network Service Type defines a network service.
 * 
 * The network Service Type is used to select the network interface of the
 * target host where a NIC in the virtual machine will be attached.
 * 
 * They are scoped at {@link Datacenter} level: two {@link NetworkServiceType}
 * can have the same name if they belong to a different {@link Datacenter}
 * 
 * @author Jaume Devesa
 */
public class NetworkServiceType extends DomainWrapper<NetworkServiceTypeDto> {
   public static Builder builder(final RestContext<AbiquoApi, AbiquoAsyncApi> context, final Datacenter datacenter) {
      return new Builder(context, datacenter);
   }

   /**
    * Helper class to build {@link NetworkServiceType} in a controlled way.
    * 
    * @author Jaume Devesa
    */
   public static class Builder {
      private RestContext<AbiquoApi, AbiquoAsyncApi> context;

      private Datacenter datacenter;

      private String name;

      public Builder(final RestContext<AbiquoApi, AbiquoAsyncApi> context, final Datacenter datacenter) {
         super();
         checkNotNull(datacenter, ValidationErrors.NULL_RESOURCE + Datacenter.class);
         this.datacenter = datacenter;
         this.context = context;
      }

      public NetworkServiceType build() {
         NetworkServiceTypeDto dto = new NetworkServiceTypeDto();
         dto.setName(this.name);

         NetworkServiceType nst = new NetworkServiceType(context, dto);
         nst.datacenter = this.datacenter;
         return nst;
      }

      public Builder name(final String name) {
         this.name = checkNotNull(name, ValidationErrors.MISSING_REQUIRED_FIELD + name);
         return this;
      }
   }

   /** The datacenter where the NetworkServiceType belongs. */
   private Datacenter datacenter;

   /** Constructor will only be used by the builder. */
   protected NetworkServiceType(final RestContext<AbiquoApi, AbiquoAsyncApi> context, final NetworkServiceTypeDto target) {
      super(context, target);
   }

   /**
    * Delete the Network Service Type.
    */
   public void delete() {
      context.getApi().getInfrastructureApi().deleteNetworkServiceType(target);
      target = null;
   }

   /**
    * Create a new Network Service Type
    */
   public void save() {
      target = context.getApi().getInfrastructureApi().createNetworkServiceType(datacenter.unwrap(), target);
   }

   /**
    * Update Network Service Type information in the server with the data from
    * this NST.
    */
   public void update() {
      target = context.getApi().getInfrastructureApi().updateNetworkServiceType(target);
   }

   public Integer getId() {
      return target.getId();
   }

   public String getName() {
      return target.getName();
   }

   public Boolean isDefaultNST() {
      return target.isDefaultNST();
   }

   public void setName(final String name) {
      target.setName(name);
   }

   @Override
   public String toString() {
      return "NetworkServiceType [id=" + getId() + ", name=" + getName() + ", isDefault=" + isDefaultNST() + "]";
   }

}
