/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 *(Link.builder().regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless(Link.builder().required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.vcloud.director.v1_5.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.NS;

import java.net.URI;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.jclouds.ovf.Network;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.Sets;

@XmlRootElement(namespace = NS, name = "OrgNetwork")
public class OrgNetwork extends EntityType<OrgNetwork> {
   
   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return new Builder().fromOrgNetwork(this);
   }

   public static class Builder extends EntityType.Builder<OrgNetwork> {

      private NetworkConfiguration networkConfiguration;
      private ReferenceType<?> networkPool;
      private IpAddresses allowedExternalIpAddresses;
      
      /**
       * @see Network#getConfiguration()
       */
      public Builder configuration(NetworkConfiguration networkConfiguration) {
         this.networkConfiguration = networkConfiguration;
         return this;
      }
      
      /**
       * @see Network#getNetworkPool()
       */
      public Builder networkPool(ReferenceType<?> networkPool) {
         this.networkPool = networkPool;
         return this;
      }

      /**
       * @see Network#getAllowedExternalIpAddresses()
       */
      public Builder allowedExternalIpAddresses(IpAddresses allowedExternalIpAddresses) {
         this.allowedExternalIpAddresses = allowedExternalIpAddresses;
         return this;
      }
      
      @Override
      public OrgNetwork build() {
         OrgNetwork network = new OrgNetwork(href, name);
         network.setConfiguration(networkConfiguration);
         network.setNetworkPool(networkPool);
         network.setAllowedExternalIpAddresses(allowedExternalIpAddresses);
         network.setDescription(description);
         network.setId(id);
         network.setType(type);
         network.setLinks(links);
         network.setTasksInProgress(tasksInProgress);
         return network;
      }

      /**
       * @see EntityType#getName()
       */
      @Override
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @see EntityType#getDescription()
       */
      @Override
      public Builder description(String description) {
         this.description = description;
         return this;
      }

      /**
       * @see EntityType#getId()
       */
      @Override
      public Builder id(String id) {
         this.id = id;
         return this;
      }

      /**
       * @see EntityType#getTasksInProgress()
       */
      @Override
      public Builder tasksInProgress(TasksInProgress tasksInProgress) {
         this.tasksInProgress = tasksInProgress;
         return this;
      }

      /**
       * @see ReferenceType#getHref()
       */
      @Override
      public Builder href(URI href) {
         this.href = href;
         return this;
      }

      /**
       * @see ReferenceType#getType()
       */
      @Override
      public Builder type(String type) {
         this.type = type;
         return this;
      }

      /**
       * @see ReferenceType#getLinks()
       */
      @Override
      public Builder links(Set<Link> links) {
         this.links = Sets.newLinkedHashSet(checkNotNull(links, "links"));
         return this;
      }

      /**
       * @see ReferenceType#getLinks()
       */
      @Override
      public Builder link(Link link) {
         this.links.add(checkNotNull(link, "link"));
         return this;
      }

      @Override
      public Builder fromEntityType(EntityType<OrgNetwork> in) {
         return Builder.class.cast(super.fromEntityType(in));
      }

      public Builder fromOrgNetwork(OrgNetwork in) {
         return fromEntityType(in).configuration(in.getConfiguration())
               .networkPool(in.getNetworkPool())
               .allowedExternalIpAddresses(in.getAllowedExternalIpAddresses());
      }
   }

   private OrgNetwork() {
      // For JAXB and builder use
   }
   
   private OrgNetwork(URI href, String name) {
      super(href, name);
   }

   @XmlElement(namespace = NS, name = "Configuration")
   private NetworkConfiguration networkConfiguration;
   @XmlElement(namespace = NS, name = "NetworkPool")
   private ReferenceType<?> networkPool;
   @XmlElement(namespace = NS, name = "AllowedExternalIpAddresses")
   private IpAddresses allowedExternalIpAddresses;

   /**
    * @return optional configuration
    */
   public NetworkConfiguration getConfiguration() {
      return networkConfiguration;
   }
   
   public void setConfiguration(NetworkConfiguration networkConfiguration) {
      this.networkConfiguration = networkConfiguration;
   }
   
   /**
    * @return optional network pool
    */
   public ReferenceType<?> getNetworkPool() {
      return networkPool;
   }
   
   public void setNetworkPool(ReferenceType<?> networkPool) {
      this.networkPool = networkPool;
   }
   
   /**
    * @return optional network pool
    */
   public IpAddresses getAllowedExternalIpAddresses() {
      return allowedExternalIpAddresses;
   }
   
   public void setAllowedExternalIpAddresses(IpAddresses allowedExternalIpAddresses) {
      this.allowedExternalIpAddresses = allowedExternalIpAddresses;
   }
   
   @Override
   public boolean equals(Object o) {
      if (!super.equals(o))
         return false;
      OrgNetwork that = OrgNetwork.class.cast(o);
      return super.equals(that) && equal(networkConfiguration, that.networkConfiguration) &&
            equal(networkPool, that.networkPool) && 
            equal(allowedExternalIpAddresses, that.allowedExternalIpAddresses);
   }

   @Override
   public int hashCode() {
      return super.hashCode() + Objects.hashCode(networkConfiguration, 
            networkPool, allowedExternalIpAddresses);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("configuration", networkConfiguration).add("networkPool", networkPool)
            .add("allowedExternalIpAddresses", allowedExternalIpAddresses);
   }

}
