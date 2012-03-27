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
package org.jclouds.vcloud.domain.network.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.List;
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.internal.ReferenceTypeImpl;
import org.jclouds.vcloud.domain.network.Features;
import org.jclouds.vcloud.domain.network.FenceMode;
import org.jclouds.vcloud.domain.network.IpScope;
import org.jclouds.vcloud.domain.network.OrgNetwork;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * 
 * @author Adrian Cole
 */
public class OrgNetworkImpl extends ReferenceTypeImpl implements OrgNetwork {
   @Nullable
   private final ReferenceType org;
   @Nullable
   private final String description;
   private final List<Task> tasks = Lists.newArrayList();
   private final Configuration configuration;
   @Nullable
   private final ReferenceType networkPool;
   private final Set<String> allowedExternalIpAddresses = Sets.newLinkedHashSet();

   public OrgNetworkImpl(String name, String type, URI id, @Nullable ReferenceType org, @Nullable String description,
            Iterable<Task> tasks, Configuration configuration, @Nullable ReferenceType networkPool,
            Iterable<String> allowedExternalIpAddresses) {
      super(name, type, id);
      this.org = org;
      this.description = description;
      Iterables.addAll(this.tasks, checkNotNull(tasks, "tasks"));
      this.configuration = checkNotNull(configuration, "configuration");
      this.networkPool = networkPool;
      Iterables.addAll(this.allowedExternalIpAddresses, checkNotNull(allowedExternalIpAddresses,
               "allowedExternalIpAddresses"));
   }

   public static class ConfigurationImpl implements Configuration {

      @Nullable
      private final IpScope ipScope;
      @Nullable
      private final ReferenceType parentNetwork;
      private final FenceMode fenceMode;
      private final Features features;

      public ConfigurationImpl(@Nullable IpScope ipScope, @Nullable ReferenceType parentNetwork, FenceMode fenceMode,
               @Nullable Features features) {
         this.ipScope = ipScope;
         this.parentNetwork = parentNetwork;
         this.fenceMode = checkNotNull(fenceMode, "fenceMode");
         this.features = features;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public IpScope getIpScope() {
         return ipScope;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public ReferenceType getParentNetwork() {
         return parentNetwork;
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
      @Nullable
      public Features getFeatures() {
         return features;
      }

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + ((features == null) ? 0 : features.hashCode());
         result = prime * result + ((fenceMode == null) ? 0 : fenceMode.hashCode());
         result = prime * result + ((ipScope == null) ? 0 : ipScope.hashCode());
         result = prime * result + ((parentNetwork == null) ? 0 : parentNetwork.hashCode());
         return result;
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj)
            return true;
         if (obj == null)
            return false;
         if (getClass() != obj.getClass())
            return false;
         ConfigurationImpl other = (ConfigurationImpl) obj;
         if (features == null) {
            if (other.features != null)
               return false;
         } else if (!features.equals(other.features))
            return false;
         if (fenceMode == null) {
            if (other.fenceMode != null)
               return false;
         } else if (!fenceMode.equals(other.fenceMode))
            return false;
         if (ipScope == null) {
            if (other.ipScope != null)
               return false;
         } else if (!ipScope.equals(other.ipScope))
            return false;
         if (parentNetwork == null) {
            if (other.parentNetwork != null)
               return false;
         } else if (!parentNetwork.equals(other.parentNetwork))
            return false;
         return true;
      }

      @Override
      public String toString() {
         return "[features=" + features + ", fenceMode=" + fenceMode + ", ipScope=" + ipScope + ", parentNetwork="
                  + parentNetwork + "]";
      }

   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ReferenceType getOrg() {
      return org;
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
   public List<Task> getTasks() {
      return tasks;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Configuration getConfiguration() {
      return configuration;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ReferenceType getNetworkPool() {
      return networkPool;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Set<String> getAllowedExternalIpAddresses() {
      return allowedExternalIpAddresses;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((allowedExternalIpAddresses == null) ? 0 : allowedExternalIpAddresses.hashCode());
      result = prime * result + ((configuration == null) ? 0 : configuration.hashCode());
      result = prime * result + ((description == null) ? 0 : description.hashCode());
      result = prime * result + ((networkPool == null) ? 0 : networkPool.hashCode());
      result = prime * result + ((org == null) ? 0 : org.hashCode());
      result = prime * result + ((tasks == null) ? 0 : tasks.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      OrgNetworkImpl other = (OrgNetworkImpl) obj;
      if (allowedExternalIpAddresses == null) {
         if (other.allowedExternalIpAddresses != null)
            return false;
      } else if (!allowedExternalIpAddresses.equals(other.allowedExternalIpAddresses))
         return false;
      if (configuration == null) {
         if (other.configuration != null)
            return false;
      } else if (!configuration.equals(other.configuration))
         return false;
      if (description == null) {
         if (other.description != null)
            return false;
      } else if (!description.equals(other.description))
         return false;
      if (networkPool == null) {
         if (other.networkPool != null)
            return false;
      } else if (!networkPool.equals(other.networkPool))
         return false;
      if (org == null) {
         if (other.org != null)
            return false;
      } else if (!org.equals(other.org))
         return false;
      if (tasks == null) {
         if (other.tasks != null)
            return false;
      } else if (!tasks.equals(other.tasks))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[allowedExternalIpAddresses=" + allowedExternalIpAddresses + ", configuration=" + configuration
               + ", description=" + description + ", networkPool=" + networkPool + ", org=" + org + ", tasks=" + tasks
               + "]";
   }

}