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
package org.jclouds.vcloud.options;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import org.jclouds.vcloud.domain.network.NetworkConfig;

import com.google.common.collect.Sets;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class InstantiateVAppTemplateOptions {
   private Set<NetworkConfig> networkConfig = Sets.newLinkedHashSet();

   private Boolean customizeOnInstantiate;
   private String description = null;
   private boolean deploy = true;
   private boolean powerOn = true;

   public String getDescription() {
      return description;
   }
   
   public boolean shouldDeploy() {
      return deploy;
   }

   public boolean shouldPowerOn() {
      return powerOn;
   }

   /**
    * Optional description. Used for the Description of the vApp created by this
    * instantiation.
    */
   public InstantiateVAppTemplateOptions description(String description) {
      this.description = description;
      return this;
   }

   /**
    * deploy the vapp after it is instantiated?
    */
   public InstantiateVAppTemplateOptions deploy(boolean deploy) {
      this.deploy = deploy;
      return this;
   }

   /**
    * powerOn the vapp after it is instantiated?
    */
   public InstantiateVAppTemplateOptions powerOn(boolean powerOn) {
      this.powerOn = powerOn;
      return this;
   }
   
   /**
    * {@networkConfig VAppTemplate}s have internal networks that can be
    * connected in order to access the internet or other external networks.
    * 
    * <h4>default behaviour if you don't use this option</h4> By default, we
    * connect the first internal {@networkConfig
    * org.jclouds.vcloud.domain.VAppTemplate#getNetworkSection network in the
    * vapp template}to a default chosen from the org or specified via
    * {@networkConfig
    * org.jclouds.vcloud.reference.VCloudConstants#
    * PROPERTY_VCLOUD_DEFAULT_NETWORK} using the {@networkConfig
    *  org.jclouds.vcloud.domain.FenceMode#BRIDGED} or an
    * override set by the property {@networkConfig
    * org.jclouds.vcloud.reference.VCloudConstants#
    * PROPERTY_VCLOUD_DEFAULT_FENCEMODE}.
    */
   public InstantiateVAppTemplateOptions addNetworkConfig(NetworkConfig networkConfig) {
      this.networkConfig.add(checkNotNull(networkConfig, "networkConfig"));
      return this;
   }

   public Set<NetworkConfig> getNetworkConfig() {
      return networkConfig;
   }

   public static class Builder {

      /**
       * @see InstantiateVAppTemplateOptions#description
       */
      public static InstantiateVAppTemplateOptions description(String description) {
         InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
         return options.description(description);
      }

      /**
       * @see InstantiateVAppTemplateOptions#deploy
       */
      public static InstantiateVAppTemplateOptions deploy(boolean deploy) {
         InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
         return options.deploy(deploy);
      }

      /**
       * @see InstantiateVAppTemplateOptions#powerOn
       */
      public static InstantiateVAppTemplateOptions powerOn(boolean powerOn) {
         InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
         return options.powerOn(powerOn);
      }

      /**
       * @see InstantiateVAppTemplateOptions#addNetworkConfig
       */
      public static InstantiateVAppTemplateOptions addNetworkConfig(NetworkConfig networkConfig) {
         InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
         return options.addNetworkConfig(networkConfig);
      }

   }

   @Override
   public String toString() {
      return "[networkConfig=" + networkConfig + ", customizeOnInstantiate=" + customizeOnInstantiate
            + ", description=" + description  + ", deploy=" + deploy + ", powerOn=" + powerOn + "]";
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((customizeOnInstantiate == null) ? 0 : customizeOnInstantiate.hashCode());
      result = prime * result + (deploy ? 1231 : 1237);
      result = prime * result + ((description == null) ? 0 : description.hashCode());
      result = prime * result + ((networkConfig == null) ? 0 : networkConfig.hashCode());
      result = prime * result + (powerOn ? 1231 : 1237);
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
      InstantiateVAppTemplateOptions other = (InstantiateVAppTemplateOptions) obj;
      if (customizeOnInstantiate == null) {
         if (other.customizeOnInstantiate != null)
            return false;
      } else if (!customizeOnInstantiate.equals(other.customizeOnInstantiate))
         return false;
      if (deploy != other.deploy)
         return false;
      if (description == null) {
         if (other.description != null)
            return false;
      } else if (!description.equals(other.description))
         return false;
      if (networkConfig == null) {
         if (other.networkConfig != null)
            return false;
      } else if (!networkConfig.equals(other.networkConfig))
         return false;
      if (powerOn != other.powerOn)
         return false;
      return true;
   }

}
