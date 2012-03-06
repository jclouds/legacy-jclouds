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

import java.net.URI;
import java.util.Set;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.Sets;

@XmlRootElement(name = "NetworkType")
public class NetworkType<T extends NetworkType<T>> extends EntityType<T> {

   public static <T extends NetworkType<T>> Builder<T> builder() {
      return new Builder<T>();
   }

   @Override
   public Builder<T> toBuilder() {
      return new Builder<T>().fromNetworkType(this);
   }

   public static class Builder<T extends NetworkType<T>> extends EntityType.Builder<T> {

      protected NetworkConfiguration networkConfiguration;

      /**
       * @see NetworkType#getConfiguration()
       */
      public Builder<T> configuration(NetworkConfiguration networkConfiguration) {
         this.networkConfiguration = networkConfiguration;
         return this;
      }

      @Override
      public NetworkType<T> build() {
         return new NetworkType<T>(href, type, links, description, tasksInProgress, id, name, networkConfiguration);
      }

      /**
       * @see EntityType#getName()
       */
      @Override
      public Builder<T> name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @see EntityType#getDescription()
       */
      @Override
      public Builder<T> description(String description) {
         this.description = description;
         return this;
      }

      /**
       * @see EntityType#getId()
       */
      @Override
      public Builder<T> id(String id) {
         this.id = id;
         return this;
      }

      /**
       * @see EntityType#getTasksInProgress()
       */
      @Override
      public Builder<T> tasksInProgress(TasksInProgress tasksInProgress) {
         this.tasksInProgress = tasksInProgress;
         return this;
      }

      /**
       * @see ReferenceType#getHref()
       */
      @Override
      public Builder<T> href(URI href) {
         this.href = href;
         return this;
      }

      /**
       * @see ReferenceType#getType()
       */
      @Override
      public Builder<T> type(String type) {
         this.type = type;
         return this;
      }

      /**
       * @see EntityType#getLinks()
       */
      @Override
      public Builder<T> links(Set<Link> links) {
         this.links = Sets.newLinkedHashSet(checkNotNull(links, "links"));
         return this;
      }

      /**
       * @see EntityType#getLinks()
       */
      @Override
      public Builder<T> link(Link link) {
         this.links.add(checkNotNull(link, "link"));
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @SuppressWarnings("unchecked")
      @Override
      public Builder<T> fromEntityType(EntityType<T> in) {
         return Builder.class.cast(super.fromEntityType(in));
      }

      public Builder<T> fromNetworkType(NetworkType<T> in) {
         return fromEntityType(in).configuration(in.getConfiguration());
      }
   }

   public NetworkType(URI href, String type, Set<Link> links, String description, TasksInProgress tasksInProgress,
                      String id, String name, NetworkConfiguration networkConfiguration) {
      super(href, type, links, description, tasksInProgress, id, name);
      this.networkConfiguration = networkConfiguration;
   }

   protected NetworkType() {
      // for JAXB
   }

   @XmlElement(name = "Configuration")
   private NetworkConfiguration networkConfiguration;

   /**
    * @return optional configuration
    */
   public NetworkConfiguration getConfiguration() {
      return networkConfiguration;
   }

   @Override
   public boolean equals(Object o) {
      if (!super.equals(o))
         return false;
      NetworkType<?> that = NetworkType.class.cast(o);
      return super.equals(that) && equal(networkConfiguration, that.networkConfiguration);
   }

   @Override
   public int hashCode() {
      return super.hashCode() + Objects.hashCode(networkConfiguration);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("configuration", networkConfiguration);
   }

}
