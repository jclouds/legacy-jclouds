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

import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.AbiquoAsyncApi;
import org.jclouds.abiquo.domain.DomainWrapper;
import org.jclouds.rest.RestContext;

import com.abiquo.model.enumerator.NetworkType;
import com.abiquo.server.core.infrastructure.network.AbstractIpDto;

/**
 * Adds generic high level functionality to {@link AbstractIpDto}.
 * 
 * @author Ignasi Barrera
 * @author Francesc Montserrat
 */
public abstract class Ip<T extends AbstractIpDto, N extends Network<?>> extends DomainWrapper<T> {
   /**
    * Constructor to be used only by the builder.
    */
   protected Ip(final RestContext<AbiquoApi, AbiquoAsyncApi> context, final T target) {
      super(context, target);
   }

   // Domain operations

   public abstract N getNetwork();

   public abstract NetworkType getNetworkType();

   // Delegate methods

   public Integer getId() {
      return target.getId();
   }

   public String getIp() {
      return target.getIp();
   }

   public String getMac() {
      return target.getMac();
   }

   public String getName() {
      return target.getName();
   }

   public String getNetworkName() {
      return target.getNetworkName();
   }

   @Override
   public String toString() {
      return "Ip [id=" + getId() + ", ip=" + getIp() + ", mac=" + getMac() + ", name=" + getName() + ", networkName="
            + getNetworkName() + "]";
   }
}
