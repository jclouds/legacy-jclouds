/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.trmk.vcloud_0_8.domain.internal;

import java.net.URI;

import org.jclouds.trmk.vcloud_0_8.domain.FenceMode;
import org.jclouds.trmk.vcloud_0_8.domain.Network;
import org.jclouds.trmk.vcloud_0_8.domain.ReferenceType;

/**
 * Locations of resources in vCloud
 * 
 * @author Adrian Cole
 * 
 */
public class NetworkImpl extends ReferenceTypeImpl implements Network {

   protected final String description;
   protected final String gateway;
   protected final String netmask;
   protected final FenceMode fenceMode;
   protected final ReferenceType networkExtension;
   protected final ReferenceType ips;

   public NetworkImpl(String name, String type, URI id, String description, String gateway, String netmask,
         FenceMode fenceMode, ReferenceType networkExtension, ReferenceType ips) {
      super(name, type, id);
      this.description = description;
      this.gateway = gateway;
      this.netmask = netmask;
      this.fenceMode = fenceMode;
      this.networkExtension = networkExtension;
      this.ips = ips;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getDescription() {
      return description;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getGateway() {
      return gateway;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getNetmask() {
      return netmask;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public FenceMode getFenceMode() {
      return fenceMode;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ReferenceType getIps() {
      return ips;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ReferenceType getNetworkExtension() {
      return networkExtension;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int compareTo(ReferenceType o) {
      return (this == o) ? 0 : getHref().compareTo(o.getHref());
   }

}
