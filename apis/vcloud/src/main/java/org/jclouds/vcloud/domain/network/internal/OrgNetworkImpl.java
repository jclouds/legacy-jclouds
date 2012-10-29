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

import static com.google.common.base.Objects.equal;
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

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
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
      public boolean equals(Object o) {
         if (this == o)
            return true;
         if (o == null || getClass() != o.getClass())
            return false;
         ConfigurationImpl that = ConfigurationImpl.class.cast(o);
         return equal(this.ipScope, that.ipScope) && equal(this.parentNetwork, that.parentNetwork)
               && equal(this.fenceMode, that.fenceMode) && equal(this.features, that.features);
      }

      @Override
      public int hashCode() {
         return Objects.hashCode(ipScope, parentNetwork, fenceMode, features);
      }

      @Override
      public String toString() {
         return Objects.toStringHelper("").omitNullValues().add("ipScope", ipScope).add("parentNetwork", parentNetwork)
               .add("fenceMode", fenceMode).add("features", features).toString();
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
   public ToStringHelper string() {
      ToStringHelper helper = super.string().add("org", org).add("description", description)
            .add("configuration", configuration).add("networkPool", networkPool);
      if (allowedExternalIpAddresses.size() > 0)
         helper.add("allowedExternalIpAddresses", allowedExternalIpAddresses);
      if (tasks.size() > 0)
         helper.add("tasks", tasks);
      return helper;
   }

}
