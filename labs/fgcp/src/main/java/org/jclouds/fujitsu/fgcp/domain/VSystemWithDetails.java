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
package org.jclouds.fujitsu.fgcp.domain;

import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Represents a virtual system with servers, additional storage, public IP
 * addresses and networks.
 * 
 * @author Dies Koper
 */
@XmlRootElement(name = "vsys")
public class VSystemWithDetails extends VSystem {
   @XmlElementWrapper(name = "vservers")
   @XmlElement(name = "vserver")
   private Set<VServerWithVNICs> servers = Sets.newLinkedHashSet();
   @XmlElementWrapper(name = "vdisks")
   @XmlElement(name = "vdisk")
   private Set<VDisk> disks = Sets.newLinkedHashSet();
   @XmlElementWrapper(name = "publicips")
   @XmlElement(name = "publicip")
   private Set<PublicIP> publicips = Sets.newLinkedHashSet();
   @XmlElementWrapper(name = "vnets")
   @XmlElement(name = "vnet")
   private Set<VNet> networks = Sets.newLinkedHashSet();

   public Set<VServerWithVNICs> getServers() {
      return servers == null ? ImmutableSet.<VServerWithVNICs> of() : ImmutableSet
            .copyOf(servers);
   }

   public Set<VDisk> getDisks() {
      return disks == null ? ImmutableSet.<VDisk> of() : ImmutableSet
            .copyOf(disks);
   }

   public Set<PublicIP> getPublicips() {
      return publicips == null ? ImmutableSet.<PublicIP> of() : ImmutableSet
            .copyOf(publicips);
   }

   public Set<VNet> getNetworks() {
      return networks == null ? ImmutableSet.<VNet> of() : ImmutableSet
            .copyOf(networks);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("id", id)
            .add("name", name).add("creator", creator)
            .add("template", template).add("description", description)
            .add("disks", disks).add("networks", networks)
            .add("publicips", publicips).add("servers", servers).toString();
   }
}
