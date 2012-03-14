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

package org.jclouds.vcloud.director.v1_5.domain;

import static com.google.common.base.Objects.*;
import static com.google.common.base.Preconditions.*;

import java.net.URI;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.Sets;

/**
 * Represents a vApp network.
 *
 * <pre>
 * &lt;complexType name="VAppNetwork" /&gt;
 * </pre>
 */
@XmlType(name = "VAppNetwork")
public class VAppNetwork extends NetworkType<VAppNetwork> {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return new Builder().fromVAppNetwork(this);
   }

   public static class Builder extends NetworkType.Builder<VAppNetwork> {

      private Boolean deployed;

      /**
       * @see VAppNetwork#isDeployed()
       */
      public Builder isDeployed(Boolean deployed) {
         this.deployed = deployed;
         return this;
      }

      /**
       * @see VAppNetwork#isDeployed()
       */
      public Builder deployed() {
         this.deployed = Boolean.TRUE;
         return this;
      }

      /**
       * @see VAppNetwork#isDeployed()
       */
      public Builder notDeployed() {
         this.deployed = Boolean.FALSE;
         return this;
      }

      @Override
      public VAppNetwork build() {
         VAppNetwork vAppNetwork = new VAppNetwork(href, type, links, description, tasks, id, name, networkConfiguration);
         vAppNetwork.deployed = deployed;
         return vAppNetwork;
      }

      /**
       * @see NetworkType#getConfiguration()
       */
      @Override
      public Builder configuration(NetworkConfiguration networkConfiguration) {
         this.networkConfiguration = networkConfiguration;
         return this;
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
       * @see EntityType#getTasks()
       */
      @Override
      public Builder tasks(Set<Task> tasks) {
         if (checkNotNull(tasks, "tasks").size() > 0)
            this.tasks = Sets.newLinkedHashSet(tasks);
         return this;
      }

      /**
       * @see EntityType#getTasks()
       */
      @Override
      public Builder task(Task task) {
         if (tasks == null)
            tasks = Sets.newLinkedHashSet();
         this.tasks.add(checkNotNull(task, "task"));
         return this;
      }

      /**
       * @see ResourceType#getHref()
       */
      @Override
      public Builder href(URI href) {
         this.href = href;
         return this;
      }

      /**
       * @see ResourceType#getType()
       */
      @Override
      public Builder type(String type) {
         this.type = type;
         return this;
      }

      /**
       * @see ResourceType#getLinks()
       */
      @Override
      public Builder links(Set<Link> links) {
         if (checkNotNull(links, "links").size() > 0)
            this.links = Sets.newLinkedHashSet(links);
         return this;
      }

      /**
       * @see ResourceType#getLinks()
       */
      @Override
      public Builder link(Link link) {
         if (links == null)
            links = Sets.newLinkedHashSet();
         this.links.add(checkNotNull(link, "link"));
         return this;
      }

      @Override
      public Builder fromNetworkType(NetworkType<VAppNetwork> in) {
         return Builder.class.cast(super.fromNetworkType(in));
      }

      public Builder fromVAppNetwork(VAppNetwork in) {
         return fromNetworkType(in).isDeployed(in.isDeployed());
      }
   }

   protected VAppNetwork() {
      // For JAXB and builder use
   }

   public VAppNetwork(URI href, String type, @Nullable Set<Link> links, String description, @Nullable Set<Task> tasks, String id, String name, NetworkConfiguration networkConfiguration) {
      super(href, type, links, description, tasks, id, name, networkConfiguration);
   }

   @XmlAttribute
   protected Boolean deployed;

   /**
    * Gets the value of the deployed property.
    */
   public Boolean isDeployed() {
      return deployed;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      VAppNetwork that = VAppNetwork.class.cast(o);
      return super.equals(that) && equal(this.deployed, that.deployed);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), deployed);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("deployed", deployed);
   }
}
